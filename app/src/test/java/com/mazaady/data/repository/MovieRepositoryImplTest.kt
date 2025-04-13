package com.mazaady.data.repository

import com.mazaady.data.api.MovieApiService
import com.mazaady.data.db.MovieDao
import com.mazaady.data.db.MovieEntity
import com.mazaady.data.util.NetworkResult
import com.mazaady.domain.model.Movie
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class MovieRepositoryImplTest {
    private lateinit var repository: MovieRepositoryImpl
    private val movieApi: MovieApiService = mock()
    private val movieDao: MovieDao = mock()

    private val testMovie = Movie(
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

    private val testMovieEntity = MovieEntity(
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

    @Before
    fun setup() {
        repository = MovieRepositoryImpl(movieApi, movieDao)
    }

    @Test
    fun `getFavoriteMovies returns success with movies from local database`() = runTest {
        // Given
        whenever(movieDao.getFavoriteMovies()).thenReturn(flowOf(listOf(testMovieEntity)))

        // When
        val result = repository.getFavoriteMovies().first()

        // Then
        assert(result is NetworkResult.Success)
        assert((result as NetworkResult.Success).data.first().id == testMovie.id)
    }

    @Test
    fun `getFavoriteMovies returns error when database throws exception`() = runTest {
        // Given
        val errorMessage = "Database error"
        whenever(movieDao.getFavoriteMovies()).thenReturn(flow { throw RuntimeException(errorMessage) })

        // When
        val result = repository.getFavoriteMovies().first()

        // Then
        assert(result is NetworkResult.Error)
        assert((result as NetworkResult.Error).message == errorMessage)
    }

    @Test
    fun `toggleFavorite updates movie in local database`() = runTest {
        // Given
        whenever(movieDao.getMovie(testMovie.id)).thenReturn(testMovieEntity)

        // When
        repository.toggleFavorite(testMovie)

        // Then
        verify(movieDao).toggleFavorite(testMovie.id)
    }
}
