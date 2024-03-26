package com.github.se.wanderpals.model.data

data class User(
    val userId: String,
    val name: String,
    val email: String,
    val role: String,
    val permissions: List<String>
)
