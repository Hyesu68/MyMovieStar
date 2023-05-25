package com.susuryo.mymoviestar.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.MainActivity
import com.susuryo.mymoviestar.data.ReviewData
import com.susuryo.mymoviestar.databinding.FragmentWriteReviewBinding

class WriteReviewFragment(val movieId: Int, val title: String?, val myReview: ReviewData?, val isNew: Boolean): BottomSheetDialogFragment() {
    private lateinit var binding: FragmentWriteReviewBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteReviewBinding.inflate(inflater, container, false)

        binding.title.text = title
        binding.editText.requestFocus()

        if (myReview != null) {
            binding.editText.setText(myReview.review)

            val vote = myReview.rating.toFloat()
            binding.rating.rating = vote
        }

        binding.register.setOnClickListener { registerReview() }

        return binding.root
    }

    private fun registerReview() {
        val uid = Firebase.auth.currentUser?.uid
        val rating = binding.rating.rating.toDouble()
        val review = binding.textInput.editText?.text.toString()
        val timestamp = FieldValue.serverTimestamp()
        val reviewData = ReviewData(movieId, uid!!, rating, review, timestamp)
        val childDocumentData = hashMapOf(
            uid to reviewData,
        )

        if (isNew) {
            Firebase.firestore.collection("reviews").document(movieId.toString()).set(childDocumentData)
                .addOnSuccessListener { addSuccess(uid) }
                .addOnFailureListener { }
        } else {
            Firebase.firestore.collection("reviews").document(movieId.toString()).update(childDocumentData.toMap())
                .addOnSuccessListener { addSuccess(uid) }
                .addOnFailureListener { }
        }
    }

    private fun addSuccess(uid: String?) {
        val id = hashMapOf("reviews" to movieId.toString(), "rating" to binding.rating.rating)
        Firebase.firestore.collection("users/$uid/reviews").document(movieId.toString()).set(id)
            .addOnSuccessListener {
                val intent = Intent(requireActivity(), MainActivity::class.java)
                intent.putExtra("my", true)
                startActivity(intent)
            }
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}