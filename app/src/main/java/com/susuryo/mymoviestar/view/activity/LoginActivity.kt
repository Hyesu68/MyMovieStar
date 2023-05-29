package com.susuryo.mymoviestar.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.R
import com.susuryo.mymoviestar.contract.LoginContract
import com.susuryo.mymoviestar.databinding.ActivityLoginBinding
import com.susuryo.mymoviestar.presenter.LoginPresenter

class LoginActivity : AppCompatActivity(), LoginContract.View {
    private lateinit var binding: ActivityLoginBinding
    private val presenter: LoginContract.Presenter = LoginPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener { logIn() }
        binding.signUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun logIn() {
        val email = binding.emailInput.editText?.text.toString()
        val password = binding.passwordInput.editText?.text.toString()

        if (email.isEmpty()) {
            binding.emailInput.error = resources.getString(R.string.email_not_empty)
        }
        if (password.isEmpty()) {
            binding.passwordInput.error = resources.getString(R.string.password_not_empty)
        }

        if (email.isNotEmpty() && password.isNotEmpty()) {
            presenter.doLogIn(email, password)
            binding.progressBar.visibility = View.VISIBLE
            setEnabled(false)
        }
    }

    private fun setEnabled(isEnabled: Boolean) {
        binding.button.isEnabled = isEnabled
        binding.emailInput.isEnabled = isEnabled
        binding.passwordInput.isEnabled = isEnabled
    }

    override fun onSuccess() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onFailure() {
        binding.progressBar.visibility = View.GONE
        setEnabled(true)
        Toast.makeText(applicationContext, "There was an issue encountered", Toast.LENGTH_SHORT).show()
    }
}