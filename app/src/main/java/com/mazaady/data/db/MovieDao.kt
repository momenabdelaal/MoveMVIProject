package com.mazaady.data.db

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY id ASC")
    fun getMovies(): PagingSource<Int, MovieEntity>

    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovie(movieId: Int): MovieEntity?

    @Query("SELECT * FROM movies WHERE isFavorite = 1 ORDER BY id ASC")
    fun getFavoriteMovies(): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Update
    suspend fun updateMovie(movie: MovieEntity)

    @Query("DELETE FROM movies")
    suspend fun clearMovies()

    @Query("SELECT COUNT(*) FROM movies")
    suspend fun getMovieCount(): Int

    @Query("SELECT isFavorite FROM movies WHERE id = :movieId")
    suspend fun getFavoriteStatus(movieId: Int): Boolean?

    @Query("SELECT isFavorite FROM movies WHERE id = :movieId")
    fun isMovieFavorite(movieId: Int): Flow<Boolean>

    @Query("UPDATE movies SET isFavorite = :isFavorite WHERE id = :movieId")
    suspend fun updateFavoriteStatus(movieId: Int, isFavorite: Boolean)

    @Transaction
    suspend fun toggleFavorite(movieId: Int) {
        val currentFavorite = getFavoriteStatus(movieId) ?: false
        updateFavoriteStatus(movieId, !currentFavorite)
    }
}
