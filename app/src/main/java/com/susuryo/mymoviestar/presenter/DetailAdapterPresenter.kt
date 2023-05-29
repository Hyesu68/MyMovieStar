package com.susuryo.mymoviestar.presenter

import android.widget.ImageView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.BuildConfig
import com.susuryo.mymoviestar.contract.DetailAdapterContract
import com.susuryo.mymoviestar.databinding.ItemDetailBinding
import com.susuryo.mymoviestar.model.DetailData
import com.susuryo.mymoviestar.network.MovieService
import com.susuryo.mymoviestar.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailAdapterPresenter(val view: DetailAdapterContract.View): DetailAdapterContract.Presenter {

    private var movieService: MovieService = RetrofitClient.movieService
    override fun getMovieDetail(movieId: Int, imageView: ImageView) {
        val call = movieService.getMovieDetails(movieId, BuildConfig.MOVIE_API_KEY)
        call.enqueue(object : Callback<DetailData> {
            override fun onResponse(call: Call<DetailData>, response: Response<DetailData>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    view.showMovieDetail(body?.posterPath, imageView)
                }
            }

            override fun onFailure(call: Call<DetailData>, t: Throwable) {
                view.showFailure()
            }
        })
    }

    override fun getUserData(uid: String, binding: ItemDetailBinding) {
        Firebase.firestore.collection("users").document(uid).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(com.susuryo.mymoviestar.model.UserData::class.java)
                    view.showUserDetail(user, binding)
                }
            }
            .addOnFailureListener {
                view.showFailure()
            }
    }
}