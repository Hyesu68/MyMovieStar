package com.susuryo.mymoviestar.view.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.BuildConfig
import com.susuryo.mymoviestar.network.MovieService
import com.susuryo.mymoviestar.view.activity.DetailActivity
import com.susuryo.mymoviestar.databinding.ItemMemberBinding
import com.susuryo.mymoviestar.model.DetailData
import com.susuryo.mymoviestar.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MemberAdapter(uid: String?, val context: Context, val view: View): RecyclerView.Adapter<MemberAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemMemberBinding): RecyclerView.ViewHolder(binding.root)
    private var dataSet = mutableListOf<String>()

    init {
        Firebase.firestore.collection("users/$uid/reviews").get()
            .addOnSuccessListener { documents ->
                dataSet.clear()
                for (document in documents) {
                    dataSet.add(document.id)
                }

                if (dataSet.size == 0) {
                    view.visibility = View.GONE
                }
                getPosters()
            }
            .addOnFailureListener {

            }
    }

    private var movieService: MovieService = RetrofitClient.movieService
    private var profile = mutableListOf<String?>()

    private fun getPosters() {
        profile.clear()
        for (i in dataSet) {
            val call = movieService.getMovieDetails(i.toInt(), BuildConfig.MOVIE_API_KEY)
            call.enqueue(object : Callback<DetailData> {
                override fun onResponse(call: Call<DetailData>, response: Response<DetailData>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        profile.add(body?.posterPath)
                    }

                    notifyDataSetChanged()
                }

                override fun onFailure(call: Call<DetailData>, t: Throwable) {
                    notifyDataSetChanged()
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMemberBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(profile[position]) {
                val uri = "https://image.tmdb.org/t/p/original$this"
                Glide.with(context)
                    .load(uri)
                    .apply(RequestOptions().fitCenter())
                    .centerCrop()
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            view.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            view.visibility = View.GONE
                            return false
                        }
                    })
                    .into(binding.poster)

                itemView.setOnClickListener {
                    moveToDetail(itemView.context, dataSet[position].toInt())
                }
            }
        }
    }

    private fun moveToDetail(context: Context, movieId: Int) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra("id", movieId)
        context.startActivity(intent)
    }

    override fun getItemCount() = profile.size
}