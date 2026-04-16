package com.example.spendsense.data.model

data class Group(
    val id: String = "",
    val name: String = "",
    val code: String = "",
    val createdBy: String = "", // ✅ ADD THIS
    val members: Map<String, Boolean> = emptyMap()
)