package com.susuryo.mymoviestar.contract

interface WriteReviewContract {
    interface View {
        fun goToDetail()
        fun showFailure()
    }

    interface Presenter {
        fun setReview(movieId: Int, rating: Double, review: String)
        fun updateReview(movieId: Int, rating: Double, review: String)
        fun setReviewInUser(movieId: Int, rating: Double)
    }
}