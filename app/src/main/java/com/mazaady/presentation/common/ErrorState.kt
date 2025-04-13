package com.mazaady.presentation.common

import androidx.annotation.StringRes
import com.mazaady.R

sealed class ErrorState(
    @StringRes val titleRes: Int,
    @StringRes val messageRes: Int
) {
    object NoInternet : ErrorState(
        R.string.error_no_internet,
        R.string.error_no_internet_message
    )

    object ServerError : ErrorState(
        R.string.error_server,
        R.string.error_server_message
    )

    object EmptyFavorites : ErrorState(
        R.string.error_empty_favorites,
        R.string.error_empty_favorites_message
    )

    object EmptyMovies : ErrorState(
        R.string.error_empty_movies,
        R.string.error_empty_movies_message
    )
}
