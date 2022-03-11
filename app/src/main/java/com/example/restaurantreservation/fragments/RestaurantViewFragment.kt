package com.example.restaurantreservation.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
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
import com.example.restaurantreservation.databinding.DialogCustomBinding
import com.example.restaurantreservation.databinding.FragmentRestaurantViewBinding
import com.example.restaurantreservation.rest.requestModels.ReservedTableRequest
import com.example.restaurantreservation.rest.responseModels.RestaurantResponse
import com.example.restaurantreservation.viewmodels.TablesReserveViewModel
import com.example.restaurantreservation.viewmodels.ViewRestaurantViewModel
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class RestaurantViewFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentRestaurantViewBinding
    private lateinit var dataItemRestaurant: RestaurantResponse

    private val navController by lazy { findNavController() }
    private val vm by lazy { ViewModelProvider(this).get(ViewRestaurantViewModel::class.java) }
    private val vmTR by lazy { ViewModelProvider(this).get(TablesReserveViewModel::class.java) }

    private val dataTableReserved = ReservedTableRequest(null, null, null)

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
        dataTableReserved.id = dataItemRestaurant.id
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
            } else binding.layoutLoad.visibility = View.GONE
        })
        vmTR.loading.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.layoutLoad.visibility = View.VISIBLE
            } else binding.layoutLoad.visibility = View.GONE
        })
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.btnAddReserve.id -> {
                val builder = AlertDialog.Builder(requireContext())
                val view =
                    DialogCustomBinding.bind(layoutInflater.inflate(R.layout.dialog_custom, null))
                val alertDialog = builder.create()
                alertDialog.setView(view.root)
                alertDialog.show()
                view.btnNext.setOnClickListener {
                    val tableName = view.textFieldNameTable.editText?.text.toString().trim()
                    if (tableName.isNotEmpty()) {
                        dataTableReserved.name = tableName
                        alertDialog.cancel()
                        val dateNow =
                            SimpleDateFormat("yyyy/M/dd").format(Date()).toString().split("/")
                        val builderAlertDatePicker =
                            DatePickerDialog(requireContext(), { view, yy, mm, dd ->
                                dataTableReserved.date = "$yy-$mm-$dd"
                                vmTR.addTableReserve(dataTableReserved)
                                    .observe(viewLifecycleOwner, Observer {
                                        if (it.isSuccessful) {
                                            val dataResponse = it.body()
                                            if (dataResponse?.ok!!) {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Reserva realizada satisfactoriamente",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Hubo un error en la petición",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } else {
                                            Toast.makeText(
                                                requireContext(),
                                                "No se pudo realizar la operación",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                            }, dateNow[0].toInt(), dateNow[1].toInt(), dateNow[2].toInt())
                        builderAlertDatePicker.create()
                        builderAlertDatePicker.show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Debes escribir el nombre de la mesa a reservar",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
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
                    vm.deleteRestaurant(dataItemRestaurant.id)
                        .observe(viewLifecycleOwner, Observer {
                            if (it.isSuccessful) {
                                val dataResponse = it.body()
                                if (dataResponse?.ok!!) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Se elimino el restaurante",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigateUp()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Hubo un error en la petición",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "No fue posible realizar la operación",
                                    Toast.LENGTH_SHORT
                                ).show()
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