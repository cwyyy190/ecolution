/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.cupcake

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.cupcake.databinding.FragmentStartBinding
import com.example.cupcake.model.OrderViewModel

/**
 * This is the first screen of the Cupcake app. The user can choose how many cupcakes to order.
 */
class StartFragment : Fragment() {

    // Binding object instance corresponding to the fragment_start.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var binding: FragmentStartBinding? = null

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    private val sharedViewModel: OrderViewModel by activityViewModels()

    // Initialize
    lateinit var spinnerCountry : Spinner
    lateinit var spinnerState : Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentStartBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.startFragment = this

        // Spinner adapter for Country
        spinnerCountry = view.findViewById(R.id.spinner_country) as Spinner
        val country = resources.getStringArray(R.array.countries)
        val arrayAdapterCountry = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, country)

        // Attached arrayAdapter to Spinner
        spinnerCountry.adapter = arrayAdapterCountry
        spinnerCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                sharedViewModel.setCountry(country[p2])
                // Nested Spinner adapter for state
                fillSpinnerState(view, p2)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
                //showDefaultLocation(view)
            }
        }
    }

    private fun fillSpinnerState(view: View, index: Int){
        val state = when(index){
            0 -> resources.getStringArray(R.array.states_brunei)
            1-> resources.getStringArray(R.array.states_indonesia)
            2 -> resources.getStringArray(R.array.states_malaysia)
            3-> resources.getStringArray(R.array.states_singapore)
            else -> resources.getStringArray(R.array.states_thailand)
        }

        spinnerState = view.findViewById(R.id.spinner_state) as Spinner
        val arrayAdapterState = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, state)

        // Attached arrayAdapter to Spinner
        spinnerState.adapter = arrayAdapterState
        spinnerState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                sharedViewModel.setState(state[p2])

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
                //showDefaultLocation(view)
            }
        }
    }

    /**
     * Start an order with the desired quantity of cupcakes and navigate to the next screen.
     */
    fun startRecycle(){
        if (sharedViewModel.hasNoCategorySet()) {
            sharedViewModel.setCategory(getString(R.string.plastic))
        }

        findNavController().navigate(R.id.action_startFragment_to_categoryFragment)
    }

    /**
     * This fragment lifecycle method is called when the view hierarchy associated with the fragment
     * is being removed. As a result, clear out the binding object.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}