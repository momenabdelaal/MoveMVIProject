package com.mazaady.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mazaady.domain.model.Movie

@Entity(tableName = "movies")
@TypeConverters(MovieEntity.Converters::class)
data class MovieEntity(
    @PrimaryKey
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
) {
    fun toDomainModel() = Movie(
        id = id,
        title = title,
        year = year,
        genres = genres,
        rating = rating,
        director = director,
        actors = actors,
        plot = plot,
        posterUrl = posterUrl,
        trailerUrl = trailerUrl,
        runtime = runtime,
        awards = awards,
        country = country,
        language = language,
        boxOffice = boxOffice,
        production = production,
        website = website,
        isFavorite = isFavorite
    )

    companion object {
        fun fromDomainModel(movie: Movie) = MovieEntity(
            id = movie.id,
            title = movie.title,
            year = movie.year,
            genres = movie.genres,
            rating = movie.rating,
            director = movie.director,
            actors = movie.actors,
            plot = movie.plot,
            posterUrl = movie.posterUrl,
            trailerUrl = movie.trailerUrl,
            runtime = movie.runtime,
            awards = movie.awards,
            country = movie.country,
            language = movie.language,
            boxOffice = movie.boxOffice,
            production = movie.production,
            website = movie.website,
            isFavorite = movie.isFavorite
        )
    }

    class Converters {
        private val gson = Gson()

        @TypeConverter
        fun fromString(value: String): List<String> {
            val listType = object : TypeToken<List<String>>() {}.type
            return gson.fromJson(value, listType)
        }

        @TypeConverter
        fun fromList(list: List<String>): String {
            return gson.toJson(list)
        }
    }
}
