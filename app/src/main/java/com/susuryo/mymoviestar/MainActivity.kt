package com.susuryo.mymoviestar

import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.data.UserData
import com.susuryo.mymoviestar.databinding.ActivityMainBinding
import com.susuryo.mymoviestar.fragment.HomeFragment
import com.susuryo.mymoviestar.fragment.MyFragment
import com.susuryo.mymoviestar.fragment.ReviewFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().replace(R.id.layout, HomeFragment()).commit()
        binding.navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.layout, HomeFragment()).commit()
                    true
                }
                R.id.item_review -> {
                    supportFragmentManager.beginTransaction().replace(R.id.layout, ReviewFragment()).commit()
                    true
                }
                R.id.item_my -> {
                    supportFragmentManager.beginTransaction().replace(R.id.layout, MyFragment()).commit()
                    true
                }
                else -> false
            }
        }

        /*binding.toolBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_profile -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                    true
                }
                else -> false
            }
        }*/

        goToMyList()
        setMyProfile()
    }

    private fun goToMyList() {
        val my = intent.getBooleanExtra("my", false)
        if (my) {
            binding.navigation.selectedItemId = R.id.item_my
        }
    }

    private fun setMyProfile() {
        val uid = Firebase.auth.currentUser?.uid
        Firebase.firestore.collection("users").document(uid!!).get()
            .addOnSuccessListener {
                val user = it.toObject<UserData>()
                val menuItem = binding.toolBar.menu.findItem(R.id.item_profile)
                val inflater = LayoutInflater.from(this)
                menuItem?.actionView = inflater.inflate(R.layout.top_profile, null)

                val menuItemView = menuItem.actionView
                val imageView = menuItemView?.findViewById<ImageView>(R.id.menu_item_image)

                // Update the layout parameters of the ImageView
                val layoutParams = imageView?.layoutParams as? ViewGroup.MarginLayoutParams
                layoutParams?.width = 40.dpToPx()
                layoutParams?.height = 40.dpToPx()
                layoutParams?.setMargins(0, 0, 15.dpToPx(), 0)
                imageView?.layoutParams = layoutParams

                Glide.with(applicationContext)
                    .load(user?.profile) // Load the profile image URL
                    .circleCrop()
                    .into(imageView!!)

                imageView.setOnClickListener {
                    startActivity(Intent(this, SettingActivity::class.java))
                }
            }
            .addOnFailureListener {  }
    }

    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

}