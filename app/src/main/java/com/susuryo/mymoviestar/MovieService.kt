package com.susuryo.mymoviestar

import com.susuryo.mymoviestar.data.DetailData
import com.susuryo.mymoviestar.data.GenreData
import com.susuryo.mymoviestar.data.LatestData
import com.susuryo.mymoviestar.data.NowData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {
    @GET("movie/latest")
    fun getLatestMovie(@Query("api_key") apiKey: String): Call<LatestData>

    @GET("movie/now_playing")
    fun getNowPlaying(@Query("api_key") apiKey: String): Call<NowData>

    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Call<DetailData>

    @GET("genre/movie/list")
    fun getGenres(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): Call<GenreData>

}