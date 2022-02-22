package com.example.restaurantreservation.rest.responseModels

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CityResponse(
    @SerializedName(value = "_id") val id: String,
    @SerializedName(value = "name") val name: String,
): ParentElementResponse(), Serializable