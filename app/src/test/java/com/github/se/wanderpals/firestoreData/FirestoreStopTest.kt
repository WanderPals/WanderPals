package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class FirestoreStopTest {

  @Test
  fun testFromStopConversion() {
    val geoCords =
        GeoCords(latitude = 34.0522, longitude = -118.2437) // Assuming a simple constructor
    val stop =
        Stop(
            stopId = "stop1",
            title = "Los Angeles",
            address = "123 LA Street",
            time = LocalDate.of(2024, 5, 1),
            budget = 500.0,
            description = "Visit LA",
            geoCords = geoCords,
            website = "http://laexample.com",
            imageUrl = "http://laexample.com/image.png")

    val firestoreStop = FirestoreStop.fromStop(stop)
    assertEquals("2024-05-01", firestoreStop.time)
    assertEquals("stop1", firestoreStop.stopId)
    assertEquals(
        geoCords,
        firestoreStop.geoCords) // Direct comparison if GeoCords data class has equals/hashCode
    // Add more assertEquals for other properties as needed
  }

  @Test
  fun testToStopConversion() {
    val geoCords =
        GeoCords(latitude = 34.0522, longitude = -118.2437) // Assuming a simple constructor
    val firestoreStop =
        FirestoreStop(
            stopId = "stop1",
            title = "Los Angeles",
            address = "123 LA Street",
            time = "2024-05-01",
            budget = 500.0,
            description = "Visit LA",
            geoCords = geoCords,
            website = "http://laexample.com",
            imageUrl = "http://laexample.com/image.png")

    val stop = firestoreStop.toStop()
    assertEquals(LocalDate.of(2024, 5, 1), stop.time)
    assertEquals("stop1", stop.stopId)
    assertEquals(
        geoCords, stop.geoCords) // Direct comparison if GeoCords data class has equals/hashCode
    // Add more assertEquals for other properties as needed
  }
}
