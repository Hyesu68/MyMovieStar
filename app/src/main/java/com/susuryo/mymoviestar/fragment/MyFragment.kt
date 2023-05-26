package com.susuryo.mymoviestar.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.MemberAdapter
import com.susuryo.mymoviestar.data.ReviewData
import com.susuryo.mymoviestar.data.UserData
import com.susuryo.mymoviestar.databinding.FragmentMyBinding
import java.text.DecimalFormat

class MyFragment: Fragment() {
    private lateinit var binding: FragmentMyBinding
    private lateinit var memberAdapter: MemberAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyBinding.inflate(inflater, container, false)

        val uid = Firebase.auth.currentUser?.uid
        getProfile(uid)
        memberAdapter = MemberAdapter(uid, requireContext())
        binding.gridView.adapter = memberAdapter
        binding.titleBar.visibility = View.GONE
        binding.follow.visibility = View.GONE

        return binding.root
    }

    private fun getProfile(uid: String?) {
        Firebase.firestore.collection("users").document(uid!!).get()
            .addOnSuccessListener {documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(UserData::class.java)
                    Glide.with(requireContext())
                        .load(user?.profile)
                        .circleCrop()
                        .into(binding.profile)
                    binding.nickname.text = user?.nickname

                    getReviews(documentSnapshot)
                    getFollowers(documentSnapshot)
                }
            }
            .addOnFailureListener {

            }
    }

    private fun getFollowers(documentSnapshot: DocumentSnapshot) {
        documentSnapshot.reference.collection("follower").get()
            .addOnSuccessListener {
                val size = it.size()
                binding.follower.text = size.toString()
            }
            .addOnFailureListener {  }
    }

    private fun getReviews(documentSnapshot: DocumentSnapshot) {
        documentSnapshot.reference.collection("reviews").get()
            .addOnSuccessListener {
                val size = it.size()
                binding.review.text = size.toString()

                var rating = 0.0
                val documents = it.documents
                for (document in documents) {
                    val fieldMappings = document.data
                    if (fieldMappings != null) {
                        for (fieldMapping in fieldMappings) {
                            if (fieldMapping.key.equals("rating")) {
                                rating += fieldMapping.value as? Double ?: 0.0
                            }
                        }
                        rating /= fieldMappings.size
                    }
                }

                val decimalFormat = DecimalFormat("#.0")
                val formattedNumber = decimalFormat.format(rating)
                binding.star.text = formattedNumber.toString()
            }
    }
}