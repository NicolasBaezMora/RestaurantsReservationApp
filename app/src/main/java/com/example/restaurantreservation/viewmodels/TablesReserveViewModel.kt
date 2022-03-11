package com.example.restaurantreservation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantreservation.rest.RetrofitInstance
import com.example.restaurantreservation.rest.requestModels.ReservedTableRequest
import com.example.restaurantreservation.rest.responseModels.DefaultRestaurantResponse
import com.example.restaurantreservation.rest.responseModels.ReservedTableResponse
import com.example.restaurantreservation.rest.servicies.TablesReserveRestService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class TablesReserveViewModel: ViewModel() {

    val loading = MutableLiveData<Boolean>(false)
    private val retrofitInstanceServiceTableReserve = RetrofitInstance()
        .getInstance()
        .create(TablesReserveRestService::class.java)

    fun getTablesReserved(): LiveData<Response<DefaultRestaurantResponse<List<ReservedTableResponse>>>> {
        val responseTablesReserve = MutableLiveData<Response<DefaultRestaurantResponse<List<ReservedTableResponse>>>>()
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                retrofitInstanceServiceTableReserve
                    .getTablesReserve()
                    .execute()
            }
            responseTablesReserve.postValue(result)
        }
        return responseTablesReserve
    }


    fun deleteTableReserve(id: String): LiveData<Response<DefaultRestaurantResponse<ReservedTableResponse>>> {
        val tableReserveDeleted =
            MutableLiveData<Response<DefaultRestaurantResponse<ReservedTableResponse>>>()
        viewModelScope.launch {
            loading.postValue(true)
            val result = withContext(Dispatchers.IO) {
                retrofitInstanceServiceTableReserve.deleteTableReserve(id).execute()
            }
            loading.postValue(false)
            tableReserveDeleted.postValue(result)
        }
        return tableReserveDeleted
    }

    fun addTableReserve(dataTableReserve: ReservedTableRequest): LiveData<Response<DefaultRestaurantResponse<ReservedTableResponse>>> {
        val tableReserved =
            MutableLiveData<Response<DefaultRestaurantResponse<ReservedTableResponse>>>()
        viewModelScope.launch {
            loading.postValue(true)
            val result = withContext(Dispatchers.IO) {
                retrofitInstanceServiceTableReserve.addTableReserve(dataTableReserve).execute()
            }
            loading.postValue(false)
            tableReserved.postValue(result)
        }
        return tableReserved
    }


}