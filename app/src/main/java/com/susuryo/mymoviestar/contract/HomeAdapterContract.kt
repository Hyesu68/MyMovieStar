package com.susuryo.mymoviestar.contract

import com.susuryo.mymoviestar.model.Results

interface HomeAdapterContract {
    interface View {
        fun showNowPlaying(dataSet: MutableList<Results>)
        fun showFailure()
    }

    interface Presenter {
        fun getNowPlaying()
        fun getQuery(query: String?)
    }
}