package com.lev.poalimtv.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MultiSearchItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("media_type") val mediaType: String,
    // movie
    @SerializedName("title") val title: String?,
    @SerializedName("release_date") val releaseDate: String?,
    // tv
    @SerializedName("name") val name: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    // common
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
)
