package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

class FirestoreStopTest {

    @Test
    fun testFromStopConversion() {
        val geoCords = GeoCords(latitude = 34.0522, longitude = -118.2437) // Assuming a simple constructor
        val stop = Stop(
            stopId = "stop1",
            title = "Los Angeles",
            address = "123 LA Street",
            date = LocalDate.of(2024, 5, 1),
            startTime = LocalTime.of(10, 0),
            duration = 120, // 2 hours
            budget = 500.0,
            description = "Visit LA",
            geoCords = geoCords,
            website = "http://laexample.com",
            imageUrl = "http://laexample.com/image.png"
        )

        val firestoreStop = FirestoreStop.fromStop(stop)
        assertEquals("2024-05-01", firestoreStop.date)
        assertEquals("10:00:00", firestoreStop.startTime)
        assertEquals(120, firestoreStop.duration)
        assertEquals("stop1", firestoreStop.stopId)
        assertEquals("Los Angeles", firestoreStop.title)
        assertEquals("123 LA Street", firestoreStop.address)
        assertEquals(500.0, firestoreStop.budget, 0.0)
        assertEquals("Visit LA", firestoreStop.description)
        assertEquals(geoCords, firestoreStop.geoCords)
        assertEquals("http://laexample.com", firestoreStop.website)
        assertEquals("http://laexample.com/image.png", firestoreStop.imageUrl)
    }

    @Test
    fun testToStopConversion() {
        val geoCords = GeoCords(latitude = 34.0522, longitude = -118.2437)
        val firestoreStop = FirestoreStop(
            stopId = "stop1",
            title = "Los Angeles",
            address = "123 LA Street",
            date = "2024-05-01",
            startTime = "10:00",
            duration = 120, // 2 hours
            budget = 500.0,
            description = "Visit LA",
            geoCords = geoCords,
            website = "http://laexample.com",
            imageUrl = "http://laexample.com/image.png"
        )

        val stop = firestoreStop.toStop()
        assertEquals(LocalDate.of(2024, 5, 1), stop.date)
        assertEquals(LocalTime.of(10, 0), stop.startTime)
        assertEquals(120, stop.duration)
        assertEquals("stop1", stop.stopId)
        assertEquals("Los Angeles", stop.title)
        assertEquals("123 LA Street", stop.address)
        assertEquals(500.0, stop.budget, 0.0)
        assertEquals("Visit LA", stop.description)
        assertEquals(geoCords, stop.geoCords)
        assertEquals("http://laexample.com", stop.website)
        assertEquals("http://laexample.com/image.png", stop.imageUrl)
    }
}
