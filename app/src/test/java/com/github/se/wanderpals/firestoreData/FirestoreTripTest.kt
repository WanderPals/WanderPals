package com.github.se.wanderpals.firestoreData

import FirestoreTrip
import com.github.se.wanderpals.model.data.Trip
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class FirestoreTripTest {

  @Test
  fun testFromTripConversion() {
    val trip =
        Trip(
            tripId = "1",
            title = "Test Trip",
            startDate = LocalDate.of(2024, 3, 27),
            endDate = LocalDate.of(2024, 4, 10),
            totalBudget = 2000.0,
            description = "Test Description",
            imageUrl = "http://example.com/image.png",
            stops = listOf("lskieo", "glgoskisu"),
            users = listOf("owlwwo", "lspwiee"),
            suggestions = listOf("kdsfdf", "dkfdfkdofo"),
            announcements = listOf("Announcement1", "Announcement2"),
            expenses = listOf("exp1", "exp2"))

    val firestoreTrip = FirestoreTrip.fromTrip(trip)
    assertEquals("2024-03-27", firestoreTrip.startDate)
    assertEquals("2024-04-10", firestoreTrip.endDate)
    assertEquals("1", firestoreTrip.tripId)
    assertEquals(trip.announcements, firestoreTrip.announcements)
    assertEquals(trip.expenses, firestoreTrip.expenses)
  }

  @Test
  fun testToTripConversion() {
    val firestoreTrip =
        FirestoreTrip(
            tripId = "1",
            title = "Test Trip",
            startDate = "2024-03-27",
            endDate = "2024-04-10",
            totalBudget = 2000.0,
            description = "Test Description",
            imageUrl = "http://example.com/image.png",
            stops = listOf("Stop1", "Stop2"),
            users = listOf("User1", "User2"),
            suggestions = listOf("Suggestion1", "Suggestion2"),
            announcements = listOf("Announcement1", "Announcement2"),
            expenses = listOf("exp1", "exp2"))

    val trip = firestoreTrip.toTrip()
    assertEquals(LocalDate.of(2024, 3, 27), trip.startDate)
    assertEquals(LocalDate.of(2024, 4, 10), trip.endDate)
    assertEquals(firestoreTrip.announcements, trip.announcements)
    assertEquals(firestoreTrip.expenses, trip.expenses)
    // Additional assertions can be added to verify other fields as needed
  }
}
