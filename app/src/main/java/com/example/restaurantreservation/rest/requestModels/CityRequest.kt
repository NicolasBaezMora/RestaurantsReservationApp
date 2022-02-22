package com.example.restaurantreservation.rest.requestModels

import com.google.gson.annotations.SerializedName

data class CityRequest (
    @SerializedName(value = "name") val name: String
)