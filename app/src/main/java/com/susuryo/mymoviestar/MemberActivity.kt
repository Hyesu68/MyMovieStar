package com.susuryo.mymoviestar

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.data.DetailData
import com.susuryo.mymoviestar.databinding.ActivityMemberBinding
import com.susuryo.mymoviestar.databinding.ItemMemberBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.atomic.AtomicInteger

class MemberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMemberBinding
    private lateinit var memberAdapter: MemberAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uid = intent.getStringExtra("uid")
        memberAdapter = MemberAdapter(uid, applicationContext)
//        binding.gridView.layoutManager = LinearLayoutManager(applicationContext)
        binding.gridView.adapter = memberAdapter
    }

}

class MemberAdapter(uid: String?, val context: Context): RecyclerView.Adapter<MemberAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemMemberBinding): RecyclerView.ViewHolder(binding.root)
    private var dataSet = mutableListOf<String>()

    init {
        Firebase.firestore.collection("users/$uid/reviews").get()
            .addOnSuccessListener { documents ->
                dataSet.clear()
                for (document in documents) {
                    dataSet.add(document.id)
                }
                getPosters()
            }
            .addOnFailureListener {

            }
    }

    private var movieService: MovieService = RetrofitClient.movieService
    private var profile = mutableListOf<String?>()
    val completedCalls = AtomicInteger(0)

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

                    if (completedCalls.incrementAndGet() == dataSet.size) {
                        notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<DetailData>, t: Throwable) {
                    if (completedCalls.incrementAndGet() == dataSet.size) {
                        notifyDataSetChanged()
                    }
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
                    .into(binding.poster)

            }
        }
    }

    override fun getItemCount() = profile.size
}