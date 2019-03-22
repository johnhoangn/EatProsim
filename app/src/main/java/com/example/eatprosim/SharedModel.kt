package com.example.eatprosim

import androidx.lifecycle.MutableLiveData

class SharedModel {

    private lateinit var restaurants : MutableLiveData<ArrayList<Restaurant>>

    init {
        restaurants.value = ArrayList()
    }



}