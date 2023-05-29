package com.susuryo.mymoviestar.model

import com.google.gson.annotations.SerializedName

data class GenreData(
    @SerializedName("genres" ) var genres : ArrayList<Genres> = arrayListOf()
)
