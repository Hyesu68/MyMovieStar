package com.susuryo.mymoviestar.data

import com.google.gson.annotations.SerializedName

data class GenreData(
    @SerializedName("genres" ) var genres : ArrayList<Genres> = arrayListOf()
)
