package com.example.restaurantreservation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantreservation.rest.RetrofitInstance
import com.example.restaurantreservation.rest.requestModels.RestaurantRequest
import com.example.restaurantreservation.rest.responseModels.CityResponse
import com.example.restaurantreservation.rest.responseModels.DefaultCityResponse
import com.example.restaurantreservation.rest.responseModels.DefaultRestaurantResponse
import com.example.restaurantreservation.rest.responseModels.RestaurantResponse
import com.example.restaurantreservation.rest.servicies.CityRestService
import com.example.restaurantreservation.rest.servicies.RestaurantRestService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class UpdateRestaurantViewModel : ViewModel() {

    private val retrofitInstanceServiceCity = RetrofitInstance()
        .getInstance()
        .create(CityRestService::class.java)


    private val retrofitInstanceServiceRestaurant = RetrofitInstance()
        .getInstance()
        .create(RestaurantRestService::class.java)

    val loading = MutableLiveData<Boolean>(false)

    fun getCities(): LiveData<Response<DefaultCityResponse<List<CityResponse>>>> {
        val cities = MutableLiveData<Response<DefaultCityResponse<List<CityResponse>>>>()
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                retrofitInstanceServiceCity.getCities().execute()
            }
            cities.postValue(result)

        }
        return cities
    }

    fun updateRestaurant(
        id: String,
        name: String,
        description: String,
        address: String,
        image: File?,
        city: String
    ): LiveData<Response<DefaultRestaurantResponse<RestaurantResponse>>> {
        val newRestaurant =
            MutableLiveData<Response<DefaultRestaurantResponse<RestaurantResponse>>>()
        viewModelScope.launch {
            loading.postValue(true)
            if (image != null) withContext(Dispatchers.IO) {
                retrofitInstanceServiceRestaurant.updateRestaurantImage(
                    MultipartBody.Part.createFormData(
                        "file",
                        image.name,
                        RequestBody.create(MediaType.get("image/*"), image)
                    ),
                    id
                ).execute()
            }
            val result = withContext(Dispatchers.IO) {
                retrofitInstanceServiceRestaurant.updateRestaurant(
                    RestaurantRequest(
                        name,
                        description,
                        address,
                        city
                    ),
                    id
                ).execute()
            }
            loading.postValue(false)
            newRestaurant.postValue(result)
        }
        return newRestaurant
    }


}