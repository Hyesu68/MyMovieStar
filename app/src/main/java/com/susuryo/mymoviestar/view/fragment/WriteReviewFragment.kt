package com.susuryo.mymoviestar.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.contract.WriteReviewContract
import com.susuryo.mymoviestar.view.activity.DetailActivity
import com.susuryo.mymoviestar.model.ReviewData
import com.susuryo.mymoviestar.databinding.FragmentWriteReviewBinding
import com.susuryo.mymoviestar.presenter.WriteReviewPresenter

class WriteReviewFragment(private val movieId: Int, val title: String?, private val myReview: ReviewData?, val isNew: Boolean): BottomSheetDialogFragment(), WriteReviewContract.View {
    private lateinit var binding: FragmentWriteReviewBinding
    private val presenter: WriteReviewContract.Presenter = WriteReviewPresenter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteReviewBinding.inflate(inflater, container, false)

        binding.title.text = title
        binding.editText.requestFocus()

        setMyReview(myReview)

        binding.register.setOnClickListener { registerReview() }

        return binding.root
    }

    private fun setMyReview(myReview: ReviewData?) {
        if (myReview != null) {
            binding.editText.setText(myReview.review)

            val vote = myReview.rating.toFloat()
            binding.rating.rating = vote
        }
    }

    private fun registerReview() {
        val rating = binding.rating.rating.toDouble()
        val review = binding.textInput.editText?.text.toString()

        if (isNew) {
            presenter.setReview(movieId, rating, review)
        } else {
            presenter.updateReview(movieId, rating, review)
        }
    }

    override fun goToDetail() {
        val intent = Intent(requireActivity(), DetailActivity::class.java)
        intent.putExtra("id", movieId)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun showFailure() {
        Toast.makeText(requireContext(), "There was an issue encountered", Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}