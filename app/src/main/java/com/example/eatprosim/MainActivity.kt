package com.example.eatprosim

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.*
import com.google.firebase.database.*

@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback : LocationCallback
    private lateinit var database : DatabaseReference
    private lateinit var model : SharedModel
    private var currentLocation : Location? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize
        model = ViewModelProviders.of(this).get(SharedModel::class.java)
        database = FirebaseDatabase.getInstance().reference
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Store new locations as we get them
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    currentLocation = location
                }
            }
        }

        downloadRestaurants()
    }

    private fun downloadRestaurants() {
        database.child("restaurants").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(snap: DataSnapshot) {
                val menu : MutableList<Restaurant> = mutableListOf()
                snap.children.mapNotNullTo(menu) {
                    it.getValue<Restaurant>(Restaurant::class.java)
                }
                for (restaurant : Restaurant in menu) {
                    model.restaurants.value?.also { list ->
                        list.add(restaurant)
                    }
                    model.restaurants.postValue(model.restaurants.value)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 1000
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback, null)
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
