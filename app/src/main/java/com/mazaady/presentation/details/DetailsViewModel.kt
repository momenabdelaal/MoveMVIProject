package com.mazaady.presentation.details

import androidx.lifecycle.viewModelScope
import com.mazaady.domain.repository.MovieRepository
import com.mazaady.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: MovieRepository
) : MviViewModel<DetailsIntent, DetailsState>() {

    override fun createInitialState(): DetailsState = DetailsState()

    override fun handleIntent(intent: DetailsIntent) {
        when (intent) {
            is DetailsIntent.LoadMovie -> loadMovie(intent.movieId)
            is DetailsIntent.ToggleFavorite -> toggleFavorite(intent.movie)
        }
    }

    private fun loadMovie(movieId: Int) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            try {
                val movie = repository.getMovieDetails(movieId)
                setState { copy(movie = movie, isLoading = false) }
            } catch (e: Exception) {
                setState { copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            try {
                repository.toggleFavorite(movie)
                setState { copy(movie = movie.copy(isFavorite = !movie.isFavorite)) }
            } catch (e: Exception) {
                setState { copy(error = e.message) }
            }
        }
    }
}
