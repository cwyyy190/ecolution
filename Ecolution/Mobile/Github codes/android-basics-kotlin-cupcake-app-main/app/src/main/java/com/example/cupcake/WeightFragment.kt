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
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.cupcake.databinding.FragmentWeightBinding
import com.example.cupcake.model.OrderViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * [WeightFragment] allows the user to choose a weight for the recycling order.
 */
class WeightFragment : Fragment() {

    // Binding object instance corresponding to the fragment_pickup.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var binding: FragmentWeightBinding? = null

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    private val sharedViewModel: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentWeightBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            weightFragment = this@WeightFragment
        }

        var minusButton = view.findViewById<Button>(R.id.minus_weight)
        var nextButton = view.findViewById<Button>(R.id.next_button)
        disableButton(minusButton, nextButton)
    }

    private fun disableButton(minusButton: Button, nextButton: Button){
        //Initialize as 2
        var value = 2

        minusButton.setOnClickListener{
            value--
            nextButton.text = value.toString()
            if(value == 0){
                minusButton.isEnabled = false
            }
        }
    }

    fun validateOrder(){
        if(sharedViewModel.validateWeight()){
            Toast.makeText(requireContext(), "Please do not select a empty weight.", Toast.LENGTH_LONG).show()
        }
        else{
            // Add bonus point when the user have ranking of top 5
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child("YBr2tqKwP9ct9mCICzcohODodui2")//ref.child(firebaseAuth.uid!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        // Load user ranking from Firebase
                        val ranking = "${snapshot.child("Rank").value}"
                        var bonus = when (ranking.toInt()) {
                            1 -> 0.25
                            2 -> 0.15
                            3 -> 0.10
                            4 -> 0.05
                            5 -> 0.025
                            else -> 0.0
                        }
                        sharedViewModel.setBonusValue(bonus)
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            goToNextScreen()
        }
    }

    //Navigate to the next screen (summary fragment)
    private fun goToNextScreen() {
        findNavController().navigate(R.id.action_weightFragment_to_summaryFragment)
    }

    //reset the order and navigate to start fragment
    fun cancelOrder() {
        // Reset order in view model
        sharedViewModel.resetOrder()

        // Navigate back to the [StartFragment] to start over
        findNavController().navigate(R.id.action_weightFragment_to_startFragment)
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