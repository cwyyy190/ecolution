package com.example.assignment.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import kotlin.math.roundToInt

private const val POINT_PER_PLASTIC = 180
private const val POINT_PER_PAPER = 240
private const val POINT_PER_GLASS = 350
private const val POINT_PER_METAL = 450
private const val POINT_PER_ORGANIC = 150
private const val POINT_PER_MIXED = 110


class RecycleViewModel: ViewModel() {
    private val _weight = MutableLiveData<Int>()
    val weight: LiveData<Int> = _weight

    private val _category = MutableLiveData<String>()
    val category: LiveData<String> = _category

    private val _point = MutableLiveData<Int>()
    val point: LiveData<String> = Transformations.map(_point){
        NumberFormat.getCurrencyInstance().format(it)
    }

    private val _location = MutableLiveData<String>()
    val location: LiveData<String> = _location

    init{
        resetRecycle()
    }

    fun setLocation(location: String){
        _location.value = location
    }

    fun setWeight(weightWaste: Int){
        _weight.value = weightWaste
        updatePoint()
    }

    fun setCategory(categoryWaste: String) {
        _category.value = categoryWaste
    }

    fun updatePoint(){
        var point: Int = when(_category.value){
            "Plastic" -> POINT_PER_PLASTIC
            "Paper" -> POINT_PER_PAPER
            "Glass" -> POINT_PER_GLASS
            "Metal" -> POINT_PER_METAL
            "Organic" -> POINT_PER_ORGANIC
            else -> POINT_PER_MIXED
        }

        var calculatedPoint : Double = (weight.value ?: 0) * point.toDouble()
        var formattedPoint : Int = calculatedPoint.roundToInt()
    }

    fun resetRecycle(){
        _weight.value = 0
        _category.value = ""
        _point.value= 0
    }



}