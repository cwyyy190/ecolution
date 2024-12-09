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
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.cupcake.databinding.FragmentSummaryBinding
import com.example.cupcake.model.OrderViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.HashMap

/**
 * [SummaryFragment] contains a summary of the order details with a button to share the order
 * via another app.
 */
class SummaryFragment : Fragment() {

    // Binding object instance corresponding to the fragment_summary.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var binding: FragmentSummaryBinding? = null
    private lateinit var firebaseAuth : FirebaseAuth

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    private val sharedViewModel: OrderViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentSummaryBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        firebaseAuth = FirebaseAuth.getInstance()

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            summaryFragment = this@SummaryFragment
        }

        var textView = view.findViewById<TextView>(R.id.location)
        textView.text = getString(R.string.location_value, sharedViewModel.state.value, sharedViewModel.country.value)
    }

    /**
     * Submit the order by sharing out the order details to another app via an implicit intent.
     * Update user currentPoint and totalPoint in Firebase
     */
    fun sendOrder() {

        // Read and Update Firebase
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child("YBr2tqKwP9ct9mCICzcohODodui2")//ref.child(firebaseAuth.uid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    // Load user currentPoint & totalPoint from Firebase
                    val currentPoint = "${snapshot.child("currentPoints").value}"
                    val totalPoint = "${snapshot.child("totalPoints").value}"
                    val gainedPoint = sharedViewModel.finalPoint.value.toString().toDouble()
                    val updatedCurrentPoint = currentPoint.toDouble() + gainedPoint
                    val updatedTotalPoint = totalPoint.toDouble()+ gainedPoint

                    // Update user currentPoint & totalPoint in Firebase
                    val hashMap = HashMap<String,Any>()
                    hashMap["currentPoints"] = updatedCurrentPoint
                    hashMap["totalPoints"] = updatedTotalPoint

                    val dbRef = FirebaseDatabase.getInstance().getReference("Users")
                    dbRef.child("YBr2tqKwP9ct9mCICzcohODodui2")// dbRef.child(firebaseAuth.uid!!)
                        .updateChildren(hashMap)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        displayToast()
    }

    /**
     * Display custom toast message
     */
    private fun displayToast(){
        // Display custom toast
        val layout= layoutInflater.inflate(R.layout.custom_toast, null)
        Toast(requireContext()).apply{
            duration = Toast.LENGTH_LONG
            //setGravity(Gravity.CENTER,0,0)
            view = layout
        }.show()

        // Delay navigation after a period of time (after showing toast)
        Handler().postDelayed({
            findNavController().navigate(R.id.action_summaryFragment_to_startFragment)
        }, 1000)
    }

    /**
     * Cancel the order and start over.
     */
    fun cancelOrder() {
        // Reset order in view model
        sharedViewModel.resetOrder()

        // Navigate back to the [StartFragment] to start over
        findNavController().navigate(R.id.action_summaryFragment_to_startFragment)
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