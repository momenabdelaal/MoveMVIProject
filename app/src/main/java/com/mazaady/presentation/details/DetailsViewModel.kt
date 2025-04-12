package com.mazaady.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mazaady.domain.model.Movie
import com.mazaady.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DetailsState())
    val state: StateFlow<DetailsState> = _state

    fun setMovie(movie: Movie) {
        _state.value = DetailsState(movie = movie)
    }

    fun toggleFavorite() {
        val currentMovie = _state.value.movie ?: return
        val updatedMovie = currentMovie.copy(isFavorite = !currentMovie.isFavorite)
        
        viewModelScope.launch {
            repository.toggleFavorite(updatedMovie)
            _state.value = _state.value.copy(movie = updatedMovie)
        }
    }
}

data class DetailsState(
    val movie: Movie? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
