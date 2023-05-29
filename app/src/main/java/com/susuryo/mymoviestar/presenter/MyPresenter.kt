package com.susuryo.mymoviestar.presenter

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.contract.MyContract
import com.susuryo.mymoviestar.model.UserData

class MyPresenter(val view: MyContract.View): MyContract.Presenter {

    override fun getUserData(uid: String?) {
        Firebase.firestore.collection("users").document(uid!!).get()
            .addOnSuccessListener {documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(UserData::class.java)
                    view.showProfile(user)
                    getFollowers(documentSnapshot)
                    getReviews(documentSnapshot)
                }
            }
            .addOnFailureListener {
                view.showFail()
            }
    }

    override fun getFollowers(documentSnapshot: DocumentSnapshot) {
        documentSnapshot.reference.collection("follower").get()
            .addOnSuccessListener {
                view.showFollowers(it)
            }
            .addOnFailureListener {
                view.showFail()
            }
    }

    override fun getReviews(documentSnapshot: DocumentSnapshot) {
        documentSnapshot.reference.collection("reviews").get()
            .addOnSuccessListener {
                view.showReviews(it)
            }
            .addOnFailureListener {
                view.showFail()
            }
    }
}