package com.susuryo.mymoviestar.view.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.bumptech.glide.Glide
import com.susuryo.mymoviestar.contract.SignupContract
import com.susuryo.mymoviestar.databinding.ActivitySignupBinding
import com.susuryo.mymoviestar.presenter.SignupPresenter

class SignupActivity : AppCompatActivity(), SignupContract.View {
    private lateinit var binding: ActivitySignupBinding
    private val presenter: SignupContract.Presenter = SignupPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolBar.setNavigationOnClickListener { finish() }
        binding.profileButton.setOnClickListener { selectImage2() }
        binding.button.setOnClickListener { signUp() }
    }

    private fun signUp() {
        val email = binding.emailInput.editText?.text.toString()
        val password = binding.passwordInput.editText?.text.toString()
        val nickname = binding.nicknameInput.editText?.text.toString()
        presenter.doSignUp(email, password, nickname, profileUri)
    }

    override fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun showFailure() {
        Toast.makeText(applicationContext, "There was an issue encountered", Toast.LENGTH_SHORT).show()
    }

    val REQUEST_IMAGE_OPEN = 1
    fun selectImage2() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
        startActivityForResult(intent, REQUEST_IMAGE_OPEN)
    }

    var profileUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_OPEN && resultCode == Activity.RESULT_OK) {
            profileUri = data?.data
            // Do work with full size photo saved at fullPhotoUri
            Glide.with(this)
                .load(profileUri)
                .circleCrop()
                .into(binding.profileImage)

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val thumbnail: Bitmap? = data?.getParcelableExtra("data")

        }
    }

    val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var locationForPhotos: Uri
    fun capturePhoto(targetFilename: String) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, Uri.withAppendedPath(locationForPhotos, targetFilename))
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

}