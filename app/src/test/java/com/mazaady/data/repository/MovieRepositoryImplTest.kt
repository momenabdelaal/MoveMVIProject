package com.mazaady.data.repository

import com.mazaady.data.db.MovieDao
import com.mazaady.data.remote.MovieApi
import com.mazaady.domain.model.Movie
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MovieRepositoryImplTest {
    private lateinit var repository: MovieRepositoryImpl
    private val movieApi: MovieApi = mock()
    private val movieDao: MovieDao = mock()

    @Before
    fun setup() {
        repository = MovieRepositoryImpl(movieApi, movieDao)
    }

    @Test
    fun `getFavoriteMovies returns movies from local database`() = runTest {
        // Given
        val movies = listOf(
            Movie(
                id = 1,
                title = "Test Movie",
                plot = "Test Plot",
                year = 2023,
                rating = 4.5,
                posterUrl = "url",
                isFavorite = true
            )
        )
        whenever(movieDao.getFavoriteMovies()).thenReturn(flowOf(movies.map { it.toMovieEntity() }))

        // When
        val result = repository.getFavoriteMovies().first()

        // Then
        assert(result is NetworkResult.Success)
        assert((result as NetworkResult.Success).data == movies)
    }

    @Test
    fun `toggleFavorite updates movie in local database`() = runTest {
        // Given
        val movie = Movie(
            id = 1,
            title = "Test Movie",
            plot = "Test Plot",
            year = 2023,
            rating = 4.5,
            posterUrl = "url",
            isFavorite = false
        )

        // When
        repository.toggleFavorite(movie)

        // Then
        verify(movieDao).updateMovie(movie.toMovieEntity().copy(isFavorite = true))
    }
}
