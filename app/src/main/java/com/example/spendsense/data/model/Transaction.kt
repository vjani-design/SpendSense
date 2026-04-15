package com.example.spendsense.model

data class Transaction(
    val id: String = "",
    val userId: String = "",
    val groupId: String = "",   // ✅ REQUIRED
    val type: String = "",
    val category: String = "",
    val amount: Double = 0.0,
    val note: String = "",
    val paymentMethod: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    var isRecurring: Boolean = false,
    var recurrenceType: String? = null,
    var nextDueDate: Long? = null
)