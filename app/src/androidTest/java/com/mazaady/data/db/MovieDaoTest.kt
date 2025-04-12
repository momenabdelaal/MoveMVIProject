package com.mazaady.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var movieDao: MovieDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        movieDao = database.movieDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetFavoriteMovies() = runBlocking {
        // Given
        val movie = MovieEntity(
            id = 1,
            title = "Test Movie",
            plot = "Test Plot",
            year = 2023,
            rating = 4.5,
            posterUrl = "url",
            genres = listOf("Action", "Drama"),
            director = "Test Director",
            actors = listOf("Actor 1", "Actor 2"),
            trailerUrl = "trailer_url",
            runtime = 120,
            awards = "Test Awards",
            country = "Test Country",
            language = "English",
            boxOffice = "$100M",
            production = "Test Production",
            website = "test.com",
            isFavorite = true
        )

        // When
        movieDao.insertMovies(listOf(movie))
        val favoriteMovies = movieDao.getFavoriteMovies().first()

        // Then
        assert(favoriteMovies.contains(movie))
    }

    @Test
    fun updateMovieFavoriteStatus() = runBlocking {
        // Given
        val movie = MovieEntity(
            id = 1,
            title = "Test Movie",
            plot = "Test Plot",
            year = 2023,
            rating = 4.5,
            posterUrl = "url",
            genres = listOf("Action", "Drama"),
            director = "Test Director",
            actors = listOf("Actor 1", "Actor 2"),
            trailerUrl = "trailer_url",
            runtime = 120,
            awards = "Test Awards",
            country = "Test Country",
            language = "English",
            boxOffice = "$100M",
            production = "Test Production",
            website = "test.com",
            isFavorite = false
        )
        movieDao.insertMovies(listOf(movie))

        // When
        movieDao.toggleFavorite(movie.id)
        val favoriteMovies = movieDao.getFavoriteMovies().first()

        // Then
        assert(favoriteMovies.any { it.id == movie.id && it.isFavorite })
    }
}
