package com.susuryo.mymoviestar.data

import com.google.firebase.Timestamp

data class ReviewData(
    val movieId: Int,
    val userId: String,
    val rating: Double,
    val review: String,
    var timestamp: Timestamp? = null
)
