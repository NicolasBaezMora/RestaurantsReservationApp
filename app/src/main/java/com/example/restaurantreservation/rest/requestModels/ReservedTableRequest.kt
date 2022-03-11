package com.example.restaurantreservation.rest.requestModels

import com.google.gson.annotations.SerializedName

data class ReservedTableRequest(
    @SerializedName(value = "idRestaurant") var id: String?,
    @SerializedName(value = "name") var name: String?,
    @SerializedName(value = "date") var date: String?
)