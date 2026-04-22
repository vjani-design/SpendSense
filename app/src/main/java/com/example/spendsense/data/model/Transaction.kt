package com.example.spendsense.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

data class Transaction(

    @get:Exclude
    var id: String = "",   // not stored in Firestore

    val amount: Double = 0.0,
    val type: String = "",           // "EXPENSE" / "INCOME"
    val category: String = "",
    val note: String = "",
    val paymentMethod: String = "",

    val date: Timestamp? = null,
    val createdAt: Timestamp? = null,

    val description: String = ""
)