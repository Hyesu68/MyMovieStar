package com.susuryo.mymoviestar.data

data class ReviewData(
    val movieId: Int,
    val userId: String,
    val rating: Double,
    val review: String,
    var timestamp: Any? = null
)
