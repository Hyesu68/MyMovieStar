package com.susuryo.mymoviestar

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.data.DetailData
import com.susuryo.mymoviestar.data.UserData
import com.susuryo.mymoviestar.databinding.FragmentMyBinding
import com.susuryo.mymoviestar.databinding.ItemMemberBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.concurrent.atomic.AtomicInteger

class MemberActivity : AppCompatActivity() {
    private lateinit var binding: FragmentMyBinding
    private lateinit var memberAdapter: MemberAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentMyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myUid = Firebase.auth.currentUser?.uid
        val uid = intent.getStringExtra("id")
        memberAdapter = MemberAdapter(uid, applicationContext)
        binding.gridView.adapter = memberAdapter

        if (myUid == uid) { binding.follow.visibility = View.GONE }
        binding.follow.setOnClickListener { setFollow(myUid, uid) }

        getUserInfo(uid)
        checkFollow(myUid, uid)
    }

    private fun getUserInfo(uid: String?) {
        Firebase.firestore.collection("users").document(uid!!).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(UserData::class.java)
                    Glide.with(applicationContext)
                        .load(user?.profile)
                        .circleCrop()
                        .into(binding.profile)

                    binding.nickname.text = user?.nickname

                    getReviews(documentSnapshot)
                    getFollowers(documentSnapshot)
                }
            }
            .addOnFailureListener {  }
    }

    private fun getFollowers(documentSnapshot: DocumentSnapshot) {
        documentSnapshot.reference.collection("follower").get()
            .addOnSuccessListener {
                val size = it.size()
                binding.follower.text = size.toString()
            }
            .addOnFailureListener {  }
    }

    private fun getReviews(documentSnapshot: DocumentSnapshot) {
        documentSnapshot.reference.collection("reviews").get()
            .addOnSuccessListener {
                val size = it.size()
                binding.review.text = size.toString()

                var rating = 0.0
                val documents = it.documents
                for (document in documents) {
                    val fieldMappings = document.data
                    if (fieldMappings != null) {
                        for (fieldMapping in fieldMappings) {
                            if (fieldMapping.key.equals("rating")) {
                                rating += fieldMapping.value as? Double ?: 0.0
                            }
                        }
                        rating /= fieldMappings.size
                    }
                }

                val decimalFormat = DecimalFormat("#.0")
                val formattedNumber = decimalFormat.format(rating)
                binding.star.text = formattedNumber.toString()
            }
    }

    private fun setButton(isUnfollow: Boolean, myUid: String?, uid: String?) {
        if (isUnfollow) {
            binding.follow.text = "Unfollow"
            binding.follow.setTextColor(Color.BLACK)
            binding.follow.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.gradient2))
            binding.follow.setOnClickListener { setUnfollow(myUid, uid) }
        } else {
            binding.follow.text = "Follow"
            binding.follow.setTextColor(Color.WHITE)
            binding.follow.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.gradient1))
            binding.follow.setOnClickListener { setFollow(myUid, uid) }
        }
    }

    private fun checkFollow(myUid: String?, uid: String?) {
        Firebase.firestore.collection("users/$myUid/following").get()
            .addOnSuccessListener {
                val documents = it.documents
                for (document in documents) {
                    if (document.id == uid) {
                        setButton(true, myUid, uid)
                    }
                }
            }
            .addOnFailureListener {  }
    }

    private fun setFollow(myUid: String?, uid: String?) {
        val following = hashMapOf(uid to uid)
        Firebase.firestore.collection("users/$myUid/following").document(uid!!).set(following)
            .addOnSuccessListener {
                setButton(true, myUid, uid)

                val follower = hashMapOf(myUid to myUid)
                Firebase.firestore.collection("users/$uid/follower").document(myUid!!).set(follower)
            }
            .addOnFailureListener {  }
    }

    private fun setUnfollow(myUid: String?, uid: String?) {
        Firebase.firestore.collection("users/$myUid/following").document(uid!!).delete()
            .addOnSuccessListener {
                setButton(false, myUid, uid)

                Firebase.firestore.collection("users/$uid/follower").document(myUid!!).delete()
            }
            .addOnFailureListener {  }
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