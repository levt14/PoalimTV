package com.lev.poalimtv.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lev.poalimtv.data.repository.MovieRepository
import com.lev.poalimtv.data.repository.TvRepository
import com.lev.poalimtv.domain.model.MediaItem
import com.lev.poalimtv.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val tvRepository: TvRepository,
) : ViewModel() {

    private val _popularMovies = MutableStateFlow<UiState<List<MediaItem>>>(UiState.Loading)
    val popularMovies: StateFlow<UiState<List<MediaItem>>> = _popularMovies.asStateFlow()

    private val _popularTvShows = MutableStateFlow<UiState<List<MediaItem>>>(UiState.Loading)
    val popularTvShows: StateFlow<UiState<List<MediaItem>>> = _popularTvShows.asStateFlow()

    init {
        viewModelScope.launch { fetchMovies() }
        viewModelScope.launch { fetchTvShows() }
    }

    private suspend fun fetchMovies() {
        _popularMovies.value = UiState.Loading
        movieRepository.getPopularMovies()
            .onSuccess { _popularMovies.value = UiState.Success(it) }
            .onFailure { _popularMovies.value = UiState.Error(it.message ?: "Failed to load movies") }
    }

    private suspend fun fetchTvShows() {
        _popularTvShows.value = UiState.Loading
        tvRepository.getPopularTvShows()
            .onSuccess { _popularTvShows.value = UiState.Success(it) }
            .onFailure { _popularTvShows.value = UiState.Error(it.message ?: "Failed to load TV shows") }
    }
}
