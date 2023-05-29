package com.susuryo.mymoviestar.model

data class UserData(
    val email: String = "",
    val nickname: String = "",
    val profile: String = ""
) {
    // Add a no-argument constructor
    constructor() : this("", "", "")
}
