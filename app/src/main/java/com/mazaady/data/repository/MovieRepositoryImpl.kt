package com.mazaady.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.mazaady.data.api.MovieApiService
import com.mazaady.data.db.MovieDao
import com.mazaady.data.db.MovieEntity
import com.mazaady.data.paging.MovieRemoteMediator
import com.mazaady.data.util.NetworkResult
import com.mazaady.data.util.safeApiCall
import com.mazaady.domain.model.Movie
import com.mazaady.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val api: MovieApiService,
    private val dao: MovieDao
) : MovieRepository {

    companion object {
        const val PAGE_SIZE = 20
        const val PREFETCH_DISTANCE = PAGE_SIZE
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getMovies(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = PREFETCH_DISTANCE
            ),
            remoteMediator = MovieRemoteMediator(api, dao),
            pagingSourceFactory = { dao.getMovies() }
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomainModel() }
        }
    }

    override fun getFavoriteMovies(): Flow<NetworkResult<List<Movie>>> = dao.getFavoriteMovies()
        .map { entities -> entities.map { it.toDomainModel() } }
        .map<List<Movie>, NetworkResult<List<Movie>>> { movies -> NetworkResult.Success(movies) }
        .catch { e ->
            Timber.e(e, "Error getting favorite movies")
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }

    override suspend fun toggleFavorite(movie: Movie): NetworkResult<Unit> {
        return try {
            val existingMovie = dao.getMovie(movie.id)
            if (existingMovie == null) {
                // If movie doesn't exist in DB, insert it with favorite status
                dao.insertMovies(listOf(MovieEntity.fromDomainModel(movie.copy(isFavorite = true))))
                Timber.d("Inserted new favorite movie ${movie.id}")
            } else {
                // If movie exists, use the transaction to toggle its favorite status
                dao.toggleFavorite(movie.id)
                Timber.d("Toggled favorite status for movie ${movie.id}")
            }
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error toggling favorite for movie ${movie.id}")
            NetworkResult.Error("Failed to update favorite status")
        }
    }

    override suspend fun getMovieDetails(movieId: Int): NetworkResult<Movie> {
        return try {
            val movieDto = api.getMovieDetails(movieId)
            val isFavorite = dao.getFavoriteStatus(movieId) ?: false
            NetworkResult.Success(movieDto.toDomainModel().copy(isFavorite = isFavorite))
        } catch (e: Exception) {
            Timber.e(e, "Error getting movie details for movie $movieId")
            NetworkResult.Error(e.message ?: "Failed to get movie details")
        }
    }

    override fun isMovieFavorite(movieId: Int): Flow<Boolean> {
        return dao.isMovieFavorite(movieId)
    }
}
