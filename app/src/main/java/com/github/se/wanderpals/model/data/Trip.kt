package com.github.se.wanderpals.model.data

import java.time.LocalDate

data class Trip(
    val tripId: String,
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalBudget: Double,
    val description: String,
    // These are IDs of the documents in their respective sub-collections
    val stops: List<String> = emptyList(),
    val users: List<String> = emptyList(),
    val suggestions: List<String> = emptyList()
)
