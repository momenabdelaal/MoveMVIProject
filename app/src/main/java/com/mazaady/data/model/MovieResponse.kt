package com.mazaady.data.model

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    val page: Int,
    @SerializedName("results")
    val movies: List<MovieDto>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)

data class MovieDto(
    val id: Int,
    val title: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    val overview: String?,
    @SerializedName("vote_average")
    val rating: Double,
    @SerializedName("genre_ids")
    val genreIds: List<Int>,
    val runtime: Int?
)
