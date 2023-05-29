package com.susuryo.mymoviestar.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.susuryo.mymoviestar.BuildConfig
import com.susuryo.mymoviestar.singleton.GenreSingleton
import com.susuryo.mymoviestar.network.MovieService
import com.susuryo.mymoviestar.R
import com.susuryo.mymoviestar.view.activity.DetailActivity
import com.susuryo.mymoviestar.databinding.ItemHomeBinding
import com.susuryo.mymoviestar.model.NowData
import com.susuryo.mymoviestar.model.Results
import com.susuryo.mymoviestar.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeAdapter(private val context: Context) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemHomeBinding) : RecyclerView.ViewHolder(binding.root)
    private var movieService: MovieService = RetrofitClient.movieService
    private var dataSet = mutableListOf<Results>()
    private val genreSet = GenreSingleton.getDataset()

    init {
        val call = movieService.getNowPlaying(BuildConfig.MOVIE_API_KEY)
        call.enqueue(object : Callback<NowData> {
            override fun onResponse(call: Call<NowData>, response: Response<NowData>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        dataSet = body.results
                    }
                }
                notifyDataSetChanged()
            }

            override fun onFailure(call: Call<NowData>, t: Throwable) {

            }
        })
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHomeBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        with(viewHolder) {
            with(dataSet[position]) {
                binding.title.text = this.title
                binding.review.text = this.voteCount.toString()
                binding.popularity.text = this.popularity.toString()
                binding.date.text = this.releaseDate

                val stringBuffer = StringBuffer()
                for (genre in this.genreIds) {
                    stringBuffer.append(genreSet[genre])
                    stringBuffer.append(" ")
                }
                binding.genre.text = stringBuffer.toString()

                Glide.with(context)
                    .load("https://image.tmdb.org/t/p/original" + this.posterPath)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(binding.poster)

                val vote = this.voteAverage?.div(2)?.toFloat()
                if (vote != null) {
//                    binding.rating.rating = vote
                    binding.star.text = vote.toString()
                }

                binding.root.setOnClickListener {
                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra("id", this.id)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount() = dataSet.size

}