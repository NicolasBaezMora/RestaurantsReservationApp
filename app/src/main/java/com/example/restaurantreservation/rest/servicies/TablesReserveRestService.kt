package com.example.restaurantreservation.rest.servicies

import com.example.restaurantreservation.rest.requestModels.ReservedTableRequest
import com.example.restaurantreservation.rest.responseModels.DefaultRestaurantResponse
import com.example.restaurantreservation.rest.responseModels.ReservedTableResponse
import retrofit2.Call
import retrofit2.http.*

interface TablesReserveRestService {

    @GET(value = "api/tables")
    fun getTablesReserve(): Call<DefaultRestaurantResponse<List<ReservedTableResponse>>>

    @DELETE(value = "api/tables/{id}")
    fun deleteTableReserve(@Path(value = "id") id: String): Call<DefaultRestaurantResponse<ReservedTableResponse>>

    @POST(value = "api/tables")
    fun addTableReserve(@Body dataReservedTable: ReservedTableRequest): Call<DefaultRestaurantResponse<ReservedTableResponse>>

}