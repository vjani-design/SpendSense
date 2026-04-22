package com.example.spendsense.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object CurrencyManager {

    private val _currency = MutableStateFlow("₹")
    val currency: StateFlow<String> = _currency

    fun setCurrency(symbol: String) {
        _currency.value = symbol
    }
}