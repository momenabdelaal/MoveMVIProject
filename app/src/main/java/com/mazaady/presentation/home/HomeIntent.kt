package com.mazaady.presentation.home

import com.mazaady.domain.model.Movie

sealed class HomeIntent {
    object LoadMovies : HomeIntent()
    object RefreshMovies : HomeIntent()
    object ToggleLayout : HomeIntent()
    data class ToggleFavorite(val movie: Movie) : HomeIntent()
    data class NavigateToDetails(val movie: Movie) : HomeIntent()
}
