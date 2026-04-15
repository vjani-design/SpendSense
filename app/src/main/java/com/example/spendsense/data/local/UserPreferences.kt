package com.example.spendsense.data.local

import android.content.Context

class UserPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // =========================
    // SAVE PROFILE
    // =========================
    fun saveUser(
        name: String,
        incomeRange: String,
        currency: String
    ) {
        prefs.edit()
            .putString("name", name)
            .putString("incomeRange", incomeRange)
            .putString("currency", currency)
            .apply()
    }

    // =========================
    // GET NAME
    // =========================
    fun getName(): String {
        return prefs.getString("name", "") ?: ""
    }

    // =========================
    // GET INCOME RANGE
    // =========================
    fun getIncomeRange(): String {
        return prefs.getString("incomeRange", "") ?: ""
    }

    // =========================
    // GET CURRENCY
    // =========================
    fun getCurrency(): String {
        return prefs.getString("currency", "₹") ?: "₹"
    }

    // =========================
    // CLEAR DATA (LOGOUT)
    // =========================
    fun clear() {
        prefs.edit().clear().apply()
    }
}