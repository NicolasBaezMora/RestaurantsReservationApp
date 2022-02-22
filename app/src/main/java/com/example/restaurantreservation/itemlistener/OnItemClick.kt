package com.example.restaurantreservation.itemlistener

import android.view.View
import com.example.restaurantreservation.rest.responseModels.ParentElementResponse

interface OnItemClick<B> {
    fun onItemClickListener(item: ParentElementResponse, binding: B, view: View)
}