package com.susuryo.mymoviestar.contract

interface LoginContract {
    interface View {
        fun onSuccess()
        fun onFailure()
    }

    interface Presenter {
        fun doLogIn(email: String, password: String)
    }
}