package com.lev.poalimtv.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lev.poalimtv.data.repository.MovieRepository
import com.lev.poalimtv.domain.model.MediaItem
import com.lev.poalimtv.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MovieRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val results: StateFlow<UiState<List<MediaItem>>> = _query
        .debounce(300L)
        .flatMapLatest { q ->
            if (q.isBlank()) {
                flowOf(UiState.Success(emptyList()))
            } else {
                flow {
                    emit(UiState.Loading)
                    val result = repository.searchMulti(q)
                    emit(
                        result.fold(
                            onSuccess = { UiState.Success(it) },
                            onFailure = { UiState.Error(it.message ?: "Search failed") },
                        )
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Success(emptyList()),
        )

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }
}
