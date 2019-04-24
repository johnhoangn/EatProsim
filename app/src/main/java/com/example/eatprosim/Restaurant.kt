package com.example.eatprosim

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Restaurant(
    var name : String? = null,
    var phone : String? = null,
    var placeid : String? = null,
    var rating : Double = 0.0,
    var site : String? = null,
    var summary : String? = null
)
