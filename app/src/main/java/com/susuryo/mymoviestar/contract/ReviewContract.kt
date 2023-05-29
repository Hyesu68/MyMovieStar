package com.susuryo.mymoviestar.contract

import com.susuryo.mymoviestar.model.ReviewData

interface ReviewContract {
    interface View {
        fun showReviews(reviewList: MutableList<ReviewData>)
        fun showFailure()
    }

    interface Presenter {
        fun getReviews()
    }
}