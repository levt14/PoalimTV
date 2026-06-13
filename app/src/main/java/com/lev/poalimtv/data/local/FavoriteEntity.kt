package com.lev.poalimtv.data.local

import androidx.room.Entity

@Entity(
    tableName = "favorites",
    primaryKeys = ["id", "mediaType"],
)
data class FavoriteEntity(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val mediaType: String,
    val rating: Double,
)
