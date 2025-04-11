package com.mazaady.data.api

import com.mazaady.data.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {
    @GET("discover/movie")
    suspend fun getMovies(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): MovieResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): MovieResponse
}
