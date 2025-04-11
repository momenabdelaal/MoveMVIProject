package com.mazaady.data.db

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Delete
    suspend fun deleteMovie(movie: MovieEntity)

    @Query("SELECT * FROM movies")
    fun getFavoriteMovies(): Flow<List<MovieEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM movies WHERE id = :movieId)")
    fun isMovieFavorite(movieId: Int): Flow<Boolean>

    @Query("SELECT * FROM movies")
    fun getFavoriteMoviesPaged(): PagingSource<Int, MovieEntity>
}
