package com.susuryo.mymoviestar.contract

import com.google.firebase.firestore.DocumentSnapshot
import com.susuryo.mymoviestar.model.UserData

interface MemberContract {
    interface View {
        fun showUserInfo(user: UserData?)
        fun showFollowers(size: Int)
        fun showReviews(size: Int, rating: String)
        fun setButton(isUnfollow: Boolean, myUid: String?, uid: String?)
        fun onFailure()
    }

    interface Presenter {
        fun getUserInfo(uid: String?)
        fun getFollowers(documentSnapshot: DocumentSnapshot)
        fun getReviews(documentSnapshot: DocumentSnapshot)
        fun checkFollow(myUid: String?, uid: String?)
        fun setFollow(myUid: String?, uid: String?)
        fun setUnfollow(myUid: String?, uid: String?)
    }
}