package com.mazaady.presentation.home

import androidx.paging.PagingData
import com.mazaady.domain.model.Movie
import com.mazaady.presentation.base.MviState
import kotlinx.coroutines.flow.Flow

data class HomeState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isGrid: Boolean = false,
    val movies: Flow<PagingData<Movie>>? = null
) : MviState
