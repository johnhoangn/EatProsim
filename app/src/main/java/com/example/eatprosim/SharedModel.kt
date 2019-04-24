package com.example.eatprosim

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedModel : ViewModel() {

    val sorts = arrayOf("name", "rating", "distance")
    val filters = arrayOf("on campus", "off campus")
    var sort = "name"
    var filter = "campus"

    var restaurants : MutableLiveData<ArrayList<Restaurant>> = MutableLiveData<ArrayList<Restaurant>>().apply {
        value = ArrayList()
    }

    var supportedRestaurants = arrayOf(
        "bollos",
        "carolLeeDonuts",
        "chipotle",
        "himalaynCurry",
        "kungFuTea",
        "nextDoorBakeShop",
        "sugarMagnolia",
        "sushiFactory",
        "theCellar"
    )

    fun setSort(sort : Int) {
        this.sort = sorts[sort]
    }
    fun setFilter(filter : Int) {
        this.filter = filters[filter]
    }

    /*
        "Sushi Factory",
        "JPetal",
        "Kung Fu Tea",
        "Cafe Mekong",
        "Spice City",
        "Next Door Bake Shop",
        "Himalayan Curry",
        "Happy Wok",
        "Souvlaki",
        "The Cellar",
        "Cafe Bangkok"
     */
}