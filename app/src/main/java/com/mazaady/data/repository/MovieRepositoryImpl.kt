package com.mazaady.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.mazaady.data.api.MovieApi
import com.mazaady.data.db.MovieDao
import com.mazaady.data.model.MovieDto
import com.mazaady.domain.model.Movie
import com.mazaady.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: MovieApi,
    private val dao: MovieDao,
    private val moviePagingSource: MoviePagingSource
) : MovieRepository {

    override fun getMovies(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = { moviePagingSource }
        ).flow
    }

    override suspend fun getMovieDetails(movieId: Int): Movie {
        val response = api.getMovieDetails(movieId)
        return response.movies.first().toDomainModel()
    }

    override suspend fun toggleFavorite(movie: Movie) {
        if (dao.isMovieFavorite(movie.id).first()) {
            dao.deleteMovie(movie.toEntity())
        } else {
            dao.insertMovie(movie.toEntity())
        }
    }

    override fun getFavoriteMovies(): Flow<List<Movie>> {
        return dao.getFavoriteMovies().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun isMovieFavorite(movieId: Int): Flow<Boolean> {
        return dao.isMovieFavorite(movieId)
    }

    private fun MovieDto.toDomainModel() = Movie(
        id = id,
        title = title,
        posterUrl = if (posterPath != null) "${Movie.IMAGE_BASE_URL}$posterPath" else null,
        releaseDate = releaseDate,
        overview = overview,
        rating = rating,
        genreIds = genreIds,
        runtime = runtime
    )
}
