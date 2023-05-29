package com.susuryo.mymoviestar.presenter

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.contract.ReviewContract
import com.susuryo.mymoviestar.model.ReviewData

class ReviewPresenter(val view: ReviewContract.View): ReviewContract.Presenter {
    override fun getReviews() {
        Firebase.firestore.collection("reviews").get()
            .addOnSuccessListener { querySnapshot ->
                val tempList = mutableListOf<ReviewData>()
                for (document in querySnapshot) {
                    val fieldMappings = document.data
                    for (fieldMapping in fieldMappings.values) {
                        if (fieldMapping is Map<*, *>) {
                            val mId = fieldMapping["movieId"] as? Long ?: 0
                            val reviewData = ReviewData(
                                movieId = mId.toInt(),
                                userId = fieldMapping["userId"] as? String ?: "",
                                rating = fieldMapping["rating"] as? Double ?: 0.0,
                                review = fieldMapping["review"] as? String ?: "",
                                timestamp = fieldMapping["timestamp"] as? Timestamp
                            )

                            tempList.add(reviewData)
                        }
                    }
                }

                val reviewList = mutableListOf<ReviewData>()
                reviewList.addAll(tempList.sortedBy { it.timestamp.toString() })
                reviewList.reverse()
                view.showReviews(reviewList)
            }
            .addOnFailureListener {
                view.showFailure()
            }
    }
}