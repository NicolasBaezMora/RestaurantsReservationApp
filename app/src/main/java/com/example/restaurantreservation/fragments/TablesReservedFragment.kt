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
import com.example.restaurantreservation.adapters.ReserveTableAdapter
import com.example.restaurantreservation.databinding.FragmentTablesReservedBinding
import com.example.restaurantreservation.databinding.ItemReserveBinding
import com.example.restaurantreservation.itemlistener.OnItemClick
import com.example.restaurantreservation.rest.responseModels.ParentElementResponse
import com.example.restaurantreservation.rest.responseModels.ReservedTableResponse
import com.example.restaurantreservation.viewmodels.TablesReserveViewModel

class TablesReservedFragment : Fragment() {

    private lateinit var binding: FragmentTablesReservedBinding
    private lateinit var tablesReserveAdapter: ReserveTableAdapter

    private val navController by lazy { findNavController() }
    private val vm by lazy { ViewModelProvider(this).get(TablesReserveViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTablesReservedBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTablesReserveAdapter()
    }

    override fun onResume() {
        super.onResume()
        getTableReserved()
    }

    private fun setupTablesReserveAdapter() {
        tablesReserveAdapter = ReserveTableAdapter(object : OnItemClick<ItemReserveBinding> {
            override fun onItemClickListener(
                item: ParentElementResponse,
                binding: ItemReserveBinding,
                view: View
            ) {
                val itemTableReserved = item as ReservedTableResponse
                navController.navigate(R.id.action_tablesReservedFragment_to_tableReserveViewFragment, bundleOf(
                    "dataTableReserved" to itemTableReserved
                ))
            }
        })
        binding.recyclerViewTablesReserve.adapter = tablesReserveAdapter
        binding.recyclerViewTablesReserve.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun getTableReserved() {
        vm.getTablesReserved().observe(viewLifecycleOwner, Observer {
            if (it.isSuccessful) {
                val dataResponse = it.body()
                if (dataResponse?.ok!!) {
                    tablesReserveAdapter.listElements = dataResponse.body
                    tablesReserveAdapter.notifyDataSetChanged()
                }
            } else {
                Toast.makeText(requireContext(), "Ocurrio un error", Toast.LENGTH_SHORT).show()
            }
        })
    }

}