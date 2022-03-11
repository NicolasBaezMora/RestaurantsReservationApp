package com.example.restaurantreservation.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.restaurantreservation.databinding.FragmentTableReserveViewBinding
import com.example.restaurantreservation.rest.responseModels.ReservedTableResponse
import com.example.restaurantreservation.viewmodels.TablesReserveViewModel
import com.squareup.picasso.Picasso

class TableReserveViewFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentTableReserveViewBinding

    private lateinit var dataTableReserved: ReservedTableResponse

    private val navController by lazy { findNavController() }
    private val vm by lazy { ViewModelProvider(this).get(TablesReserveViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTableReserveViewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataTableReserved = arguments?.getSerializable("dataTableReserved") as ReservedTableResponse
        setupLayout()
    }

    private fun setupLayout() {
        binding.textViewNameRestaurant.text = dataTableReserved.restaurant?.name
        binding.textViewAddressRestaurant.text = dataTableReserved.restaurant?.address
        binding.textViewDateReserve.text = dataTableReserved.date.split("T")[0]
        binding.textViewTableNameRestaurant.text = dataTableReserved.tableName
        Picasso.get().load(dataTableReserved.restaurant?.image).into(binding.imageViewRestaurant)
        binding.btnCancelReserve.setOnClickListener(this)
        binding.cardViewBtnBack.setOnClickListener(this)

        vm.loading.observe(viewLifecycleOwner, Observer {
            if (it) binding.layoutLoad.visibility = View.VISIBLE
            else binding.layoutLoad.visibility = View.GONE
        })
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            binding.cardViewBtnBack.id -> {
                navController.navigateUp()
            }
            binding.btnCancelReserve.id -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Cancelar Reserva")
                builder.setMessage("Deseas cancelar la reserva?")
                builder.setPositiveButton("Si") { _, _ ->
                    vm.deleteTableReserve(dataTableReserved.id).observe(viewLifecycleOwner, Observer {
                        if (it.isSuccessful) {
                            val data = it.body()
                            if (data?.ok!!) {
                                Toast.makeText(requireContext(), "Reserva eliminada", Toast.LENGTH_SHORT).show()
                                navController.navigateUp()
                            } else {
                                Toast.makeText(requireContext(), "Hubo un error en la petición", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "No fue posible realizar la petición", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                builder.setNegativeButton("No") { _, _ ->

                }
                builder.create()
                builder.show()
            }
        }
    }

}