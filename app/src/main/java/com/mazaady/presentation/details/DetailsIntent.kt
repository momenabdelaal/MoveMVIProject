package com.mazaady.presentation.details

import com.mazaady.domain.model.Movie
import com.mazaady.presentation.base.MviIntent

sealed class DetailsIntent : MviIntent {
    data class LoadMovie(val movie: Movie) : DetailsIntent()
    data class ToggleFavorite(val movie: Movie) : DetailsIntent()
}
