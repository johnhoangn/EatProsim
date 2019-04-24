package com.example.eatprosim

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.database.*

class MainActivity : AppCompatActivity(), LocationListener {

    private val provider = LocationManager.NETWORK_PROVIDER

    private lateinit var database : DatabaseReference
    private lateinit var locationManager : LocationManager
    private lateinit var model : SharedModel

    // Location listener code
    override fun onLocationChanged(location: Location?) {}
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String?) {}
    override fun onProviderDisabled(provider: String?) {}
    // End listener code

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize
        model = ViewModelProviders.of(this).get(SharedModel::class.java)
        database = FirebaseDatabase.getInstance().reference
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(provider, 1000, 0f, this)

        downloadRestaurants()
    }

    fun downloadRestaurants() {
        val newList = ArrayList<Restaurant>()

        database.child("restaurants").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(snap: DataSnapshot) {
                val restaurant = snap.getValue(Restaurant::class.java)
                restaurant?.also {
                    Log.wtf("GOT", it.name)
                    newList.add(it)
                }
            }
        })

        model.restaurants.postValue(newList)
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(this)
    }
}
