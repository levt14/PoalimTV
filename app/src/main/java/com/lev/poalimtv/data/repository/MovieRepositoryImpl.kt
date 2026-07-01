package com.lev.poalimtv.data.repository

import com.lev.poalimtv.data.local.FavoriteEntity
import com.lev.poalimtv.data.local.FavoritesDao
import com.lev.poalimtv.data.remote.TmdbApiService
import com.lev.poalimtv.data.remote.dto.MovieDetailDto
import com.lev.poalimtv.data.remote.dto.MovieDto
import com.lev.poalimtv.data.remote.dto.MultiSearchItemDto
import com.lev.poalimtv.data.remote.dto.VideosResponseDto
import com.lev.poalimtv.domain.model.MediaDetail
import com.lev.poalimtv.domain.model.MediaItem
import com.lev.poalimtv.domain.model.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApiService,
    private val dao: FavoritesDao,
) : MovieRepository {

    override suspend fun getPopularMovies(): Result<List<MediaItem>> =
        runCatching { api.getPopularMovies().results.map { it.toDomain() } }

    override suspend fun getMovieDetails(movieId: Int): Result<MediaDetail> =
        runCatching { api.getMovieDetails(movieId).toDetailDomain() }

    override suspend fun getMovieTrailerKey(movieId: Int): Result<String?> =
        runCatching { api.getMovieVideos(movieId).trailerKey() }

    override suspend fun searchMulti(query: String): Result<List<MediaItem>> =
        runCatching { api.searchMulti(query).results.mapNotNull { it.toDomain() } }

    override fun getFavorites(): Flow<List<MediaItem>> =
        dao.getAllFavorites().map { list -> list.map { it.toDomain() } }

    override suspend fun addFavorite(item: MediaItem) =
        dao.insert(item.toEntity())

    override suspend fun removeFavorite(id: Int, mediaType: MediaType) =
        dao.deleteById(id, mediaType.name)

    override fun isFavorite(id: Int, mediaType: MediaType): Flow<Boolean> =
        dao.isFavorite(id, mediaType.name)
}

private fun MovieDto.toDomain() = MediaItem(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    rating = voteAverage,
    releaseDate = releaseDate,
    mediaType = MediaType.MOVIE,
)

private fun MovieDetailDto.toDetailDomain() = MediaDetail(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    rating = voteAverage,
    releaseDate = releaseDate,
    mediaType = MediaType.MOVIE,
    genres = genres.map { it.name },
    status = status,
    runtime = runtime,
    numberOfSeasons = null,
)

private fun MultiSearchItemDto.toDomain(): MediaItem? {
    val type = when (mediaType) {
        "movie" -> MediaType.MOVIE
        "tv" -> MediaType.TV
        else -> return null
    }
    return MediaItem(
        id = id,
        title = title ?: name ?: "",
        overview = overview ?: "",
        posterPath = posterPath,
        backdropPath = backdropPath,
        rating = voteAverage ?: 0.0,
        releaseDate = releaseDate ?: firstAirDate,
        mediaType = type,
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
