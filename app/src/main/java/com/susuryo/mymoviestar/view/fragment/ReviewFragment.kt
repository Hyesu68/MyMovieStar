package com.susuryo.mymoviestar.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.susuryo.mymoviestar.contract.ReviewContract
import com.susuryo.mymoviestar.view.adapter.DetailAdapter
import com.susuryo.mymoviestar.model.ReviewData
import com.susuryo.mymoviestar.databinding.FragmentReviewBinding
import com.susuryo.mymoviestar.presenter.ReviewPresenter

class ReviewFragment: Fragment(), ReviewContract.View {
    private lateinit var binding: FragmentReviewBinding
    private lateinit var detailAdapter: DetailAdapter
    private val presenter: ReviewContract.Presenter = ReviewPresenter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewBinding.inflate(inflater, container, false)
        getMyReview()
        return binding.root
    }

    private fun getMyReview() {
        presenter.getReviews()
    }

    override fun showReviews(reviewList: MutableList<ReviewData>) {
        binding.progressBar.visibility = View.GONE

        detailAdapter = DetailAdapter(requireContext(), reviewList, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = detailAdapter
    }

    override fun showFailure() {
        Toast.makeText(requireContext(), "There was an issue encountered", Toast.LENGTH_SHORT).show()
    }
}