package com.susuryo.mymoviestar.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.BuildConfig
import com.susuryo.mymoviestar.DetailAdapter
import com.susuryo.mymoviestar.MovieService
import com.susuryo.mymoviestar.RetrofitClient
import com.susuryo.mymoviestar.data.DetailData
import com.susuryo.mymoviestar.data.ReviewData
import com.susuryo.mymoviestar.databinding.FragmentReviewBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewFragment: Fragment() {
    private lateinit var binding: FragmentReviewBinding
    private lateinit var detailAdapter: DetailAdapter
    private val posterMap = mutableMapOf<Int, String?>()
    private var movieService: MovieService = RetrofitClient.movieService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewBinding.inflate(inflater, container, false)
        getMyReview()
        return binding.root
    }

    private val reviewList = mutableListOf<ReviewData>()
    private val movieId = mutableSetOf<Int>()
    private fun getMyReview() {
        Firebase.firestore.collection("reviews").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val fieldMappings = document.data
                    if (fieldMappings != null) {
                        for (fieldMapping in fieldMappings.values) {
                            if (fieldMapping is Map<*, *>) {
                                val mId = fieldMapping["movieId"] as? Long ?: 0
                                val reviewData = ReviewData(
                                    movieId = mId.toInt(),
                                    userId = fieldMapping["userId"] as? String ?: "",
                                    rating = fieldMapping["rating"] as? Double ?: 0.0,
                                    review = fieldMapping["review"] as? String ?: "",
                                    timestamp = fieldMapping["timestamp"]
                                )

                                movieId.add(reviewData.movieId)
                                reviewList.add(reviewData)
                            }
                        }
                    }
                }

                movieId.forEach { movieId ->
                    getMovieDetail(movieId)
                }
            }
            .addOnFailureListener {

            }
    }

    private var completedRequestsCount = 0
    private fun getMovieDetail(movieId: Int) {
        val call = movieService.getMovieDetails(movieId, BuildConfig.MOVIE_API_KEY)
        call.enqueue(object : Callback<DetailData> {
            override fun onResponse(call: Call<DetailData>, response: Response<DetailData>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    posterMap[movieId] = body?.posterPath
                }
                checkAllRequestsCompleted()
            }

            override fun onFailure(call: Call<DetailData>, t: Throwable) {

            }
        })
    }

    private fun checkAllRequestsCompleted() {
        completedRequestsCount++
        if (completedRequestsCount == movieId.size) {
            if (reviewList.isNotEmpty()) {
                detailAdapter = DetailAdapter(reviewList, false, posterMap)
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerView.adapter = detailAdapter
            }
        }
    }
}