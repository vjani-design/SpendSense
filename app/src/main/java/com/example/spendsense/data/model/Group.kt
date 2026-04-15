package com.example.spendsense.data.model

data class Group(
    val id: String = "",
    val name: String = "",
    val code: String = "",
    val members: List<String> = emptyList()
)