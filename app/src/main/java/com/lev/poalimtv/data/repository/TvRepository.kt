package com.lev.poalimtv.data.repository

import com.lev.poalimtv.domain.model.MediaItem
import com.lev.poalimtv.domain.model.MediaType
import kotlinx.coroutines.flow.Flow

interface TvRepository {
    suspend fun getPopularTvShows(): Result<List<MediaItem>>
    suspend fun getTvDetails(tvId: Int): Result<MediaItem>
    suspend fun getTvTrailerKey(tvId: Int): Result<String?>
    fun getFavorites(): Flow<List<MediaItem>>
    suspend fun addFavorite(item: MediaItem)
    suspend fun removeFavorite(id: Int, mediaType: MediaType)
    fun isFavorite(id: Int, mediaType: MediaType): Flow<Boolean>
}
