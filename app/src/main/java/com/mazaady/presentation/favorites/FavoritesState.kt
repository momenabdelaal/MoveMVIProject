package com.mazaady.presentation.favorites

import com.mazaady.domain.model.Movie
import com.mazaady.presentation.base.MviState

data class FavoritesState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val movies: List<Movie> = emptyList(),
    val isGrid: Boolean = false
) : MviState
