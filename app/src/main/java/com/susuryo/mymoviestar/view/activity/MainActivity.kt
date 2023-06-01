package com.susuryo.mymoviestar.view.activity

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.susuryo.mymoviestar.R
import com.susuryo.mymoviestar.contract.MainContract
import com.susuryo.mymoviestar.model.UserData
import com.susuryo.mymoviestar.databinding.ActivityMainBinding
import com.susuryo.mymoviestar.presenter.MainPresenter
import com.susuryo.mymoviestar.view.fragment.HomeFragment
import com.susuryo.mymoviestar.view.fragment.MyFragment
import com.susuryo.mymoviestar.view.fragment.ReviewFragment

class MainActivity : AppCompatActivity(), MainContract.View {
    private lateinit var binding: ActivityMainBinding
    private val presenter: MainContract.Presenter = MainPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setMyProfile()
        getGenre()

        binding.navigation.setOnItemSelectedListener { item -> setItemSelect(item) }
    }

    private fun setItemSelect(item: MenuItem) = when (item.itemId) {
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

    private fun getGenre() {
        presenter.getGenre()
    }

    override fun setHomeFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.layout, HomeFragment()).commit()
    }

    override fun setMyProfile(user: UserData?) {
        val menuItem = binding.toolBar.menu.findItem(R.id.item_profile)
        val inflater = LayoutInflater.from(this)
        menuItem?.actionView = inflater.inflate(R.layout.top_profile, null)

        val menuItemView = menuItem.actionView
        val imageView = menuItemView?.findViewById<ImageView>(R.id.menu_item_image)

        val layoutParams = imageView?.layoutParams as? ViewGroup.MarginLayoutParams
        layoutParams?.width = 40.dpToPx()
        layoutParams?.height = 40.dpToPx()
        layoutParams?.setMargins(0, 0, 15.dpToPx(), 0)
        imageView?.layoutParams = layoutParams

        Glide.with(applicationContext)
            .load(user?.profile)
            .circleCrop()
            .into(imageView!!)

        imageView.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
    }

    private fun setMyProfile() {
        presenter.getMyProfile()
    }

    override fun showFailure() {
        Toast.makeText(applicationContext, "There was an issue encountered", Toast.LENGTH_SHORT).show()
    }

    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

}