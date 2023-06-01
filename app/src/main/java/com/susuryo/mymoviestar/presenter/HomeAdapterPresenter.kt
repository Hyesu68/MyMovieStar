package com.susuryo.mymoviestar.presenter

import com.susuryo.mymoviestar.BuildConfig
import com.susuryo.mymoviestar.contract.HomeAdapterContract
import com.susuryo.mymoviestar.model.NowData
import com.susuryo.mymoviestar.model.QueryData
import com.susuryo.mymoviestar.network.MovieService
import com.susuryo.mymoviestar.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeAdapterPresenter(val view: HomeAdapterContract.View): HomeAdapterContract.Presenter {

    private var movieService: MovieService = RetrofitClient.movieService
    override fun getNowPlaying() {
        val call = movieService.getNowPlaying(BuildConfig.MOVIE_API_KEY)
        call.enqueue(object : Callback<NowData> {
            override fun onResponse(call: Call<NowData>, response: Response<NowData>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        view.showNowPlaying(body.results)
                    } else {
                        view.showFailure()
                    }
                } else {
                    view.showFailure()
                }
            }

            override fun onFailure(call: Call<NowData>, t: Throwable) {
                view.showFailure()
            }
        })
    }

    override fun getQuery(query: String?) {
        val call = movieService.getMovieSearch(query!!, BuildConfig.MOVIE_API_KEY)
        call.enqueue(object : Callback<QueryData> {
            override fun onResponse(call: Call<QueryData>, response: Response<QueryData>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        view.showNowPlaying(body.results)
                    } else {
                        view.showFailure()
                    }
                } else {
                    view.showFailure()
                }
            }

            override fun onFailure(call: Call<QueryData>, t: Throwable) {
                view.showFailure()
            }
        })
    }
}