package com.lev.poalimtv.data.repository

import com.lev.poalimtv.data.local.FavoriteEntity
import com.lev.poalimtv.data.local.FavoritesDao
import com.lev.poalimtv.data.remote.TmdbApiService
import com.lev.poalimtv.data.remote.dto.TvDetailDto
import com.lev.poalimtv.data.remote.dto.TvShowDto
import com.lev.poalimtv.data.remote.dto.VideosResponseDto
import com.lev.poalimtv.domain.model.MediaDetail
import com.lev.poalimtv.domain.model.MediaItem
import com.lev.poalimtv.domain.model.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TvRepositoryImpl @Inject constructor(
    private val api: TmdbApiService,
    private val dao: FavoritesDao,
) : TvRepository {

    override suspend fun getPopularTvShows(): Result<List<MediaItem>> =
        runCatching { api.getPopularTvShows().results.map { it.toDomain() } }

    override suspend fun getTvDetails(tvId: Int): Result<MediaDetail> =
        runCatching { api.getTvDetails(tvId).toDetailDomain() }

    override suspend fun getTvTrailerKey(tvId: Int): Result<String?> =
        runCatching { api.getTvVideos(tvId).trailerKey() }

    override fun getFavorites(): Flow<List<MediaItem>> =
        dao.getAllFavorites().map { list -> list.map { it.toDomain() } }

    override suspend fun addFavorite(item: MediaItem) =
        dao.insert(item.toEntity())

    override suspend fun removeFavorite(id: Int, mediaType: MediaType) =
        dao.deleteById(id, mediaType.name)

    override fun isFavorite(id: Int, mediaType: MediaType): Flow<Boolean> =
        dao.isFavorite(id, mediaType.name)
}

private fun TvShowDto.toDomain() = MediaItem(
    id = id,
    title = name,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    rating = voteAverage,
    releaseDate = firstAirDate,
    mediaType = MediaType.TV,
)

private fun TvDetailDto.toDetailDomain() = MediaDetail(
    id = id,
    title = name,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    rating = voteAverage,
    releaseDate = firstAirDate,
    mediaType = MediaType.TV,
    genres = genres.map { it.name },
    status = status,
    runtime = null,
    numberOfSeasons = numberOfSeasons,
)

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

private fun MediaItem.toEntity() = FavoriteEntity(
    id = id,
    title = title,
    posterPath = posterPath,
    mediaType = mediaType.name,
    rating = rating,
)

private fun VideosResponseDto.trailerKey(): String? =
    results.firstOrNull { it.site == "YouTube" && it.type == "Trailer" && it.official }?.key
        ?: results.firstOrNull { it.site == "YouTube" && it.type == "Trailer" }?.key
