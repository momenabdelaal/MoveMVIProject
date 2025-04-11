package com.mazaady.domain.repository

import androidx.paging.PagingData
import com.mazaady.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMovies(): Flow<PagingData<Movie>>
    suspend fun getMovieDetails(movieId: Int): Movie
    suspend fun toggleFavorite(movie: Movie)
    fun getFavoriteMovies(): Flow<List<Movie>>
    fun isMovieFavorite(movieId: Int): Flow<Boolean>
}
