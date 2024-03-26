package com.github.se.wanderpals.model.data

import java.time.LocalDate

data class Suggestion(
    val suggestionId: String,
    val userId: String,
    val userName: String,
    val text: String,
    val createdAt: LocalDate,
    val stop: Stop, // Embed the Stop object directly
    val comments: List<Comment> = emptyList()
)
