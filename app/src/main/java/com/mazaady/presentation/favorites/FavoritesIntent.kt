package com.mazaady.presentation.favorites

import com.mazaady.domain.model.Movie
import com.mazaady.presentation.base.MviIntent

sealed class FavoritesIntent : MviIntent {
    object LoadFavorites : FavoritesIntent()
    object ToggleLayout : FavoritesIntent()
    data class ToggleFavorite(val movie: Movie) : FavoritesIntent()
    data class NavigateToDetails(val movie: Movie) : FavoritesIntent()
}
