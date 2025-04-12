package com.mazaady.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mazaady.data.api.MovieApiService
import com.mazaady.data.model.MovieDto
import com.mazaady.data.util.NetworkResult
import com.mazaady.data.util.safeApiCall
import com.mazaady.domain.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class MoviePagingSource @Inject constructor(
    private val api: MovieApiService
) : PagingSource<Int, Movie>() {

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val offset = params.key ?: 0
            val loadSize = if (params.loadSize > MAX_LIMIT) MAX_LIMIT else params.loadSize

            Timber.d("Loading movies: offset=$offset, loadSize=$loadSize")

            withContext(Dispatchers.IO) {
                when (val result = safeApiCall<List<MovieDto>> { api.getMovies(offset = offset, limit = loadSize) }) {
                    is NetworkResult.Success<List<MovieDto>> -> {
                        val movies = result.data.map { dto -> dto.toDomainModel() }
                        Timber.d("Loaded ${movies.size} movies")

                        val nextKey = if (movies.isEmpty()) {
                            Timber.d("No more movies to load")
                            null
                        } else {
                            Timber.d("Next offset: ${offset + loadSize}")
                            offset + loadSize
                        }

                        LoadResult.Page(
                            data = movies,
                            prevKey = if (offset == 0) null else offset - loadSize,
                            nextKey = nextKey
                        )
                    }
                    is NetworkResult.Error -> {
                        when {
                            result.code == 401 -> LoadResult.Error(
                                HttpException(
                                    retrofit2.Response.error<Any>(
                                        401,
                                        "Unauthorized".toResponseBody("text/plain".toMediaTypeOrNull())
                                    )
                                )
                            )
                            result.message.contains("internet") -> LoadResult.Error(IOException("No internet connection"))
                            else -> LoadResult.Error(Exception(result.message))
                        }
                    }
                    NetworkResult.Loading -> LoadResult.Error(Exception("Loading state not supported in PagingSource"))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error loading page: offset=${params.key}, loadSize=${params.loadSize}")
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val MAX_LIMIT = 60 // Maximum number of items per request
    }
}
