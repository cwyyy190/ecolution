package com.example.assignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.assignment.databinding.FragmentClockInStartBinding
import com.example.assignment.model.RecycleViewModel

class ClockInStartFragment : Fragment() {
    private var binding: FragmentClockInStartBinding? = null

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    private val sharedViewModel: RecycleViewModel by activityViewModels()


   // override fun onCreate(savedInstanceState: Bundle?) {
    //    super.onCreate(savedInstanceState)

        //val locationName : Array<String> = resources.getStringArray(R.array.locations)
       // val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locationName)

    //val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locationName)

        //spinner.adapter = arrayAdapter
   // }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentClockInStartBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding?.ClockInStartFragment = this
    }

    fun recycleWaste(location: String){
        sharedViewModel.setLocation(location)

        findNavController().navigate(R.id.action_clockInStartFragment_to_materialFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}