package com.mazaady.presentation.home

import androidx.lifecycle.viewModelScope
import com.mazaady.domain.repository.MovieRepository
import com.mazaady.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MovieRepository
) : MviViewModel<HomeIntent, HomeState>() {

    override fun createInitialState(): HomeState = HomeState()

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadMovies -> loadMovies()
            is HomeIntent.ToggleViewType -> toggleViewType(intent.isGrid)
            is HomeIntent.ToggleFavorite -> toggleFavorite(intent.movie)
            is HomeIntent.NavigateToDetails -> Unit // Handled by Fragment
        }
    }

    private fun loadMovies() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            repository.getMovies()
                .catch { e ->
                    setState { copy(isLoading = false, error = e.message) }
                }
                .collectLatest { pagingData ->
                    setState { 
                        copy(
                            movies = pagingData,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    private fun toggleViewType(isGrid: Boolean) {
        setState { copy(isGrid = isGrid) }
    }

    private fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            try {
                repository.toggleFavorite(movie)
            } catch (e: Exception) {
                setState { copy(error = e.message) }
            }
        }
    }
}
