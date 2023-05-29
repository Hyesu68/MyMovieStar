package com.susuryo.mymoviestar.contract

import com.susuryo.mymoviestar.model.UserData

interface MainContract {
    interface View {
        fun setHomeFragment()
        fun setMyProfile(user: UserData?)
        fun showFailure()
    }

    interface Presenter {
        fun getGenre()
        fun getMyProfile()
    }
}