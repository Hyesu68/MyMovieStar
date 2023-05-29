package com.susuryo.mymoviestar.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.susuryo.mymoviestar.contract.SettingContract
import com.susuryo.mymoviestar.databinding.ActivitySettingBinding
import com.susuryo.mymoviestar.presenter.SettingPresenter

class SettingActivity : AppCompatActivity(), SettingContract.View {
    private lateinit var binding: ActivitySettingBinding
    private val presenter: SettingContract.Presenter = SettingPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logout.setOnClickListener {
            presenter.doSignOut()
            goToLogIn()
        }
    }

    private fun goToLogIn() {
        finish()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}