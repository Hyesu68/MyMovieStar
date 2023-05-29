package com.susuryo.mymoviestar.presenter

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.susuryo.mymoviestar.contract.MemberContract
import com.susuryo.mymoviestar.model.UserData
import java.text.DecimalFormat

class MemberPresenter(val view: MemberContract.View): MemberContract.Presenter {
    override fun getUserInfo(uid: String?) {
        Firebase.firestore.collection("users").document(uid!!).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(UserData::class.java)
                    view.showUserInfo(user)

                    getReviews(documentSnapshot)
                    getFollowers(documentSnapshot)
                }
            }
            .addOnFailureListener {
                view.onFailure()
            }
    }

    override fun getFollowers(documentSnapshot: DocumentSnapshot) {
        documentSnapshot.reference.collection("follower").get()
            .addOnSuccessListener {
                val size = it.size()
                view.showFollowers(size)
            }
            .addOnFailureListener {
                view.onFailure()
            }
    }

    override fun getReviews(documentSnapshot: DocumentSnapshot) {
        documentSnapshot.reference.collection("reviews").get()
            .addOnSuccessListener {
                val size = it.size()
                var rating = 0.0
                val documents = it.documents
                for (document in documents) {
                    val fieldMappings = document.data
                    if (fieldMappings != null) {
                        for (fieldMapping in fieldMappings) {
                            if (fieldMapping.key.equals("rating")) {
                                rating += fieldMapping.value as? Double ?: 0.0
                            }
                        }
                        rating /= fieldMappings.size
                    }
                }

                val decimalFormat = DecimalFormat("#.0")
                val formattedNumber = decimalFormat.format(rating)
                view.showReviews(size, formattedNumber)
            }
            .addOnFailureListener {
                view.onFailure()
            }
    }

    override fun checkFollow(myUid: String?, uid: String?) {
        Firebase.firestore.collection("users/$myUid/following").get()
            .addOnSuccessListener {
                val documents = it.documents
                for (document in documents) {
                    if (document.id == uid) {
                        view.setButton(true, myUid, uid)
                    }
                }
            }
            .addOnFailureListener {
                view.onFailure()
            }
    }

    override fun setFollow(myUid: String?, uid: String?) {
        val following = hashMapOf(uid to uid)
        Firebase.firestore.collection("users/$myUid/following").document(uid!!).set(following)
            .addOnSuccessListener {
                view.setButton(true, myUid, uid)

                val follower = hashMapOf(myUid to myUid)
                Firebase.firestore.collection("users/$uid/follower").document(myUid!!).set(follower)
            }
            .addOnFailureListener {
                view.onFailure()
            }
    }

    override fun setUnfollow(myUid: String?, uid: String?) {
        Firebase.firestore.collection("users/$myUid/following").document(uid!!).delete()
            .addOnSuccessListener {
                view.setButton(false, myUid, uid)

                Firebase.firestore.collection("users/$uid/follower").document(myUid!!).delete()
            }
            .addOnFailureListener {
                view.onFailure()
            }
    }
}