package com.susuryo.mymoviestar.contract

import android.widget.ImageView
import com.susuryo.mymoviestar.databinding.ItemDetailBinding
import com.susuryo.mymoviestar.model.UserData

interface DetailAdapterContract {
    interface View {
        fun showUserDetail(user: UserData?, binding: ItemDetailBinding)
        fun showMovieDetail(posterPath: String?, imageView: ImageView)
        fun showFailure()
    }

    interface Presenter {
        fun getMovieDetail(movieId: Int, imageView: ImageView)
        fun getUserData(uid: String, binding: ItemDetailBinding)
    }
}