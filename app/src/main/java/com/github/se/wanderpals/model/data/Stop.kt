package com.github.se.wanderpals.model.data

import java.time.LocalDate

/**
 * Represents a specific stop or destination within a trip itinerary. Each stop is defined by a
 * unique location, including geographical coordinates, and contains details about the visit,
 * including a website link for more information.
 *
 * @param stopId Unique identifier for the stop, typically generated by the database.
 * @param location A name or title for the stop, providing a quick reference.
 * @param address The physical address of the stop for easier location and navigation.
 * @param time The scheduled date for the stop. This helps in planning the trip itinerary.
 * @param budget Estimated budget required for the stop, covering expenses like entry fees or
 *   activities.
 * @param description A brief overview or notes about the stop, offering more context or
 *   information.
 * @param geoCords Geographical coordinates (latitude and longitude) pinpointing the exact location.
 * @param website Optional. A URL to a website providing additional information about the stop.
 *   Empty by default.
 * @param imageUrl URL for an image of the stop, optional.
 */
data class Stop(
    val stopId: String,
    val location: String,
    val address: String,
    val time: LocalDate,
    val budget: Double,
    val description: String,
    val geoCords: GeoCords,
    val website: String = "",
    val imageUrl: String = "",
)