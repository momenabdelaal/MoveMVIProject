package com.mazaady.presentation.home

import androidx.paging.PagingData
import com.mazaady.domain.model.Movie
import com.mazaady.presentation.base.MviState

data class HomeState(
    val movies: PagingData<Movie> = PagingData.empty(),
    val isGrid: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) : MviState
