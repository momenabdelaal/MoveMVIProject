package com.mazaady.presentation.favorites

import app.cash.turbine.test
import com.mazaady.data.util.NetworkResult
import com.mazaady.domain.model.Movie
import com.mazaady.domain.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {
    private lateinit var viewModel: FavoritesViewModel
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
        isFavorite = true
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        reset(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads favorite movies`() = runTest {
        // Given
        whenever(repository.getFavoriteMovies()).thenReturn(
            flowOf(NetworkResult.Success(listOf(testMovie)))
        )

        // When
        viewModel = FavoritesViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assert(!state.isLoading)
            assert(state.error == null)
            assert(state.movies.contains(testMovie))
        }
        verify(repository, times(1)).getFavoriteMovies()
    }

    @Test
    fun `LoadFavorites intent reloads favorite movies`() = runTest {
        // Given
        whenever(repository.getFavoriteMovies()).thenReturn(
            flowOf(NetworkResult.Success(listOf(testMovie)))
        )
        viewModel = FavoritesViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        reset(repository)

        whenever(repository.getFavoriteMovies()).thenReturn(
            flowOf(NetworkResult.Success(listOf(testMovie)))
        )

        // When
        viewModel.processIntent(FavoritesIntent.LoadFavorites)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assert(!state.isLoading)
            assert(state.error == null)
            assert(state.movies.contains(testMovie))
        }
        verify(repository, times(1)).getFavoriteMovies()
    }

    @Test
    fun `ToggleLayout intent toggles grid state`() = runTest {
        // Given
        whenever(repository.getFavoriteMovies()).thenReturn(
            flowOf(NetworkResult.Success(listOf(testMovie)))
        )
        viewModel = FavoritesViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        val initialState = viewModel.state.value.isGrid

        // When
        viewModel.processIntent(FavoritesIntent.ToggleLayout)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assert(state.isGrid != initialState)
            assert(state.error == null)
            assert(state.movies.contains(testMovie)) // Previous state preserved
        }
    }

    @Test
    fun `ToggleFavorite intent updates movie favorite status and reloads favorites`() = runTest {
        // Given
        whenever(repository.getFavoriteMovies()).thenReturn(
            flowOf(NetworkResult.Success(listOf(testMovie)))
        )
        viewModel = FavoritesViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        reset(repository)

        val updatedMovie = testMovie.copy(isFavorite = false)
        whenever(repository.toggleFavorite(testMovie)).thenReturn(NetworkResult.Success(Unit))
        whenever(repository.getFavoriteMovies()).thenReturn(
            flowOf(NetworkResult.Success(listOf(updatedMovie)))
        )

        // When
        viewModel.processIntent(FavoritesIntent.ToggleFavorite(testMovie))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(repository, times(1)).toggleFavorite(testMovie)
        verify(repository, times(1)).getFavoriteMovies()
        viewModel.state.test {
            val state = awaitItem()
            assert(!state.isLoading)
            assert(state.error == null)
            assert(state.movies.contains(updatedMovie))
        }
    }

    @Test
    fun `getFavoriteMovies error updates error state`() = runTest {
        // Given
        val errorMessage = "Failed to load favorites"
        whenever(repository.getFavoriteMovies()).thenReturn(
            flowOf(NetworkResult.Error(errorMessage))
        )

        // When
        viewModel = FavoritesViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assert(!state.isLoading)
            assert(state.error == errorMessage)
            assert(state.movies.isEmpty())
        }
    }

    @Test
    fun `toggleFavorite error updates error state`() = runTest {
        // Given
        whenever(repository.getFavoriteMovies()).thenReturn(
            flowOf(NetworkResult.Success(listOf(testMovie)))
        )
        viewModel = FavoritesViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        reset(repository)

        val errorMessage = "Failed to toggle favorite"
        whenever(repository.toggleFavorite(testMovie)).thenReturn(NetworkResult.Error(errorMessage))

        // When
        viewModel.processIntent(FavoritesIntent.ToggleFavorite(testMovie))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assert(!state.isLoading)
            assert(state.error == errorMessage)
            assert(state.movies.contains(testMovie)) // Previous state preserved
        }
        verify(repository, times(1)).toggleFavorite(testMovie)
    }
}
