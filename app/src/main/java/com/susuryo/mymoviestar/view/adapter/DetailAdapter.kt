package com.susuryo.mymoviestar.view.adapter

import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.BuildConfig
import com.susuryo.mymoviestar.network.MovieService
import com.susuryo.mymoviestar.R
import com.susuryo.mymoviestar.contract.DetailAdapterContract
import com.susuryo.mymoviestar.view.activity.DetailActivity
import com.susuryo.mymoviestar.view.activity.MemberActivity
import com.susuryo.mymoviestar.databinding.ItemDetailBinding
import com.susuryo.mymoviestar.model.DetailData
import com.susuryo.mymoviestar.model.ReviewData
import com.susuryo.mymoviestar.model.UserData
import com.susuryo.mymoviestar.network.RetrofitClient
import com.susuryo.mymoviestar.presenter.DetailAdapterPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class DetailAdapter(val context: Context, val dataSet: MutableList<ReviewData>, val isDetail: Boolean, val posterMap: MutableMap<Int, String?>?): RecyclerView.Adapter<DetailAdapter.ViewHolder>(),
    DetailAdapterContract.View {
    class ViewHolder(val binding: ItemDetailBinding): RecyclerView.ViewHolder(binding.root)
    private val presenter: DetailAdapterContract.Presenter = DetailAdapterPresenter(this)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(dataSet[position]) {
                binding.review.text = this.review

                val vote = this.rating.toFloat()
                binding.rating.rating = vote

                val timestamp = this.timestamp as Timestamp
                val date = timestamp.toDate()
                val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(date)
                binding.date.text = time

                if (isDetail) {
                    binding.poster.visibility = View.GONE
                } else {
                    getMovieDetail(this.movieId, binding.poster)
                    binding.review.setOnClickListener { moveToDetail(itemView.context, this.movieId) }
                    binding.ratingLayout.setOnClickListener { moveToDetail(itemView.context, this.movieId) }
                }

                presenter.getUserData(this.userId, binding)

                binding.poster.setOnClickListener { moveToDetail(itemView.context, this.movieId) }
                binding.profile.setOnClickListener { moveToMember(itemView.context, this.userId) }
                binding.nickname.setOnClickListener { moveToMember(itemView.context, this.userId) }
            }
        }
    }

    override fun showUserDetail(user: UserData?, binding: ItemDetailBinding) {
        Glide.with(context)
            .load(user?.profile)
            .circleCrop()
            .placeholder(R.drawable.placeholder_round)
            .into(binding.profile)
        binding.nickname.text = user?.nickname
    }

    private fun getMovieDetail(movieId: Int, imageView: ImageView) {
        presenter.getMovieDetail(movieId, imageView)
    }

    override fun showMovieDetail(posterPath: String?, imageView: ImageView) {
        Glide.with(context)
            .load("https://image.tmdb.org/t/p/original$posterPath")
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .into(imageView)
    }

    private fun moveToDetail(context: Context, movieId: Int) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra("id", movieId)
        context.startActivity(intent)
    }

    private fun moveToMember(context: Context, uid: String) {
        val intent = Intent(context, MemberActivity::class.java)
        intent.putExtra("id", uid)
        context.startActivity(intent)
    }

    override fun getItemCount() = dataSet.size

    override fun showFailure() {
        Toast.makeText(context, "There was an issue encountered", Toast.LENGTH_SHORT).show()
    }
}