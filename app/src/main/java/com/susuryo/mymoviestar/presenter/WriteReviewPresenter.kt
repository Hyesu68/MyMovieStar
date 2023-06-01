package com.susuryo.mymoviestar.presenter

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.contract.WriteReviewContract
import com.susuryo.mymoviestar.model.ReviewData

class WriteReviewPresenter(val view: WriteReviewContract.View): WriteReviewContract.Presenter {
    val uid = Firebase.auth.currentUser?.uid

    override fun setReview(movieId: Int, rating: Double, review: String) {
        val timestamp = FieldValue.serverTimestamp()
        val reviewData = ReviewData(movieId, uid!!, rating, review, timestamp)
        val childDocumentData = hashMapOf(
            uid to reviewData,
        )

        Firebase.firestore.collection("reviews").document(movieId.toString()).set(childDocumentData)
            .addOnSuccessListener { setReviewInUser(movieId, rating) }
            .addOnFailureListener { view.showFailure()
                Log.d("setReview", it.message.toString())
            }
    }

    override fun updateReview(movieId: Int, rating: Double, review: String) {
        val timestamp = FieldValue.serverTimestamp()
        val reviewData = ReviewData(movieId, uid!!, rating, review, timestamp)
        val childDocumentData = hashMapOf(
            uid to reviewData,
        )

        Firebase.firestore.collection("reviews").document(movieId.toString()).update(childDocumentData.toMap())
            .addOnSuccessListener { setReviewInUser(movieId, rating) }
            .addOnFailureListener { view.showFailure()
                Log.d("updateReview", it.message.toString())
            }
    }

    override fun setReviewInUser(movieId: Int, rating: Double) {
        val id = hashMapOf("reviews" to movieId.toString(), "rating" to rating)
        Firebase.firestore.collection("users/$uid/reviews").document(movieId.toString()).set(id)
            .addOnSuccessListener {
                view.goToDetail()
            }
            .addOnFailureListener { view.showFailure()
                Log.d("setReviewInUser", it.message.toString())
            }
    }
}