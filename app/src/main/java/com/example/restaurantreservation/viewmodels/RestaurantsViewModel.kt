package com.example.restaurantreservation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantreservation.rest.RetrofitInstance
import com.example.restaurantreservation.rest.responseModels.DefaultRestaurantResponse
import com.example.restaurantreservation.rest.responseModels.RestaurantResponse
import com.example.restaurantreservation.rest.servicies.RestaurantRestService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class RestaurantsViewModel: ViewModel() {

    private val retrofitInstanceServiceRestaurant = RetrofitInstance()
        .getInstance()
        .create(RestaurantRestService::class.java)

    fun getRestaurants(): LiveData<Response<DefaultRestaurantResponse<List<RestaurantResponse>>>> {
        val responseRestaurants = MutableLiveData<Response<DefaultRestaurantResponse<List<RestaurantResponse>>>>()
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                retrofitInstanceServiceRestaurant
                    .getRestaurants()
                    .execute()
            }
            responseRestaurants.postValue(result)
        }
        return responseRestaurants
    }

}