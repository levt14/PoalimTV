package com.lev.poalimtv.data.repository

import com.lev.poalimtv.domain.model.MediaItem
import com.lev.poalimtv.domain.model.MediaType
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getPopularMovies(): Result<List<MediaItem>>
    suspend fun getMovieDetails(movieId: Int): Result<MediaItem>
    suspend fun getMovieTrailerKey(movieId: Int): Result<String?>
    suspend fun searchMulti(query: String): Result<List<MediaItem>>
    fun getFavorites(): Flow<List<MediaItem>>
    suspend fun addFavorite(item: MediaItem)
    suspend fun removeFavorite(id: Int, mediaType: MediaType)
    fun isFavorite(id: Int, mediaType: MediaType): Flow<Boolean>
}
