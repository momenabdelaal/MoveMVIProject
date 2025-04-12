package com.mazaady.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mazaady.domain.model.Movie
import com.mazaady.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    init {
        processIntent(HomeIntent.LoadMovies)
    }

    fun processIntent(intent: HomeIntent) {
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
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                repository.getMovies()
                    .cachedIn(viewModelScope)
                    .catch { e ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = e.message ?: "Unknown error occurred"
                        )
                    }
                    .collectLatest { pagingData ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            movies = pagingData,
                            error = null
                        )
                    }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    private fun toggleLayout() {
        _state.value = _state.value.copy(isGrid = !_state.value.isGrid)
    }

    private fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            try {
                repository.toggleFavorite(movie)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Error toggling favorite"
                )
            }
        }
    }
}
