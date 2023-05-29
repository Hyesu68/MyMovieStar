package com.susuryo.mymoviestar.contract

import android.net.Uri

interface SignupContract {
    interface View {
        fun goToMain()
        fun showFailure()
    }

    interface Presenter {
        fun doSignUp(email: String, password: String, nickname: String, profileUri: Uri?)
    }
}