package com.example.cupcake.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

private const val PRICE_PER_CUPCAKE = 2.00
private const val PRICE_FOR_SAME_DAY_PICKUP = 3.00
const val TAG = "OrderViewModel"

class OrderViewModel : ViewModel() {
    private val _quantity = MutableLiveData<Int>()
    var quantity: LiveData<Int> = _quantity

    private val _flavor = MutableLiveData<MutableSet<String>>()
    var flavor: LiveData<MutableSet<String>> = _flavor

    private val _date = MutableLiveData<String>()
    var date: LiveData<String> = _date

    private val _price = MutableLiveData<Double>()
    val price: LiveData<String> = Transformations.map(_price) {
        // Format the price into the local currency and return this as LiveData<String>
        NumberFormat.getCurrencyInstance().format(it)
    }

    val dateOptions = getPickupOptions()

    // 注文者の名前
    private val _name = MutableLiveData<String>()
    var name: LiveData<String> = _name

    // 注文が複数か否か
    private val _isMultiOrder = MutableLiveData<Boolean>()
    var isMultiOrder: LiveData<Boolean> = _isMultiOrder


    init {
        resetOrder()
    }

    fun setQuantity(numberCupcakes: Int) {
        _quantity.value = numberCupcakes
        updatePrice()
        setIsMultiOrder()
    }

    fun setFlavor(desiredFlavor: String) {
        if (_isMultiOrder.value == true) {
            if (_flavor.value?.contains(desiredFlavor) == true) {
                _flavor.value?.remove(desiredFlavor)
            } else {
                _flavor.value?.add(desiredFlavor)
            }
        } else {
            _flavor.value?.clear()
            _flavor.value = mutableSetOf()
            _flavor.value?.add(desiredFlavor)
        }
        Log.d(TAG, "_flavor.value : ${_flavor.value}")
    }

    fun initFlavor() {
        if (_isMultiOrder.value == false && _flavor.value?.count()!! >= 2) {
            for (i in 1 until _flavor.value?.count()!!) {
                val removeItem = _flavor.value!!.first()
                _flavor.value!!.remove(removeItem)
            }
        }
    }

    fun outputFlavorString(): String {
        val flavorList = _flavor.value?.toList()
        var flavorString = ""
        if (flavorList != null) {
            for (i in 0 until flavorList.count()) {
                flavorString = if (i == 0) {
                    flavorList[i]
                } else {
                    flavorString + ", " + flavorList[i]
                }
            }
        }
        return flavorString
    }

    fun setDate(pickupDate: String) {
        _date.value = pickupDate
        updatePrice()
    }

    fun hasNoFlavorSet(): Boolean {
        return _flavor.value.isNullOrEmpty()
    }

    fun setUserName(userName: String) {
        _name.value = userName
    }

    private fun getPickupOptions(): List<String> {
        val options = mutableListOf<String>()
        val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
        val calendar = Calendar.getInstance()
        // Create a list of dates starting with the current date and the following 3 dates
        repeat(4) {
            options.add(formatter.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        return options
    }

    fun resetOrder() {
        _quantity.value = 0
        _flavor.value = mutableSetOf()
        _flavor.value?.add("Vanilla")
        _date.value = dateOptions[0]
        _price.value = 0.0
        _name.value = ""
        _isMultiOrder.value = false
    }

    private fun updatePrice() {
        var calculatePrice = (_quantity.value ?: 0) * PRICE_PER_CUPCAKE
        if (dateOptions[0] == _date.value) {
            calculatePrice += PRICE_FOR_SAME_DAY_PICKUP
        }
        _price.value = calculatePrice
    }

    private fun setIsMultiOrder() {
        _isMultiOrder.value = _quantity.value!! > 1
    }
}