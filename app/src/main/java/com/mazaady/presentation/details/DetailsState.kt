package com.mazaady.presentation.details

import com.mazaady.domain.model.Movie
import com.mazaady.presentation.base.MviState

data class DetailsState(
    val isLoading: Boolean = false,
    val movie: Movie? = null,
    val error: String? = null
) : MviState
