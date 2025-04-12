package com.mazaady.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.mazaady.data.api.MovieApiService
import com.mazaady.data.db.MovieDao
import com.mazaady.data.db.MovieEntity
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator @Inject constructor(
    private val api: MovieApiService,
    private val dao: MovieDao
) : RemoteMediator<Int, MovieEntity>() {

    override suspend fun initialize(): InitializeAction {
        return if (dao.getMovieCount() > 0) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieEntity>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    lastItem.id
                }
            }

            Timber.d("Loading movies: loadType=$loadType, loadKey=$loadKey")

            try {
                val response = api.getMovies(limit = state.config.pageSize)
                val movies = response.map { MovieEntity.fromDomainModel(it.toDomainModel()) }
                Timber.d("Loaded ${movies.size} movies")

                if (loadType == LoadType.REFRESH) {
                    dao.clearMovies()
                }
                dao.insertMovies(movies)

                MediatorResult.Success(
                    endOfPaginationReached = movies.size < state.config.pageSize
                )
            } catch (e: Exception) {
                Timber.e(e, "Error loading movies")
                MediatorResult.Error(e)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in load operation")
            MediatorResult.Error(e)
        }
    }
}
