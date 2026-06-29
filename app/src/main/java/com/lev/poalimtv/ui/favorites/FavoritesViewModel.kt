package com.lev.poalimtv.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lev.poalimtv.data.local.FavoriteEntity
import com.lev.poalimtv.data.local.FavoritesDao
import com.lev.poalimtv.domain.model.MediaItem
import com.lev.poalimtv.domain.model.MediaType
import com.lev.poalimtv.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    dao: FavoritesDao,
) : ViewModel() {

    val favorites: StateFlow<UiState<List<MediaItem>>> = dao.getAllFavorites()
        .map<List<FavoriteEntity>, UiState<List<MediaItem>>> { entities ->
            UiState.Success(entities.map { it.toDomain() })
        }
        .catch { e ->
            emit(UiState.Error(e.message ?: "Failed to load favorites"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )
}

private fun FavoriteEntity.toDomain() = MediaItem(
    id = id,
    title = title,
    overview = "",
    posterPath = posterPath,
    backdropPath = null,
    rating = rating,
    releaseDate = null,
    mediaType = MediaType.valueOf(mediaType),
)
