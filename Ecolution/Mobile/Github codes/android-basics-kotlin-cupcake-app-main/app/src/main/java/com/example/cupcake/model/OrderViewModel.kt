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
package com.example.cupcake.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.roundToInt


/** Price for a each material */
private const val POINT_PER_PLASTIC = 180
private const val POINT_PER_PAPER = 360
private const val POINT_PER_GLASS = 270
private const val POINT_PER_METAL = 310
private const val POINT_PER_ORGANIC = 160
private const val POINT_PER_MIXED = 120

/**
 * [RecycleViewModel] holds information about a cupcake order in terms of quantity, flavor, and
 * pickup date. It also knows how to calculate the total price based on these order details.
 */
class OrderViewModel : ViewModel() {

    // Weight of waste
    private val _weight = MutableLiveData<Double>()
    val weight: LiveData<Double> = _weight

    // Recycle category
    private val _category = MutableLiveData<String>()
    val category: LiveData<String> = _category

    // Country of recycling station
    private val _country = MutableLiveData<String>()
    val country: LiveData<String> = _country

    // State of recycling station
    private val _state = MutableLiveData<String>()
    val state: LiveData<String> = _state

    // Bonus of the order so far
    private val _bonus = MutableLiveData<Int>()
    val bonus: LiveData<Int> = _bonus

    // Points of the order so far
    private val _point = MutableLiveData<Int>()
    val point: LiveData<Int> = _point

    // Final Points of the order so far
    private val _finalPoint = MutableLiveData<Int>()
    val finalPoint: LiveData<Int> = _finalPoint

    init {
        // Set initial values for the order
        resetOrder()
    }

    /**
     * Add/minus the weight of waste for this order.
     *
     * @param weight to order
     */
    fun minusWeight(weight: Double){
        if(validateWeight()){
            return
        }
        _weight.value = (_weight.value)?.minus(weight)
        updatePoint()
    }

    fun addWeight(weight: Double){
        _weight.value = (_weight.value)?.plus(weight)
        updatePoint()
    }

    /**
     * Set the flavor of cupcakes for this order. Only 1 flavor can be selected for the whole order.
     *
     * @param desiredCategory is the cupcake flavor as a string
     */
    fun setCategory(desiredCategory: String) {
        _category.value = desiredCategory
        _weight.value = 1.0
        updatePoint()
    }

    /**
     * Returns true if a flavor has not been selected for the order yet. Returns false otherwise.
     */
    fun hasNoCategorySet(): Boolean {
        return _category.value.isNullOrEmpty()
    }

    fun validateWeight(): Boolean {
        return _weight.value == 0.0
    }


    fun setCountry(Country: String){
        _country.value = Country
    }

    fun setState(State: String){
        _state.value = State
    }

    /**
     * Reset the order by using initial default values for the quantity, flavor, date, and price.
     */
    fun resetOrder() {
        _weight.value = 1.0
        _category.value = ""
        _point.value = 0
        _bonus.value = 0
        _finalPoint.value = 0
    }

    /**
     * Updates the point based on the order details.
     */
    private fun updatePoint() {
        var rate : Int = 0
        if(category.value.toString() == "Plastic"){
            rate = POINT_PER_PLASTIC
        }else if(category.value.toString() == "Paper"){
            rate = POINT_PER_PAPER
        }else if(category.value.toString() == "Glass"){
            rate = POINT_PER_GLASS
        }else if(category.value.toString() == "Metal"){
            rate = POINT_PER_METAL
        }else if(category.value.toString() == "Organic"){
            rate = POINT_PER_ORGANIC
        }else{
            rate = POINT_PER_MIXED
        }

         var calculatedPoint = (weight.value ?: 0).toDouble() * rate
        _point.value = calculatedPoint.roundToInt()
    }

    fun setBonusValue(bonus: Double){
        _bonus.value = _point.value?.times(bonus)?.roundToInt()
        extraPoint()
    }

    private fun extraPoint(){
        //_finalPoint.value = point.value?.times(_bonus.value?.roundToInt()!!)
        _finalPoint.value = _point.value?.plus(_bonus.value!!)
    }

}

