package com.lev.poalimtv.data.remote

import com.lev.poalimtv.data.remote.dto.MovieDetailDto
import com.lev.poalimtv.data.remote.dto.MovieDto
import com.lev.poalimtv.data.remote.dto.MultiSearchItemDto
import com.lev.poalimtv.data.remote.dto.PagedResponseDto
import com.lev.poalimtv.data.remote.dto.TvDetailDto
import com.lev.poalimtv.data.remote.dto.TvShowDto
import com.lev.poalimtv.data.remote.dto.VideosResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1,
    ): PagedResponseDto<MovieDto>

    @GET("tv/popular")
    suspend fun getPopularTvShows(
        @Query("page") page: Int = 1,
    ): PagedResponseDto<TvShowDto>

    @GET("search/multi")
    suspend fun searchMulti(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
    ): PagedResponseDto<MultiSearchItemDto>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
    ): MovieDetailDto

    @GET("tv/{tv_id}")
    suspend fun getTvDetails(
        @Path("tv_id") tvId: Int,
    ): TvDetailDto

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
    ): VideosResponseDto

    @GET("tv/{tv_id}/videos")
    suspend fun getTvVideos(
        @Path("tv_id") tvId: Int,
    ): VideosResponseDto
}
