package com.example.restaurantreservation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantreservation.rest.RetrofitInstance
import com.example.restaurantreservation.rest.requestModels.RestaurantRequest
import com.example.restaurantreservation.rest.responseModels.DefaultRestaurantResponse
import com.example.restaurantreservation.rest.responseModels.RestaurantResponse
import com.example.restaurantreservation.rest.servicies.RestaurantRestService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class ViewRestaurantViewModel: ViewModel() {

    private val retrofitInstanceServiceRestaurant = RetrofitInstance()
        .getInstance()
        .create(RestaurantRestService::class.java)

    val loading = MutableLiveData<Boolean>(false)

    fun deleteRestaurant(id: String): LiveData<Response<DefaultRestaurantResponse<RestaurantResponse>>> {
        val newRestaurant =
            MutableLiveData<Response<DefaultRestaurantResponse<RestaurantResponse>>>()
        viewModelScope.launch {
            loading.postValue(true)
            val result = withContext(Dispatchers.IO) {
                retrofitInstanceServiceRestaurant.deleteRestaurant(id).execute()
            }
            loading.postValue(false)
            newRestaurant.postValue(result)
        }
        return newRestaurant
    }

}