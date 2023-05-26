package com.susuryo.mymoviestar


object GenreSingleton {
    private var dataset: MutableMap<Int, String> = mutableMapOf()

    fun setDataset(dataset: MutableMap<Int, String>) {
        this.dataset = dataset
    }

    fun addToDataset(id: Int, name: String) {
        dataset[id] = name
    }

    fun getDataset(): MutableMap<Int, String> {
        return dataset
    }

}