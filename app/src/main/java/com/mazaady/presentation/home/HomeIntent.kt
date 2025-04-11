package com.mazaady.presentation.home

import com.mazaady.domain.model.Movie
import com.mazaady.presentation.base.MviIntent

sealed class HomeIntent : MviIntent {
    object LoadMovies : HomeIntent()
    data class ToggleViewType(val isGrid: Boolean) : HomeIntent()
    data class ToggleFavorite(val movie: Movie) : HomeIntent()
    data class NavigateToDetails(val movieId: Int) : HomeIntent()
}
