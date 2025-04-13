package com.mazaady.presentation.details

import app.cash.turbine.test
import com.mazaady.data.util.NetworkResult
import com.mazaady.domain.model.Movie
import com.mazaady.domain.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {
    private lateinit var viewModel: DetailsViewModel
    private val repository: MovieRepository = mock()
    private val testDispatcher = StandardTestDispatcher()

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
        isFavorite = false
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DetailsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setMovie updates state with provided movie`() = runTest {
        // When
        viewModel.setMovie(testMovie)

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assert(state.movie == testMovie)
            assert(!state.isLoading)
            assert(state.error == null)
        }
    }

    @Test
    fun `toggleFavorite updates movie favorite status`() = runTest {
        // Given
        viewModel.setMovie(testMovie)
        val updatedMovie = testMovie.copy(isFavorite = true)
        whenever(repository.toggleFavorite(updatedMovie))
            .thenReturn(NetworkResult.Success(Unit))

        // When
        viewModel.toggleFavorite()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assert(state.movie?.isFavorite == true) { "Movie favorite status not updated" }
            assert(!state.isLoading) { "Loading state not cleared" }
            assert(state.error == null) { "Error state not null" }
        }
        verify(repository).toggleFavorite(updatedMovie)
    }

    @Test
    fun `toggleFavorite handles error`() = runTest {
        // Given
        viewModel.setMovie(testMovie)
        val updatedMovie = testMovie.copy(isFavorite = true)
        val errorMessage = "Failed to toggle favorite"
        whenever(repository.toggleFavorite(updatedMovie))
            .thenReturn(NetworkResult.Error(errorMessage))

        // When
        viewModel.toggleFavorite()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assert(!state.isLoading) { "Loading state not cleared" }
            assert(state.error == errorMessage) { "Error message not set" }
            assert(state.movie == testMovie) { "Movie state changed despite error" }
        }
        verify(repository).toggleFavorite(updatedMovie)
    }

    @Test
    fun `toggleFavorite does nothing when movie is null`() = runTest {
        // When
        viewModel.toggleFavorite()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assert(state.movie == null)
            assert(!state.isLoading)
            assert(state.error == null)
        }
    }
}
