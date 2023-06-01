package com.susuryo.mymoviestar.contract

import com.google.firebase.firestore.QuerySnapshot

interface MemberAdapterContract {
    interface View {
        fun showReviews(documents: QuerySnapshot)
        fun showPosters(path: String?)
        fun showFailure()
    }

    interface Presenter {
        fun getReviews(uid: String?)
        fun getPosters(id: String?)
    }
}