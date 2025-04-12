package com.mazaady.domain.repository

import androidx.paging.PagingData
import com.mazaady.data.util.NetworkResult
import com.mazaady.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    /**
     * Get paginated list of movies
     */
    fun getMovies(): Flow<PagingData<Movie>>


    suspend fun getMovieDetails(movieId: Int): NetworkResult<Movie>

    /**
     * Toggle favorite status of a movie
     */
    suspend fun toggleFavorite(movie: Movie): NetworkResult<Unit>

    /**
     * Get all favorite movies
     */
    fun getFavoriteMovies(): Flow<NetworkResult<List<Movie>>>

    /**
     * Check if a movie is favorite
     */
    fun isMovieFavorite(movieId: Int): Flow<Boolean>
}
