package com.susuryo.mymoviestar.view.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.R
import com.susuryo.mymoviestar.contract.MemberContract
import com.susuryo.mymoviestar.view.adapter.MemberAdapter
import com.susuryo.mymoviestar.model.UserData
import com.susuryo.mymoviestar.databinding.FragmentMyBinding
import com.susuryo.mymoviestar.presenter.MemberPresenter

class MemberActivity : AppCompatActivity(), MemberContract.View {
    private lateinit var binding: FragmentMyBinding
    private lateinit var memberAdapter: MemberAdapter
    private val presenter: MemberContract.Presenter = MemberPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentMyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myUid = Firebase.auth.currentUser?.uid
        val uid = intent.getStringExtra("id")
        memberAdapter = MemberAdapter(uid, applicationContext, binding.progressBar)
        binding.gridView.adapter = memberAdapter

        if (myUid == uid) { binding.follow.visibility = View.GONE }
        binding.follow.setOnClickListener { setFollow(myUid, uid) }
        binding.toolBar.setNavigationOnClickListener { finish() }

        getUserInfo(uid)
        checkFollow(myUid, uid)
    }

    private fun getUserInfo(uid: String?) {
        presenter.getUserInfo(uid)
    }

    override fun showUserInfo(user: UserData?) {
        Glide.with(applicationContext)
            .load(user?.profile)
            .circleCrop()
            .into(binding.profile)

        binding.nickname.text = user?.nickname
        binding.toolBar.title = user?.nickname
    }

    override fun showFollowers(size: Int) {
        binding.follower.text = size.toString()
    }

    override fun showReviews(size: Int, rating: String) {
        binding.review.text = size.toString()
        if (size > 0) {
            binding.star.text = rating
        } else {
            binding.star.text = "0"
        }
    }

    override fun setButton(isUnfollow: Boolean, myUid: String?, uid: String?) {
        if (isUnfollow) {
            binding.follow.text = "Unfollow"
            binding.follow.setTextColor(Color.BLACK)
            binding.follow.setBackgroundColor(ContextCompat.getColor(applicationContext,
                R.color.gradient2
            ))
            binding.follow.setOnClickListener { setUnfollow(myUid, uid) }
        } else {
            binding.follow.text = "Follow"
            binding.follow.setTextColor(Color.WHITE)
            binding.follow.setBackgroundColor(ContextCompat.getColor(applicationContext,
                R.color.gradient1
            ))
            binding.follow.setOnClickListener { setFollow(myUid, uid) }
        }
    }

    private fun checkFollow(myUid: String?, uid: String?) {
        presenter.checkFollow(myUid, uid)
    }

    private fun setFollow(myUid: String?, uid: String?) {
        presenter.setFollow(myUid, uid)
    }

    private fun setUnfollow(myUid: String?, uid: String?) {
        presenter.setUnfollow(myUid, uid)
    }

}

