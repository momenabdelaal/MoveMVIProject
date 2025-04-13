package com.mazaady.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mazaady.data.util.NetworkResult
import com.mazaady.domain.model.Movie
import com.mazaady.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DetailsState())
    val state: StateFlow<DetailsState> = _state

    fun setMovie(movie: Movie) {
        _state.update { it.copy(movie = movie) }
    }

    fun toggleFavorite() {
        val currentMovie = _state.value.movie ?: return
        val updatedMovie = currentMovie.copy(isFavorite = !currentMovie.isFavorite)
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                when (val result = repository.toggleFavorite(updatedMovie)) {
                    is NetworkResult.Success -> {
                        _state.update { it.copy(
                            movie = updatedMovie,
                            isLoading = false,
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
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                ) }
            }
        }
    }
}

data class DetailsState(
    val movie: Movie? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
