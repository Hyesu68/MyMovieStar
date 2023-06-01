package com.susuryo.mymoviestar.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.susuryo.mymoviestar.singleton.GenreSingleton
import com.susuryo.mymoviestar.R
import com.susuryo.mymoviestar.contract.HomeAdapterContract
import com.susuryo.mymoviestar.view.activity.DetailActivity
import com.susuryo.mymoviestar.databinding.ItemHomeBinding
import com.susuryo.mymoviestar.model.Results
import com.susuryo.mymoviestar.presenter.HomeAdapterPresenter

class HomeAdapter(private val context: Context, private val progressBar: ProgressBar) : RecyclerView.Adapter<HomeAdapter.ViewHolder>(),
    HomeAdapterContract.View {
    class ViewHolder(val binding: ItemHomeBinding) : RecyclerView.ViewHolder(binding.root)
    private var dataSet = mutableListOf<Results>()
    private val genreSet = GenreSingleton.getDataset()
    private val presenter: HomeAdapterContract.Presenter = HomeAdapterPresenter(this)

    override fun showNowPlaying(dataSet: MutableList<Results>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
        progressBar.visibility = View.GONE
    }

    override fun showFailure() {
        progressBar.visibility = View.GONE
        Toast.makeText(context, "There was an issue encountered", Toast.LENGTH_SHORT).show()
    }

    init {
        presenter.getNowPlaying()
    }

    fun getQuery(query: String) {
        presenter.getQuery(query)
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