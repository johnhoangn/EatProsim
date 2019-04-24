package com.example.eatprosim

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedModel : ViewModel() {

    var restaurants : MutableLiveData<ArrayList<Restaurant>> = MutableLiveData<ArrayList<Restaurant>>().apply {
        value = ArrayList()
    }

}