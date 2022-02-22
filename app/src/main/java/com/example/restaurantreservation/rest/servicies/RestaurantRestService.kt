package com.example.restaurantreservation.rest.servicies

import com.example.restaurantreservation.rest.requestModels.RestaurantRequest
import com.example.restaurantreservation.rest.responseModels.DefaultRestaurantResponse
import com.example.restaurantreservation.rest.responseModels.RestaurantResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RestaurantRestService {

    @GET(value = "api/restaurants")
    fun getRestaurants(): Call<DefaultRestaurantResponse<List<RestaurantResponse>>>

    @POST(value = "api/restaurants")
    @Multipart
    fun addRestaurant(@PartMap dataRestaurant: Map<String, @JvmSuppressWildcards RequestBody>, @Part file: MultipartBody.Part): Call<DefaultRestaurantResponse<RestaurantResponse>>

    @PUT(value = "api/restaurants/{id}")
    fun updateRestaurant(@Body dataRestaurant: RestaurantRequest, @Path(value = "id") id: String): Call<DefaultRestaurantResponse<RestaurantResponse>>

    @PUT(value = "api/restaurants/image/{id}")
    @Multipart
    fun updateRestaurantImage(@Part file: MultipartBody.Part, @Path(value = "id") id: String): Call<DefaultRestaurantResponse<RestaurantResponse>>

    @DELETE(value = "api/restaurants/{id}")
    fun deleteRestaurant(@Path(value = "id") id: String): Call<DefaultRestaurantResponse<RestaurantResponse>>

}