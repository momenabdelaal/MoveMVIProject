package com.mazaady.presentation.home

import androidx.paging.PagingData
import app.cash.turbine.test
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
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private lateinit var viewModel: HomeViewModel
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
        viewModel = HomeViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when LoadMovies intent is processed, then fetch movies from repository`() = runTest {
        // Given
        val movies = PagingData.from(listOf(testMovie))
        whenever(repository.getMovies()).thenReturn(flowOf(movies))

        // When
        viewModel.processIntent(HomeIntent.LoadMovies)

        // Then
        viewModel.state.test {
            val emission = awaitItem()
            assert(!emission.isLoading)
            assert(emission.error == null)
        }
        verify(repository).getMovies()
    }

    @Test
    fun `when ToggleLayout intent is processed, then toggle grid state`() = runTest {
        // Given
        val initialState = viewModel.state.value.isGrid

        // When
        viewModel.processIntent(HomeIntent.ToggleLayout)

        // Then
        viewModel.state.test {
            val emission = awaitItem()
            assert(emission.isGrid != initialState)
        }
    }

    @Test
    fun `when ToggleFavorite intent is processed, then update movie favorite status`() = runTest {
        // When
        viewModel.processIntent(HomeIntent.ToggleFavorite(testMovie))

        // Then
        verify(repository).toggleFavorite(testMovie)
    }

    @Test
    fun `when RefreshMovies intent is processed, then reload movies`() = runTest {
        // Given
        val movies = PagingData.from(listOf(testMovie))
        whenever(repository.getMovies()).thenReturn(flowOf(movies))

        // When
        viewModel.processIntent(HomeIntent.RefreshMovies)

        // Then
        viewModel.state.test {
            val emission = awaitItem()
            assert(!emission.isLoading)
            assert(emission.error == null)
        }
        verify(repository).getMovies()
    }
}
