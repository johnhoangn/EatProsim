package com.example.eatprosim

import android.app.Activity
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*

class SharedModel : ViewModel() {

    var database : DatabaseReference = FirebaseDatabase.getInstance().reference

    val sorts = arrayOf("Name", "Rating", "Distance")
    val filters = arrayOf("None", "On Campus", "Off Campus")
    var sort = "Name"
    var filter = "None"

    var restaurants : MutableLiveData<ArrayList<Restaurant>> = MutableLiveData<ArrayList<Restaurant>>().apply {
        value = ArrayList()
    }
    var currentLocation : Location ?= null

    fun setLocation(newLocation : Location) {
        currentLocation = newLocation
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

    /**
     * Initial restaurant download.
     */
    fun downloadRestaurants() {
        database.child("restaurants").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(snap: DataSnapshot) {
                val menu : MutableList<Restaurant> = mutableListOf()
                snap.children.mapNotNullTo(menu) {
                    it.getValue<Restaurant>(Restaurant::class.java)
                }
                for (restaurant : Restaurant in menu) {
                        restaurants.value?.also { list ->
                            list.add(restaurant)
                        }
                    restaurants.postValue(restaurants.value)
                }
            }
        })
    }

    /**
     * Function for redownloading removed restaurants.
     */
    fun reDownload() {
        database.child("restaurants").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(snap: DataSnapshot) {
                val menu : MutableList<Restaurant> = mutableListOf()
                snap.children.mapNotNullTo(menu) {
                    it.getValue<Restaurant>(Restaurant::class.java)
                }
                for (restaurant : Restaurant in menu) {
                    if (!restaurants.value!!.contains(restaurant)) { // Alice added so we don't have duplicates
                        restaurants.value?.also { list ->
                            list.add(restaurant)
                        }
                      //  restaurants.postValue(restaurants.value)
                    }
                }
            }
        })
    }

    /**
     * Filters search view for restaurants with names that contain test.
     */
    fun filterByContains(text: String) : List<Restaurant> {
            return restaurants.value?.filter {
            // only restaurants that contain text in their Name
            val restName = it.name!!.replace("\'", "")
            val inputText = text.replace("\'", "")
            restName.contains(inputText, true)
        }!!
    }

    /**
     * Sorts based on value selected from spinner.
     */
    fun sort() {
        when (sort) {
            "Name" ->
                restaurants.value?.sortBy {
                    it.name
                }
            "Rating" ->
                restaurants.value?.sortByDescending {
                    it.rating
                }
            "Distance" ->
                restaurants.value?.sortBy {
                    findDistance(it)
                }
        }
    }

    /**
     * Calculates distance from current location to restaurant
     */
    private fun findDistance(restaurant : Restaurant) : Float {
        val result : FloatArray = floatArrayOf(0F)
        Log.wtf("currLocation: ", currentLocation.toString())
        Location.distanceBetween(currentLocation!!.latitude, currentLocation!!.longitude,
            restaurant.latitude!!, restaurant.longitude!!, result)
        Log.wtf("DISTANCE: ", result[0].toString())
        return result[0] // distance is stored in first index of float array
    }

    /**
     * Filters based on value selected from spinner.
     */
    fun filter() : List<Restaurant> {
        when (filter) {
            "On Campus" ->
                return restaurants.value?.filter {
                it.campus
            }!!
            "Off Campus" ->
                return restaurants.value?.filter {
                !(it.campus)
            }!!
        }
        return restaurants.value as List<Restaurant>
    }
}