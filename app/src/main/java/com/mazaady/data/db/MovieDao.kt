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

    @Query("SELECT * FROM movies WHERE isFavorite = 1")
    fun getFavoriteMovies(): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Update
    suspend fun updateMovie(movie: MovieEntity)

    @Query("DELETE FROM movies")
    suspend fun clearMovies()

    @Query("SELECT COUNT(*) FROM movies")
    suspend fun getMovieCount(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM movies WHERE id = :movieId AND isFavorite = 1)")
    fun isMovieFavorite(movieId: Int): Flow<Boolean>
}
