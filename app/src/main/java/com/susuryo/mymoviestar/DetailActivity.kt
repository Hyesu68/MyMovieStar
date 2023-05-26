package com.susuryo.mymoviestar

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.data.DetailData
import com.susuryo.mymoviestar.data.ReviewData
import com.susuryo.mymoviestar.data.UserData
import com.susuryo.mymoviestar.databinding.ActivityDetailBinding
import com.susuryo.mymoviestar.databinding.ItemDetailBinding
import com.susuryo.mymoviestar.fragment.WriteReviewFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private var movieService: MovieService = RetrofitClient.movieService
    private var title: String? = null
    private lateinit var detailAdapter: DetailAdapter

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

    private fun getMovieDetail(movieId: Int) {
        val call = movieService.getMovieDetails(movieId, BuildConfig.MOVIE_API_KEY)
        call.enqueue(object : Callback<DetailData> {
            override fun onResponse(call: Call<DetailData>, response: Response<DetailData>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    title = body?.title
                    binding.toolBar.title = body?.title
                    binding.title.text = body?.title
                    binding.overview.text = body?.overview
                    binding.releaseDate.text = body?.releaseDate
                    binding.review.text = body?.voteAverage.toString()
                    binding.popularity.text = body?.popularity.toString()

                    Glide.with(applicationContext)
                        .load("https://image.tmdb.org/t/p/original" + body?.posterPath)
                        .into(binding.poster)

                    val vote = body?.voteAverage?.div(2)?.toFloat()
                    if (vote != null) {
                        binding.star.text = vote.toString()
                    }

                    val stringBuffer = StringBuffer()
                    for (genre in body?.genres!!) {
                        stringBuffer.append(genre.name)
                        stringBuffer.append(" ")
                    }
                    binding.genre.text = stringBuffer.toString()
                }
            }

            override fun onFailure(call: Call<DetailData>, t: Throwable) {

            }
        })
    }

    private var myReview: ReviewData? = null
    private var isNew = true
    private fun getMyReview(movieId: Int) {
        val uid = Firebase.auth.currentUser!!.uid
        Firebase.firestore.collection("reviews").document(movieId.toString()).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val fieldMappings = documentSnapshot.data

                    if (fieldMappings != null) {
                        isNew = false

                        val reviewList = mutableListOf<ReviewData>()
                        for (fieldMapping in fieldMappings.values) {
                            if (fieldMapping is Map<*, *>) {
                                val reviewData = ReviewData(
                                    movieId = fieldMapping["movieId"] as? Int ?: 0,
                                    userId = fieldMapping["userId"] as? String ?: "",
                                    rating = fieldMapping["rating"] as? Double ?: 0.0,
                                    review = fieldMapping["review"] as? String ?: "",
                                    timestamp = fieldMapping["timestamp"] as? Timestamp
                                )

                                if (reviewData.userId == uid) {
                                    myReview = reviewData
                                    binding.myReviewText.text = myReview?.review
                                    val vote = myReview?.rating?.toFloat()
                                    if (vote != null) {
                                        binding.myRating.rating = vote
                                    }

                                    val timestamp = myReview?.timestamp as Timestamp
                                    val date = timestamp.toDate()
                                    val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(date)
                                    binding.date.text = time
                                } else {
                                    reviewList.add(reviewData)
                                }
                            }
                        }

                        if (reviewList.isNotEmpty()) {
                            detailAdapter = DetailAdapter(reviewList, true, null)
                            binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
                            binding.recyclerView.adapter = detailAdapter
                        }

                        if (myReview != null) {
                            binding.myReview.visibility = View.VISIBLE
                        }
                    }
                }
            }
            .addOnFailureListener {

            }
    }
}

class DetailAdapter(val dataSet: MutableList<ReviewData>, val isDetail: Boolean, val posterMap: MutableMap<Int, String?>?): RecyclerView.Adapter<DetailAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemDetailBinding): RecyclerView.ViewHolder(binding.root)

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
                    /*if (posterMap != null) {
                        Glide.with(itemView.context)
                            .load("https://image.tmdb.org/t/p/original" + posterMap[this.movieId])
                            .centerCrop()
                            .into(binding.poster)
                    }*/

                    getMovieDetail(this.movieId, binding.poster)
                    binding.review.setOnClickListener { moveToDetail(itemView.context, this.movieId) }
                    binding.ratingLayout.setOnClickListener { moveToDetail(itemView.context, this.movieId) }
                }

                Firebase.firestore.collection("users").document(this.userId).get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val user = documentSnapshot.toObject(UserData::class.java)
                            Glide.with(itemView.context)
                                .load(user?.profile)
                                .circleCrop()
                                .into(binding.profile)

                            binding.nickname.text = user?.nickname
                        }
                    }
                    .addOnFailureListener {

                    }

                binding.poster.setOnClickListener { moveToDetail(itemView.context, this.movieId) }
                binding.profile.setOnClickListener { moveToMember(itemView.context, this.userId) }
                binding.nickname.setOnClickListener { moveToMember(itemView.context, this.userId) }
            }
        }
    }

    private var movieService: MovieService = RetrofitClient.movieService
    private fun getMovieDetail(movieId: Int, imageView: ImageView) {
        val call = movieService.getMovieDetails(movieId, BuildConfig.MOVIE_API_KEY)
        call.enqueue(object : Callback<DetailData> {
            override fun onResponse(call: Call<DetailData>, response: Response<DetailData>) {
                if (response.isSuccessful) {
                    val body = response.body()
//                    posterMap[movieId] = body?.posterPath
                    Glide.with(imageView.context)
                        .load("https://image.tmdb.org/t/p/original" + body?.posterPath)
                        .centerCrop()
                        .into(imageView)
                }
            }

            override fun onFailure(call: Call<DetailData>, t: Throwable) {

            }
        })
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

}