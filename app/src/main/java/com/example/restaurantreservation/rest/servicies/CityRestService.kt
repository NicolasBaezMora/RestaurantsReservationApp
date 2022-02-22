package com.example.restaurantreservation.rest.servicies

import com.example.restaurantreservation.rest.requestModels.CityRequest
import com.example.restaurantreservation.rest.responseModels.CityResponse
import com.example.restaurantreservation.rest.responseModels.DefaultCityResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CityRestService {

    @GET(value = "api/cities")
    fun getCities(): Call<DefaultCityResponse<List<CityResponse>>>

    @POST(value = "api/cities")
    fun addCity(@Body dataCity: CityRequest): Call<DefaultCityResponse<CityResponse>>


}