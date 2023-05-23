package com.susuryo.mymoviestar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
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

        binding.toolBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    finish()
                    startActivity(Intent(this, SplashActivity::class.java))
                    true
                }
                else -> false
            }
        }

        val my = intent.getBooleanExtra("my", false)
        if (my) {
            binding.navigation.selectedItemId = R.id.item_my
        }
    }
}