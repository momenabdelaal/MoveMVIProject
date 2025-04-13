package com.mazaady.presentation.favorites

import androidx.lifecycle.viewModelScope
import com.mazaady.data.util.NetworkResult
import com.mazaady.domain.model.Movie
import com.mazaady.domain.repository.MovieRepository
import com.mazaady.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: MovieRepository
) : MviViewModel<FavoritesState, FavoritesIntent>() {

    init {
        loadFavorites()
    }

    override fun createInitialState(): FavoritesState = FavoritesState()

    override fun handleIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.LoadFavorites -> loadFavorites()
            is FavoritesIntent.ToggleLayout -> toggleLayout()
            is FavoritesIntent.ToggleFavorite -> toggleFavorite(intent.movie)
            is FavoritesIntent.NavigateToDetails -> Unit // Handled by Fragment
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                repository.getFavoriteMovies().collectLatest { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            _state.update { it.copy(
                                isLoading = false,
                                movies = result.data,
                                error = null
                            ) }
                        }
                        is NetworkResult.Error -> {
                            _state.update { it.copy(
                                isLoading = false,
                                error = result.message
                            ) }
                        }
                        is NetworkResult.Loading -> {
                            _state.update { it.copy(
                                isLoading = true,
                                error = null
                            ) }
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                ) }
            }
        }
    }

    private fun toggleLayout() {
        _state.update { it.copy(
            isGrid = !it.isGrid,
            error = null
        ) }
    }

    private fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                when (val result = repository.toggleFavorite(movie)) {
                    is NetworkResult.Success -> loadFavorites()
                    is NetworkResult.Error -> _state.update { it.copy(
                        error = result.message,
                        isLoading = false
                    ) }
                    is NetworkResult.Loading -> _state.update { it.copy(
                        isLoading = true,
                        error = null
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                ) }
            }
        }
    }
}