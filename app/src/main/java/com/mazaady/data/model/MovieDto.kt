package com.mazaady.data.model

import com.google.gson.annotations.SerializedName
import com.mazaady.domain.model.Movie

data class MovieDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("year")
    val year: Int,
    @SerializedName("genre")
    val genre: List<String>,
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("director")
    val director: String,
    @SerializedName("actors")
    val actors: List<String>,
    @SerializedName("plot")
    val plot: String,
    @SerializedName("poster")
    val poster: String,
    @SerializedName("trailer")
    val trailer: String,
    @SerializedName("runtime")
    val runtime: Int,
    @SerializedName("awards")
    val awards: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("language")
    val language: String,
    @SerializedName("boxOffice")
    val boxOffice: String,
    @SerializedName("production")
    val production: String,
    @SerializedName("website")
    val website: String
) {
    fun toDomainModel() = Movie(
        id = id,
        title = title,
        year = year,
        genres = genre,
        rating = rating,
        director = director,
        actors = actors,
        plot = plot,
        posterUrl = poster,
        trailerUrl = trailer,
        runtime = runtime,
        awards = awards,
        country = country,
        language = language,
        boxOffice = boxOffice,
        production = production,
        website = website
    )
}
