package com.example.eatprosim

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Restaurant(var name : String? = null,
                 var rating : Int? = null,
                 var site : String? = null,
                 var phone : String? = null,
                 var summary : String? = null)
