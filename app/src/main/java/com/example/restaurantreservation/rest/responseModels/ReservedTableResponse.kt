package com.example.restaurantreservation.rest.responseModels

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class ReservedTableResponse(
    @SerializedName(value = "id") val id: String,
    @SerializedName(value = "name") val tableName: String,
    @SerializedName(value = "date") val date: String,
    @SerializedName(value = "restaurant") val restaurant: RestaurantFromTableResponse?
): Serializable, ParentElementResponse()