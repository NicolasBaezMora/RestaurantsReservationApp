package com.example.restaurantreservation.rest.requestModels

import android.graphics.Bitmap
import android.net.Uri
import com.google.gson.annotations.SerializedName
import java.io.File
import java.io.Serializable

data class RestaurantRequest (
    @SerializedName(value = "name") val name: String,
    @SerializedName(value = "description") val description: String,
    @SerializedName(value = "address") val address: String,
    @SerializedName(value = "city") val city: String,
): Serializable