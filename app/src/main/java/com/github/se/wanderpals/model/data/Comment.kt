package com.github.se.wanderpals.model.data

import java.time.LocalDate

data class Comment(
    val commentId: String,
    val userId: String,
    val userName: String,
    val text: String,
    val createdAt: LocalDate // Consider using a Date type
)
