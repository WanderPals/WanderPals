package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.TripNotification
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import junit.framework.TestCase.assertEquals
import org.junit.Test

class TripNotificationTest {

  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  @Test
  fun testFromTripNotificationToFirestoreTripNotification() {
    val tripNotification =
        TripNotification(
            title = "Upcoming Journey",
            path = "/user/trips/upcoming/journey",
            timestamp = LocalDateTime.parse("2024-04-01T12:30:00", formatter))

    val firestoreTripNotification = FirestoreTripNotification.fromTripNotification(tripNotification)

    assertEquals(tripNotification.title, firestoreTripNotification.title)
    assertEquals(tripNotification.path, firestoreTripNotification.path)
    assertEquals(tripNotification.timestamp.format(formatter), firestoreTripNotification.timestamp)
  }

  @Test
  fun testFromFirestoreTripNotificationToTripNotification() {
    val firestoreTripNotification =
        FirestoreTripNotification(
            title = "Upcoming Journey",
            path = "/user/trips/upcoming/journey",
            timestamp = "2024-04-01T12:30:00")

    val tripNotification = firestoreTripNotification.toTripNotification()

    assertEquals(firestoreTripNotification.title, tripNotification.title)
    assertEquals(firestoreTripNotification.path, tripNotification.path)
    assertEquals(
        LocalDateTime.parse(firestoreTripNotification.timestamp, formatter),
        tripNotification.timestamp)
  }
}
