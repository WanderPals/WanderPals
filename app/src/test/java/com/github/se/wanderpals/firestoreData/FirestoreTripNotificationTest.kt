package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.TripNotification
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.junit.Test
import junit.framework.TestCase.assertEquals

class TripNotificationTest {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @Test
    fun testFromTripNotificationToFirestoreTripNotification() {
        val tripNotification = TripNotification(
            notificationId = "notif123",
            userId = "user456",
            title = "Trip Alert",
            userName = "Jane Doe",
            description = "Your trip to Paris is coming up soon!",
            timestamp = LocalDateTime.parse("2024-04-01T12:30:00", formatter)
        )

        val firestoreTripNotification = FirestoreTripNotification.fromTripNotification(tripNotification)

        assertEquals(tripNotification.notificationId, firestoreTripNotification.notificationId)
        assertEquals(tripNotification.userId, firestoreTripNotification.userId)
        assertEquals(tripNotification.title, firestoreTripNotification.title)
        assertEquals(tripNotification.userName, firestoreTripNotification.userName)
        assertEquals(tripNotification.description, firestoreTripNotification.description)
        assertEquals(tripNotification.timestamp.format(formatter), firestoreTripNotification.timestamp)
    }

    @Test
    fun testFromFirestoreTripNotificationToTripNotification() {
        val firestoreTripNotification = FirestoreTripNotification(
            notificationId = "notif123",
            userId = "user456",
            title = "Trip Alert",
            userName = "Jane Doe",
            description = "Your trip to Paris is coming up soon!",
            timestamp = "2024-04-01T12:30:00"
        )

        val tripNotification = firestoreTripNotification.toTripNotification()

        assertEquals(firestoreTripNotification.notificationId, tripNotification.notificationId)
        assertEquals(firestoreTripNotification.userId, tripNotification.userId)
        assertEquals(firestoreTripNotification.title, tripNotification.title)
        assertEquals(firestoreTripNotification.userName, tripNotification.userName)
        assertEquals(firestoreTripNotification.description, tripNotification.description)
        assertEquals(LocalDateTime.parse(firestoreTripNotification.timestamp, formatter), tripNotification.timestamp)
    }
}
