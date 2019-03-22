package com.example.eatprosim

/**
 * @param name
 * @param rating
 * @param menu
 * @param phone
 * @param summary
 */
class Restaurant(val name : String,
                 val rating : Int,
                 val menu : ArrayList<FoodItem>?,
                 val phone : String,
                 val summary : String?)
