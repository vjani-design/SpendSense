package com.example.spendsense.model

data class Transaction(
    val id: String = "",
    val type: String = "", // "income" or "expense"
    val category: String = "",
    val amount: Double = 0.0,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)