package com.example.restaurantreservation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantreservation.R
import com.example.restaurantreservation.databinding.ItemRestaurantBinding
import com.example.restaurantreservation.itemlistener.OnItemClick
import com.example.restaurantreservation.rest.responseModels.RestaurantResponse
import com.squareup.picasso.Picasso

class RestaurantAdapter(
    private val onItemClick: OnItemClick<ItemRestaurantBinding>
): RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    var listElements = listOf<RestaurantResponse>()

    inner class RestaurantViewHolder(itemViewRestaurant: View): RecyclerView.ViewHolder(itemViewRestaurant) {
        private val restaurantItemBinding =  ItemRestaurantBinding.bind(itemViewRestaurant)
        fun bind(itemRestaurant: RestaurantResponse) {
            restaurantItemBinding.textViewNameRestaurant.text = itemRestaurant.name
            restaurantItemBinding.textViewCity.text = itemRestaurant.city.name
            restaurantItemBinding.textViewAddress.text = itemRestaurant.address
            Picasso.get().load(itemRestaurant.image).into(restaurantItemBinding.imageViewRestaurant)

            restaurantItemBinding.viewRestaurant.setOnClickListener {
                onItemClick.onItemClickListener(itemRestaurant, restaurantItemBinding, it)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(listElements[position])
    }

    override fun getItemCount(): Int = listElements.size

}