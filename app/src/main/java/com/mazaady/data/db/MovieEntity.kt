package com.mazaady.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String?,
    val overview: String?,
    val rating: Double,
    val genreIds: List<Int>,
    val runtime: Int?
)
