package com.susuryo.mymoviestar.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.susuryo.mymoviestar.R
import com.susuryo.mymoviestar.contract.DetailContract
import com.susuryo.mymoviestar.view.adapter.DetailAdapter
import com.susuryo.mymoviestar.model.DetailData
import com.susuryo.mymoviestar.model.ReviewData
import com.susuryo.mymoviestar.databinding.ActivityDetailBinding
import com.susuryo.mymoviestar.presenter.DetailPresenter
import com.susuryo.mymoviestar.view.fragment.WriteReviewFragment
import java.text.SimpleDateFormat
import java.util.Locale

class DetailActivity : AppCompatActivity(), DetailContract.View {
    private lateinit var binding: ActivityDetailBinding
    private var title: String? = null
    private lateinit var detailAdapter: DetailAdapter
    private val presenter: DetailContract.Presenter = DetailPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieId = intent.getIntExtra("id", 0)

        getMovieDetail(movieId)
        getMyReview(movieId)

        binding.button.setOnClickListener {
            val writeReviewFragment = WriteReviewFragment(movieId, title, myReview, isNew)
            writeReviewFragment.show(supportFragmentManager, WriteReviewFragment.TAG)
        }
        binding.toolBar.setNavigationOnClickListener { finish() }
    }

    override fun showMovieDetail(detailData: DetailData) {
        title = detailData.title
        binding.toolBar.title = detailData.title
        binding.title.text = detailData.title
        binding.overview.text = detailData.overview
        binding.releaseDate.text = detailData.releaseDate
        binding.review.text = detailData.voteAverage.toString()
        binding.popularity.text = detailData.popularity.toString()

        Glide.with(applicationContext)
            .load("https://image.tmdb.org/t/p/original" + detailData.posterPath)
            .placeholder(R.drawable.placeholder)
            .into(binding.poster)

        val vote = detailData.voteAverage?.div(2)?.toFloat()
        if (vote != null) {
            binding.star.text = vote.toString()
        }

        val stringBuffer = StringBuffer()
        for (genre in detailData.genres!!) {
            stringBuffer.append(genre.name)
            stringBuffer.append(" ")
        }
        binding.genre.text = stringBuffer.toString()
    }

    private var isNew = true
    private var myReview: ReviewData? = null
    override fun showMyReview(reviewList: MutableList<ReviewData>, myReview: ReviewData?) {
        if (reviewList.isNotEmpty()) {
            detailAdapter = DetailAdapter(applicationContext, reviewList, true)
            binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
            binding.recyclerView.adapter = detailAdapter
        }

        if (myReview != null) {
            isNew = false
            this.myReview = myReview

            binding.myReview.visibility = View.VISIBLE
            binding.myReviewText.text = myReview.review
            val vote = myReview.rating.toFloat()
            binding.myRating.rating = vote

            val timestamp = myReview.timestamp as Timestamp
            val date = timestamp.toDate()
            val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(date)
            binding.date.text = time
        }

        binding.progressBar.visibility = View.GONE
    }

    private fun getMovieDetail(movieId: Int) {
        presenter.getMovieDetail(movieId)
    }

    private fun getMyReview(movieId: Int) {
        presenter.getMyReview(movieId)
    }

    override fun showFailure() {
        Toast.makeText(applicationContext, "There was an issue encountered", Toast.LENGTH_SHORT).show()
    }
}
