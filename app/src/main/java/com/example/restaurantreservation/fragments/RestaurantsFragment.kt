package com.example.restaurantreservation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurantreservation.R
import com.example.restaurantreservation.adapters.RestaurantAdapter
import com.example.restaurantreservation.databinding.FragmentRestaurantsBinding
import com.example.restaurantreservation.databinding.ItemRestaurantBinding
import com.example.restaurantreservation.itemlistener.OnItemClick
import com.example.restaurantreservation.rest.responseModels.ParentElementResponse
import com.example.restaurantreservation.rest.responseModels.RestaurantResponse
import com.example.restaurantreservation.viewmodels.RestaurantsViewModel

class RestaurantsFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentRestaurantsBinding
    private lateinit var restaurantAdapter: RestaurantAdapter

    private val navController by lazy { findNavController() }
    private val vm by lazy { ViewModelProvider(this).get(RestaurantsViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRestaurantsBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRestaurantsAdapter()
        setupLayout()
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            binding.floatingBtnAddRestaurant.id -> {
                navController.navigate(R.id.action_restaurantsFragment_to_addRestaurantFragment)
            }
        }
    }

    private fun setupLayout() {
        binding.floatingBtnAddRestaurant.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        getRestaurants()
    }

    private fun setupRestaurantsAdapter() {
        restaurantAdapter = RestaurantAdapter(object : OnItemClick<ItemRestaurantBinding> {
            override fun onItemClickListener(
                item: ParentElementResponse,
                binding: ItemRestaurantBinding,
                view: View
            ) {
                val itemRestaurant = item as RestaurantResponse
                navController.navigate(R.id.action_restaurantsFragment_to_restaurantViewFragment, bundleOf(
                    "dataRestaurant" to itemRestaurant
                ))
            }
        })
        binding.recyclerViewRestaurants.adapter = restaurantAdapter
        binding.recyclerViewRestaurants.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun getRestaurants() {
        vm.getRestaurants().observe(viewLifecycleOwner, Observer {
            if (it.isSuccessful) {
                val dataResponse = it.body()
                if (dataResponse?.ok!!) {
                    restaurantAdapter.listElements = dataResponse.body
                    restaurantAdapter.notifyDataSetChanged()
                }
            } else {
                Toast.makeText(requireContext(), "Ocurrio un error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}