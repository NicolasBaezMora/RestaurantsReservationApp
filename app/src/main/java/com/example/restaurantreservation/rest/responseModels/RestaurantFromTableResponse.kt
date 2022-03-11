package com.example.restaurantreservation.rest.responseModels

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RestaurantFromTableResponse(
    @SerializedName(value = "name") val name: String,
    @SerializedName(value = "description") val description: String,
    @SerializedName(value = "address") val address: String,
    @SerializedName(value = "image") val image: String?,
    @SerializedName(value = "city") val city: String,
): ParentElementResponse(), Serializable