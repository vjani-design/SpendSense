package com.example.spendsense.data.model

data class Group(
    val id: String = "",
    val name: String = "",
    val code: String = "",
    val createdBy: String = "",
    val members: Map<String, Any> = emptyMap(),
    val invitedEmails: Map<String, Boolean> = emptyMap(),
    val isActive: Boolean = true
)