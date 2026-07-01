package com.lev.poalimtv.domain.model

data class MediaDetail(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val rating: Double,
    val releaseDate: String?,
    val mediaType: MediaType,
    val genres: List<String>,
    val status: String?,
    val runtime: Int?,          // minutes; null for TV
    val numberOfSeasons: Int?,  // null for movies
)
