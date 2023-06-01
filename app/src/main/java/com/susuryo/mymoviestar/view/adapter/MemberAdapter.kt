package com.susuryo.mymoviestar.view.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.firebase.firestore.QuerySnapshot
import com.susuryo.mymoviestar.contract.MemberAdapterContract
import com.susuryo.mymoviestar.view.activity.DetailActivity
import com.susuryo.mymoviestar.databinding.ItemMemberBinding
import com.susuryo.mymoviestar.presenter.MemberAdapterPresenter

class MemberAdapter(uid: String?, val context: Context, val view: View): RecyclerView.Adapter<MemberAdapter.ViewHolder>(),
    MemberAdapterContract.View {
    class ViewHolder(val binding: ItemMemberBinding): RecyclerView.ViewHolder(binding.root)
    private var dataSet = mutableListOf<String>()
    private val presenter: MemberAdapterContract.Presenter = MemberAdapterPresenter(this)

    init {
        presenter.getReviews(uid)
    }

    override fun showReviews(documents: QuerySnapshot) {
        dataSet.clear()
        for (document in documents) {
            dataSet.add(document.id)
        }

        if (dataSet.size == 0) {
            view.visibility = View.GONE
        }

        profile.clear()
        for (i in dataSet) {
            presenter.getPosters(i)
        }
    }

    override fun showFailure() {
        Toast.makeText(context, "There was an issue encountered", Toast.LENGTH_SHORT).show()
        notifyDataSetChanged()
    }

    private var profile = mutableListOf<String?>()
    override fun showPosters(path: String?) {
        profile.add(path)
        notifyDataSetChanged()
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