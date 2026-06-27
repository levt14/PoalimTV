package com.lev.poalimtv.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lev.poalimtv.domain.model.MediaItem
import com.lev.poalimtv.domain.model.MediaType
import com.lev.poalimtv.ui.UiState
import com.lev.poalimtv.ui.components.MediaCard
import com.lev.poalimtv.ui.theme.PoalimTVTheme

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onMediaClick: (MediaItem) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val popularMovies by viewModel.popularMovies.collectAsState()
    val popularTvShows by viewModel.popularTvShows.collectAsState()

    HomeScreenContent(
        popularMovies = popularMovies,
        popularTvShows = popularTvShows,
        onSearchClick = onSearchClick,
        onFavoritesClick = onFavoritesClick,
        onMediaClick = onMediaClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    popularMovies: UiState<List<MediaItem>>,
    popularTvShows: UiState<List<MediaItem>>,
    onSearchClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onMediaClick: (MediaItem) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PoalimTV") },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = onFavoritesClick) {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favorites")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            MediaSection(title = "Popular Movies", uiState = popularMovies, onItemClick = onMediaClick)
            MediaSection(title = "Popular TV Shows", uiState = popularTvShows, onItemClick = onMediaClick)
        }
    }
}

@Composable
private fun MediaSection(
    title: String,
    uiState: UiState<List<MediaItem>>,
    onItemClick: (MediaItem) -> Unit,
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        when (uiState) {
            is UiState.Loading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(248.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            is UiState.Error -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            is UiState.Success -> LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(items = uiState.data, key = { it.id }) { item ->
                    MediaCard(item = item, onClick = { onItemClick(item) })
                }
            }
        }
    }
}

private val previewMovies = listOf(
    MediaItem(
        id = 1,
        title = "Dune: Part Two",
        overview = "Paul Atreides unites with Chani and the Fremen.",
        posterPath = null,
        backdropPath = null,
        rating = 8.3,
        releaseDate = "2024-03-01",
        mediaType = MediaType.MOVIE,
    ),
    MediaItem(
        id = 2,
        title = "Oppenheimer",
        overview = "The story of J. Robert Oppenheimer.",
        posterPath = null,
        backdropPath = null,
        rating = 8.9,
        releaseDate = "2023-07-21",
        mediaType = MediaType.MOVIE,
    ),
    MediaItem(
        id = 3,
        title = "Poor Things",
        overview = "The incredible tale about the fantastical evolution of Bella Baxter.",
        posterPath = null,
        backdropPath = null,
        rating = 7.8,
        releaseDate = "2023-12-08",
        mediaType = MediaType.MOVIE,
    ),
)

private val previewTvShows = listOf(
    MediaItem(
        id = 101,
        title = "The Bear",
        overview = "A young chef returns to run his family's sandwich shop.",
        posterPath = null,
        backdropPath = null,
        rating = 8.6,
        releaseDate = "2022-06-23",
        mediaType = MediaType.TV,
    ),
    MediaItem(
        id = 102,
        title = "Shogun",
        overview = "A European navigator becomes a samurai lord.",
        posterPath = null,
        backdropPath = null,
        rating = 8.8,
        releaseDate = "2024-02-27",
        mediaType = MediaType.TV,
    ),
    MediaItem(
        id = 103,
        title = "House of the Dragon",
        overview = "The story of House Targaryen.",
        posterPath = null,
        backdropPath = null,
        rating = 8.4,
        releaseDate = "2022-08-21",
        mediaType = MediaType.TV,
    ),
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenContentPreview() {
    PoalimTVTheme {
        HomeScreenContent(
            popularMovies = UiState.Success(previewMovies),
            popularTvShows = UiState.Success(previewTvShows),
            onSearchClick = {},
            onFavoritesClick = {},
            onMediaClick = {},
        )
    }
}
