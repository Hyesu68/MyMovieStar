package com.susuryo.mymoviestar.presenter

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.BuildConfig
import com.susuryo.mymoviestar.contract.MemberAdapterContract
import com.susuryo.mymoviestar.model.DetailData
import com.susuryo.mymoviestar.network.MovieService
import com.susuryo.mymoviestar.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MemberAdapterPresenter(val view: MemberAdapterContract.View): MemberAdapterContract.Presenter {

    override fun getReviews(uid: String?) {
        Firebase.firestore.collection("users/$uid/reviews").get()
            .addOnSuccessListener { documents ->
                view.showReviews(documents)
            }
            .addOnFailureListener {
                view.showFailure()
            }
    }

    private var movieService: MovieService = RetrofitClient.movieService
    override fun getPosters(id: String?) {
        val call = movieService.getMovieDetails(id!!.toInt(), BuildConfig.MOVIE_API_KEY)
        call.enqueue(object : Callback<DetailData> {
            override fun onResponse(call: Call<DetailData>, response: Response<DetailData>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    view.showPosters(body?.posterPath)
                } else {
                    view.showFailure()
                }
            }

            override fun onFailure(call: Call<DetailData>, t: Throwable) {
                view.showFailure()
            }
        })
    }
}