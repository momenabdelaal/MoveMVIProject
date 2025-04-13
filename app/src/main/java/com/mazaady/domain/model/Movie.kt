package com.mazaady.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val id: Int,
    val title: String,
    val year: Int,
    val genres: List<String>,
    val rating: Double,
    val director: String,
    val actors: List<String>,
    val plot: String,
    val posterUrl: String,
    val trailerUrl: String,
    val runtime: Int,
    val awards: String,
    val country: String,
    val language: String,
    val boxOffice: String,
    val production: String,
    val website: String,
    val isFavorite: Boolean = false
) : Parcelable {
    companion object {
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
    }
}
