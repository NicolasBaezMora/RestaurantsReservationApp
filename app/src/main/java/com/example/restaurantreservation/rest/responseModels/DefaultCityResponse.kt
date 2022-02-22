package com.example.restaurantreservation.rest.responseModels

import com.google.gson.annotations.SerializedName

data class DefaultCityResponse<T> (
    @SerializedName(value = "ok") val ok: Boolean,
    @SerializedName(value = "body") val body: T
)