package com.mazaady.presentation.home

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mazaady.data.util.NetworkResult
import com.mazaady.domain.model.Movie
import com.mazaady.domain.usecase.GetMoviesUseCase
import com.mazaady.domain.usecase.ToggleFavoriteUseCase
import com.mazaady.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : MviViewModel<HomeState, HomeIntent>() {

    init {
        loadMovies()
    }

    override fun createInitialState(): HomeState = HomeState()

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadMovies -> loadMovies()
            is HomeIntent.RefreshMovies -> loadMovies()
            is HomeIntent.ToggleLayout -> toggleLayout()
            is HomeIntent.ToggleFavorite -> toggleFavorite(intent.movie)
            is HomeIntent.NavigateToDetails -> Unit // Handled by fragment
        }
    }

    private fun loadMovies() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                val moviesFlow = getMoviesUseCase().cachedIn(viewModelScope)
                _state.update { it.copy(
                    isLoading = false,
                    movies = moviesFlow,
                    error = null
                ) }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                ) }
            }
        }
    }

    private fun toggleLayout() {
        _state.update { it.copy(isGrid = !it.isGrid) }
    }

    private fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            when (val result = toggleFavoriteUseCase(movie)) {
                is NetworkResult.Success -> {
                    // The PagingData will automatically refresh with the updated favorite status
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(error = result.message) }
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }
}
