package com.susuryo.mymoviestar

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    const val API_KEY = BuildConfig.MOVIE_API_KEY

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val movieService: MovieService by lazy {
        retrofit.create(MovieService::class.java)
    }

}