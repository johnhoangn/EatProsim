package com.example.eatprosim

import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SharedModel : ViewModel() {

    var database : DatabaseReference = FirebaseDatabase.getInstance().reference
    var restaurants : MutableLiveData<ArrayList<Restaurant>> = MutableLiveData()

    private val debugging = true

    init {
        database = FirebaseDatabase.getInstance().reference
        database.child("Test").setValue("Hello, world!")
        if ( debugging ) {
            restaurants.value = ArrayList(
                listOf(
                    Restaurant("Restaurant A", 5, null, "123-456-7890", null),
                    Restaurant("Restaurant B", 2, null, "223-456-7890", null),
                    Restaurant("Restaurant C", 3, null, "323-456-7890", null)
                )
            )
        }
        else {
            restaurants.value = ArrayList()
        }
    }

    fun selectRestaurant(v : View, pos : Int) {
        val restaurant = restaurants.value?.get(pos)
            ?: return // Toast.makeText(Context, "",Toast.LENGTH_LONG).show()
        val bundle = bundleOf("name" to restaurant.name, "rating" to restaurant.rating,
                                "phone" to restaurant.phone)

        v.findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
    }

}