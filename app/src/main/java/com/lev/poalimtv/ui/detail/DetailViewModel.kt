package com.lev.poalimtv.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lev.poalimtv.data.local.FavoriteEntity
import com.lev.poalimtv.data.local.FavoritesDao
import com.lev.poalimtv.data.repository.MovieRepository
import com.lev.poalimtv.data.repository.TvRepository
import com.lev.poalimtv.domain.model.MediaDetail
import com.lev.poalimtv.domain.model.MediaType
import com.lev.poalimtv.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val movieRepository: MovieRepository,
    private val tvRepository: TvRepository,
    private val dao: FavoritesDao,
) : ViewModel() {

    private val mediaId: Int = checkNotNull(savedStateHandle["mediaId"])
    private val mediaType: MediaType = MediaType.valueOf(
        checkNotNull(savedStateHandle.get<String>("mediaType")).uppercase()
    )

    val detail: StateFlow<UiState<MediaDetail>> = flow {
        emit(UiState.Loading)
        val result = when (mediaType) {
            MediaType.MOVIE -> movieRepository.getMovieDetails(mediaId)
            MediaType.TV -> tvRepository.getTvDetails(mediaId)
        }
        emit(
            result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Failed to load details") },
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    val trailerKey: StateFlow<String?> = flow {
        emit(null)
        val result = when (mediaType) {
            MediaType.MOVIE -> movieRepository.getMovieTrailerKey(mediaId)
            MediaType.TV -> tvRepository.getTvTrailerKey(mediaId)
        }
        emit(result.getOrNull())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val isFavorite: StateFlow<Boolean> = dao.isFavorite(mediaId, mediaType.name)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    fun toggleFavorite(detail: MediaDetail) {
        viewModelScope.launch {
            if (isFavorite.value) {
                dao.deleteById(mediaId, mediaType.name)
            } else {
                dao.insert(
                    FavoriteEntity(
                        id = detail.id,
                        title = detail.title,
                        posterPath = detail.posterPath,
                        mediaType = mediaType.name,
                        rating = detail.rating,
                    )
                )
            }
        }
    }
}
