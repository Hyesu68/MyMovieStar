package com.susuryo.mymoviestar.contract

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.susuryo.mymoviestar.model.UserData


interface MyContract {
    interface View {
        fun showProfile(user: UserData?)
        fun showFollowers(it: QuerySnapshot)
        fun showReviews(it: QuerySnapshot)
        fun showFail()
    }

    interface Presenter {
        fun getUserData(uid: String?)
        fun getReviews(documentSnapshot: DocumentSnapshot)
        fun getFollowers(documentSnapshot: DocumentSnapshot)
    }
}