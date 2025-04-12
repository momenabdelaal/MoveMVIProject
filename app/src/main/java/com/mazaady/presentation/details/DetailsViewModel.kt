package com.mazaady.presentation.details

import androidx.lifecycle.viewModelScope
import com.mazaady.data.util.NetworkResult
import com.mazaady.domain.model.Movie
import com.mazaady.domain.usecase.GetMovieDetailsUseCase
import com.mazaady.domain.usecase.ToggleFavoriteUseCase
import com.mazaady.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : MviViewModel<DetailsState, DetailsIntent>() {

    override fun createInitialState(): DetailsState = DetailsState()

    override fun handleIntent(intent: DetailsIntent) {
        when (intent) {
            is DetailsIntent.LoadMovie -> loadMovie(intent.movie)
            is DetailsIntent.ToggleFavorite -> toggleFavorite(intent.movie)
        }
    }

    private fun loadMovie(movie: Movie) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            when (val result = getMovieDetailsUseCase(movie.id)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(
                        isLoading = false,
                        movie = result.data,
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
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            when (val result = toggleFavoriteUseCase(movie)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(
                        movie = it.movie?.copy(isFavorite = !it.movie.isFavorite),
                        error = null
                    ) }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(error = result.message) }
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }
}
