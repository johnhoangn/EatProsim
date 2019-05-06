package com.example.eatprosim

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SharedModel : ViewModel() {

    var database : DatabaseReference = FirebaseDatabase.getInstance().reference

    val sorts = arrayOf("name", "rating", "distance")
    val filters = arrayOf("both", "on campus", "off campus")
    var sort = "name"
    var filter = "both"

    var restaurants : MutableLiveData<ArrayList<Restaurant>> = MutableLiveData<ArrayList<Restaurant>>().apply {
        value = ArrayList()
    }

    fun setSort(sort : Int) {
        this.sort = sorts[sort]
    }
    fun setFilter(filter : Int) {
        this.filter = filters[filter]
    }

    fun uploadComment(comment : Comment, restaurantID : String) {
        database.child("comments").child(restaurantID)
            .child(System.currentTimeMillis().toString()).setValue(comment)
    }
}