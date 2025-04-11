package com.mazaady.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val posterUrl: String?,
    val releaseDate: String?,
    val overview: String?,
    val rating: Double,
    val genreIds: List<Int>,
    val runtime: Int?,
    var isFavorite: Boolean = false
) {
    companion object {
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
    }
}
