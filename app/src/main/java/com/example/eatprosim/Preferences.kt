package com.example.eatprosim

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Preferences(var sort : String = "default", var filter : String = "default")