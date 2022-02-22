package com.example.restaurantreservation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantreservation.rest.RetrofitInstance
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

class AddRestaurantViewModel : ViewModel() {

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

    fun addRestaurant(
        name: String,
        description: String,
        address: String,
        image: File,
        city: String
    ): LiveData<Response<DefaultRestaurantResponse<RestaurantResponse>>> {
        val newRestaurant =
            MutableLiveData<Response<DefaultRestaurantResponse<RestaurantResponse>>>()
        viewModelScope.launch {
            loading.postValue(true)
            val result = withContext(Dispatchers.IO) {
                retrofitInstanceServiceRestaurant.addRestaurant(
                    mapOf(
                        "name" to toRequestBody(name),
                        "description" to toRequestBody(description),
                        "address" to toRequestBody(address),
                        "city" to toRequestBody(city)
                    ),
                    MultipartBody.Part.createFormData(
                        "file",
                        image.name,
                        RequestBody.create(MediaType.get("image/*"), image)
                    )
                ).execute()
            }
            loading.postValue(false)
            newRestaurant.postValue(result)
        }
        return newRestaurant
    }

    private fun toRequestBody(value: String): RequestBody = RequestBody.create(MediaType.parse("text/plain"), value)

}