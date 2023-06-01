package com.susuryo.mymoviestar.presenter

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.BuildConfig
import com.susuryo.mymoviestar.contract.MainContract
import com.susuryo.mymoviestar.model.GenreData
import com.susuryo.mymoviestar.model.UserData
import com.susuryo.mymoviestar.network.MovieService
import com.susuryo.mymoviestar.network.RetrofitClient
import com.susuryo.mymoviestar.singleton.GenreSingleton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainPresenter(val view: MainContract.View): MainContract.Presenter {
    private var movieService: MovieService = RetrofitClient.movieService
    var genreSet = mutableMapOf<Int, String>()
    override fun getGenre() {
        val call = movieService.getGenres(BuildConfig.MOVIE_API_KEY)
        call.enqueue(object : Callback<GenreData> {
            override fun onResponse(call: Call<GenreData>, response: Response<GenreData>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val genres = body.genres
                        for (genre in genres) {
                            genreSet[genre.id!!] = genre.name!!
                        }

                        val genreSingleton = GenreSingleton
                        genreSingleton.setDataset(genreSet)
                    }
                }
                view.setHomeFragment()
            }

            override fun onFailure(call: Call<GenreData>, t: Throwable) {
                view.setHomeFragment()
            }

        })
    }

    override fun getMyProfile() {
        val uid = Firebase.auth.currentUser?.uid
        Firebase.firestore.collection("users").document(uid!!).get()
            .addOnSuccessListener {
                val user = it.toObject<UserData>()
                view.setMyProfile(user)
            }
            .addOnFailureListener {
                view.showFailure()
            }
    }
}