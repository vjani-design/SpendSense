package com.example.spendsense.model

data class Group(
    val id: String = "",
    val name: String = "",
    val code: String = "",
    val members: List<String> = emptyList()
)