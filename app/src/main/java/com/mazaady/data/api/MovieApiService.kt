package com.mazaady.data.api

import com.mazaady.data.model.MovieDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {
    @GET("movies")
    suspend fun getMovies(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 20
    ): List<MovieDto>

    @GET("movies/{id}")
    suspend fun getMovieDetails(
        @Path("id") movieId: Int
    ): MovieDto
}
