package com.susuryo.mymoviestar

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.susuryo.mymoviestar.data.UserData
import com.susuryo.mymoviestar.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.profileButton.setOnClickListener { selectImage2() }
        binding.button.setOnClickListener { signUp() }
    }

    private fun signUp() {
        val email = binding.emailInput.editText?.text.toString()
        val password = binding.passwordInput.editText?.text.toString()
        val nickname = binding.nicknameInput.editText?.text.toString()
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val uid = Firebase.auth.currentUser!!.uid
                    Firebase.storage.getReference("profiles").child(uid).putFile(profileUri!!)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val result = it.result.storage.downloadUrl
                                result.addOnSuccessListener { uri ->
                                    val user = UserData(email, nickname, uri.toString())
                                    Firebase.firestore.collection("users").document(uid)
                                        .set(user)
                                        .addOnCompleteListener {
                                            startActivity(Intent(this, MainActivity::class.java))
                                        }
                                }
                            } else {
                                Toast.makeText(applicationContext, it.result.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(applicationContext, it.result.toString(), Toast.LENGTH_SHORT).show()
                }
            }
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