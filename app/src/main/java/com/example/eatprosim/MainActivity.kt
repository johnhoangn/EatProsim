package com.example.eatprosim

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.*


@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback : LocationCallback
    private lateinit var model : SharedModel

    var currentLocation : Location? = null


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize
        model = ViewModelProviders.of(this).get(SharedModel::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Store new locations as we get them
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                model.setLocation(locationResult!!.lastLocation) // set current location in view model
                locationResult ?: return
                for (location in locationResult.locations){
                    currentLocation = location
                }
            }
        }

        model.downloadRestaurants()
        Log.wtf("WTF", "CREATED")
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
        Log.wtf("WTF", "RESUMED")
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
