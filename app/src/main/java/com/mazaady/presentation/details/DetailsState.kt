package com.mazaady.presentation.details

import com.mazaady.domain.model.Movie
import com.mazaady.presentation.base.MviState

data class DetailsState(
    val movie: Movie? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) : MviState
