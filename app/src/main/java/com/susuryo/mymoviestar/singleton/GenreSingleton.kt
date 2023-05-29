package com.susuryo.mymoviestar.singleton


object GenreSingleton {
    private var dataset: MutableMap<Int, String> = mutableMapOf()

    fun setDataset(dataset: MutableMap<Int, String>) {
        GenreSingleton.dataset = dataset
    }

    fun addToDataset(id: Int, name: String) {
        dataset[id] = name
    }

    fun getDataset(): MutableMap<Int, String> {
        return dataset
    }

}