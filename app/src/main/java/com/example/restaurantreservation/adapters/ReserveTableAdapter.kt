package com.example.restaurantreservation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantreservation.R
import com.example.restaurantreservation.databinding.ItemReserveBinding
import com.example.restaurantreservation.databinding.ItemRestaurantBinding
import com.example.restaurantreservation.itemlistener.OnItemClick
import com.example.restaurantreservation.rest.responseModels.ReservedTableResponse
import com.squareup.picasso.Picasso

class ReserveTableAdapter(
    private val onItemClick: OnItemClick<ItemReserveBinding>
): RecyclerView.Adapter<ReserveTableAdapter.ReserveTableViewHolder>() {

    var listElements = listOf<ReservedTableResponse>()

    inner class ReserveTableViewHolder(itemReserveTable: View): RecyclerView.ViewHolder(itemReserveTable) {

        private val binding = ItemReserveBinding.bind(itemReserveTable)
        fun bind(reserve: ReservedTableResponse) {
            binding.textViewNameRestaurant.text = reserve.restaurant?.name
            binding.textViewDateReserve.text = reserve.date.split("T")[0]
            binding.textViewNameTable.text = reserve.tableName
            Picasso.get().load(reserve.restaurant?.image).into(binding.imageViewRestaurantReserve)

            binding.viewReserve.setOnClickListener {
                onItemClick.onItemClickListener(reserve, binding, it)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReserveTableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reserve, parent, false)
        return ReserveTableViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReserveTableViewHolder, position: Int) {
        holder.bind(listElements[position])
    }

    override fun getItemCount(): Int = listElements.size

}