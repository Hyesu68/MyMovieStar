package com.susuryo.mymoviestar.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.R
import com.susuryo.mymoviestar.contract.MyContract
import com.susuryo.mymoviestar.view.adapter.MemberAdapter
import com.susuryo.mymoviestar.model.UserData
import com.susuryo.mymoviestar.databinding.FragmentMyBinding
import com.susuryo.mymoviestar.presenter.MyPresenter
import java.text.DecimalFormat

class MyFragment: Fragment(), MyContract.View {
    private lateinit var binding: FragmentMyBinding
    private lateinit var memberAdapter: MemberAdapter
    private val presenter: MyContract.Presenter = MyPresenter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyBinding.inflate(inflater, container, false)

        val uid = Firebase.auth.currentUser?.uid
        getProfile(uid)
        memberAdapter = MemberAdapter(uid, requireContext(), binding.progressBar)
        binding.gridView.adapter = memberAdapter
        binding.titleBar.visibility = View.GONE
        binding.follow.visibility = View.GONE

        return binding.root
    }

    private fun getProfile(uid: String?) {
        presenter.getUserData(uid)
    }

    override fun showProfile(user: UserData?) {
        Glide.with(requireContext())
            .load(user?.profile)
            .circleCrop()
            .placeholder(R.drawable.placeholder_round)
            .into(binding.profile)
        binding.nickname.text = user?.nickname
    }

    override fun showFollowers(it: QuerySnapshot) {
        val size = it.size()
        binding.follower.text = size.toString()
    }

    override fun showReviews(it: QuerySnapshot) {
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
        if (size > 0) {
            binding.star.text = formattedNumber.toString()
        } else {
            binding.star.text = "0"
        }
    }

    override fun showFail() {
        Toast.makeText(requireContext(), "There was an issue encountered", Toast.LENGTH_SHORT).show()
    }

}