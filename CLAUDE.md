# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

powershell
# Build debug APK
.\gradlew assembleDebug

# Build release APK
.\gradlew assembleRelease

# Run unit tests
.\gradlew test

# Run a single unit test class
.\gradlew test --tests "com.lev.poalimtv.ExampleUnitTest"

# Run instrumented tests (requires connected device or emulator)
.\gradlew connectedAndroidTest

# Lint check
.\gradlew lint

# Clean build
.\gradlew clean assembleDebug

## Architecture

Single-module Android app using Jetpack Compose with Material Design 3.

- Package: com.lev.poalimtv
- Min SDK: 26 (Android 8.0) | Target/Compile SDK: 36
- UI: Jetpack Compose + Material 3 (no View-based XML layouts)
- Theme: Dynamic colors on Android 12+, static purple/pink palette on older versions; RTL supported

### Key files

- app/src/main/java/com/lev/poalimtv/MainActivity.kt — single Activity, sets up Compose content with edge-to-edge display
- app/src/main/java/com/lev/poalimtv/ui/theme/ — Material 3 theme (Theme.kt), color tokens (Color.kt), typography (Type.kt)

### Dependency management

All library versions are centralized in gradle/libs.versions.toml (version catalog). When adding dependencies, add the version entry there and reference it via libs.* aliases in app/build.gradle.kts. The Compose BOM version (compose-bom) controls all androidx.compose.* library versions — do not pin individual Compose library versions separately.

### Test locations

- Unit tests: app/src/test/java/com/lev/poalimtv/
- Instrumented tests: app/src/androidTest/java/com/lev/poalimtv/

## Project Architecture Pattern

MVVM + Clean Architecture, three layers:

- ui/ — Compose screens + ViewModels + UiState
- domain/ — UseCases + clean domain models (Movie, TvShow, MediaItem)
- data/ — Repositories + Retrofit (remote) + Room (local)

### Package structure

com.lev.poalimtv
├── data
│   ├── local
│   │   ├── AppDatabase.kt
│   │   ├── dao/FavoritesDao.kt
│   │   └── entity/FavoriteEntity.kt
│   ├── remote
│   │   ├── TmdbApiService.kt
│   │   └── dto/
│   └── repository/
├── domain
│   ├── model/
│   └── usecase/
├── ui
│   ├── home/
│   ├── detail/
│   ├── search/
│   ├── favorites/
│   ├── navigation/
│   └── theme/
└── di/

### UiState sealed class (use everywhere)

sealed class UiState<out T> {
object Loading : UiState<Nothing>()
data class Success<T>(val data: T) : UiState<T>()
data class Error(val message: String) : UiState<Nothing>()
}

### Screens and ViewModels

- HomeViewModel — popularMovies + popularTvShows as StateFlow<UiState<List<MediaItem>>>
- SearchViewModel — debounced query 300ms, calls /search/multi endpoint
- DetailViewModel — fetches detail + trailer key + isFavorite: StateFlow<Boolean> from Room
- FavoritesViewModel — reads Room Flow only, no network calls

### Navigation

- home → composable
- search → dialog (modal over home)
- favorites → dialog (modal over home)
- detail/{mediaType}/{mediaId} → composable

### Image caching

Coil with diskCachePolicy enabled and CacheControl.maxAge(1, TimeUnit.DAYS)

### Unit tests

JUnit4 + MockK + Turbine. Priority: SearchViewModel debounce, FavoritesDao CRUD, MovieRepository DTO to domain mapping.

## Dependencies to add

hilt = "2.51"
retrofit = "2.11.0"
okhttp = "4.12.0"
coil = "2.6.0"
room = "2.6.1"
media3-exoplayer = "1.3.1"
mockk = "1.13.11"
turbine = "1.1.0"