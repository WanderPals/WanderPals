package com.github.se.wanderpals.model.data

/**
 * Represents geographical coordinates with a latitude and a longitude.
 * Utilized to pinpoint locations for trips, stops, or suggestions within the application.
 *
 * @property latitude The north-south position, with values ranging from -90.0 to 90.0.
 * @property longitude The east-west position, with values ranging from -180.0 to 180.0.
 */
data class GeoCords(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
