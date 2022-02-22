package com.example.restaurantreservation.fragments

import android.app.AlertDialog
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
import com.example.restaurantreservation.R
import com.example.restaurantreservation.databinding.FragmentRestaurantViewBinding
import com.example.restaurantreservation.rest.responseModels.RestaurantResponse
import com.example.restaurantreservation.viewmodels.ViewRestaurantViewModel
import com.squareup.picasso.Picasso

class RestaurantViewFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentRestaurantViewBinding
    private lateinit var dataItemRestaurant: RestaurantResponse

    private val navController by lazy { findNavController() }
    private val vm by lazy { ViewModelProvider(this).get(ViewRestaurantViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRestaurantViewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataItemRestaurant = arguments?.getSerializable("dataRestaurant") as RestaurantResponse
        setupLayoutRestaurant()
    }

    private fun setupLayoutRestaurant() {
        Picasso.get().load(dataItemRestaurant.image).into(binding.imageViewRestaurant)
        binding.textViewNameRestaurant.text = dataItemRestaurant.name
        binding.textViewDescriptionRestaurant.text = dataItemRestaurant.description
        binding.textViewAddressRestaurant.text = dataItemRestaurant.address
        binding.btnAddReserve.setOnClickListener(this)
        binding.cardViewBtnBack.setOnClickListener(this)
        binding.cardViewBtnEdit.setOnClickListener(this)
        binding.cardViewBtnDelete.setOnClickListener(this)

        vm.loading.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.layoutLoad.visibility = View.VISIBLE
            }
            else binding.layoutLoad.visibility = View.GONE
        })
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            binding.btnAddReserve.id -> {
                Toast.makeText(requireContext(), "Reservado papi", Toast.LENGTH_SHORT).show()
            }
            binding.cardViewBtnBack.id -> {
                navController.navigateUp()
            }
            binding.cardViewBtnEdit.id -> {
                navController.navigate(
                    R.id.action_restaurantViewFragment_to_updateRestaurantFragment,
                    bundleOf(
                        "dataRestaurant" to dataItemRestaurant
                    )
                )
            }
            binding.cardViewBtnDelete.id -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Eliminar restaurante")
                builder.setMessage("Deseas eliminar este restaurante?")
                builder.setPositiveButton("Si") { _, _ ->
                    vm.deleteRestaurant(dataItemRestaurant.id).observe(viewLifecycleOwner, Observer {
                        if (it.isSuccessful) {
                            val dataResponse = it.body()
                            if (dataResponse?.ok!!) {
                                Toast.makeText(requireContext(), "Se elimino el restaurante", Toast.LENGTH_SHORT).show()
                                navController.navigateUp()
                            } else {
                                Toast.makeText(requireContext(), "Hubo un error en la petición", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "No fue posible realizar la operación", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                builder.setNegativeButton("Cancelar") { _, _ ->

                }
                builder.create()
                builder.show()
            }
        }
    }

}