package com.github.se.wanderpals.model.data

import java.time.LocalDate

data class Stop(
    val stopId: String,
    val location: String,
    val time: LocalDate,
    val budget: Double,
    val description: String,
    val geoCords: GeoCords
)
