package com.susuryo.mymoviestar.presenter

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.contract.LoginContract

class LoginPresenter(val view: LoginContract.View): LoginContract.Presenter {
    override fun doLogIn(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    view.onSuccess()
                } else {
                    view.onFailure()
                }
            }
    }
}