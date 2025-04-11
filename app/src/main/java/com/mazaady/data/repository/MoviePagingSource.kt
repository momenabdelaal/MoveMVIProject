package com.mazaady.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mazaady.data.api.MovieApi
import com.mazaady.domain.model.Movie
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MoviePagingSource @Inject constructor(
    private val api: MovieApi
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val page = params.key ?: 1
            val response = api.getMovies(page = page)
            
            LoadResult.Page(
                data = response.movies.map { movieDto ->
                    Movie(
                        id = movieDto.id,
                        title = movieDto.title,
                        posterUrl = if (movieDto.posterPath != null) 
                            "${Movie.IMAGE_BASE_URL}${movieDto.posterPath}" 
                        else null,
                        releaseDate = movieDto.releaseDate,
                        overview = movieDto.overview,
                        rating = movieDto.rating,
                        genreIds = movieDto.genreIds,
                        runtime = movieDto.runtime
                    )
                },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page < response.totalPages) page + 1 else null
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
