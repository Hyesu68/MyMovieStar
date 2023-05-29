package com.susuryo.mymoviestar.contract

import com.susuryo.mymoviestar.model.DetailData
import com.susuryo.mymoviestar.model.ReviewData

interface DetailContract {
    interface View {
        fun showMovieDetail(detailData: DetailData)
        fun showMyReview(reviewList: MutableList<ReviewData>, myReview: ReviewData?)
        fun showFailure()
    }

    interface Presenter {
        fun getMovieDetail(movieId: Int)
        fun getMyReview(movieId: Int)
    }
}