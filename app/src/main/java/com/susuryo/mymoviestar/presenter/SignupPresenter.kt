package com.susuryo.mymoviestar.presenter

import android.net.Uri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.susuryo.mymoviestar.contract.SignupContract
import com.susuryo.mymoviestar.model.UserData

class SignupPresenter(val view: SignupContract.View): SignupContract.Presenter {
    override fun doSignUp(email: String, password: String, nickname: String, profileUri: Uri?) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
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
                                            view.goToMain()
                                        }
                                }
                            } else {
                                view.showFailure()
                            }
                        }
                } else {
                    view.showFailure()
                }
            }
    }
}