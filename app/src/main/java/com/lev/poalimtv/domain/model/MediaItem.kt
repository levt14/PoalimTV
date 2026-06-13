package com.lev.poalimtv.domain.model

enum class MediaType { MOVIE, TV }

data class MediaItem(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val rating: Double,
    val releaseDate: String?,
    val mediaType: MediaType,
)
