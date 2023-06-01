package com.susuryo.mymoviestar.presenter

import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.BuildConfig
import com.susuryo.mymoviestar.contract.DetailContract
import com.susuryo.mymoviestar.model.DetailData
import com.susuryo.mymoviestar.model.ReviewData
import com.susuryo.mymoviestar.network.MovieService
import com.susuryo.mymoviestar.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailPresenter(val view: DetailContract.View): DetailContract.Presenter {
    private var movieService: MovieService = RetrofitClient.movieService
    override fun getMovieDetail(movieId: Int) {
        val call = movieService.getMovieDetails(movieId, BuildConfig.MOVIE_API_KEY)
        call.enqueue(object : Callback<DetailData> {
            override fun onResponse(call: Call<DetailData>, response: Response<DetailData>) {
                if (response.isSuccessful) {
                    val body = response.body() as DetailData
                    view.showMovieDetail(body)
                }
            }

            override fun onFailure(call: Call<DetailData>, t: Throwable) {
                view.showFailure()
            }
        })
    }

    val uid = Firebase.auth.currentUser!!.uid
    override fun getMyReview(movieId: Int) {
        var myReview: ReviewData? = null
        Firebase.firestore.collection("reviews").document(movieId.toString()).get()
            .addOnSuccessListener { documentSnapshot ->
                val reviewList = mutableListOf<ReviewData>()
                if (documentSnapshot.exists()) {
                    val fieldMappings = documentSnapshot.data

                    if (fieldMappings != null) {
                        for (fieldMapping in fieldMappings.values) {
                            if (fieldMapping is Map<*, *>) {
                                val reviewData = ReviewData(
                                    movieId = fieldMapping["movieId"] as? Int ?: 0,
                                    userId = fieldMapping["userId"] as? String ?: "",
                                    rating = fieldMapping["rating"] as? Double ?: 0.0,
                                    review = fieldMapping["review"] as? String ?: "",
                                    timestamp = fieldMapping["timestamp"] as? Timestamp
                                )

                                if (reviewData.userId == uid) {
                                    myReview = reviewData
                                } else {
                                    reviewList.add(reviewData)
                                }
                            }
                        }
                    }
                }
                view.showMyReview(reviewList, myReview)
            }
            .addOnFailureListener {
                view.showFailure()
            }
    }
}