package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.ActivityLog
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class FirestoreActivityLogTest {

  @Test
  fun testFromActivityLogConversion() {
    val activityLog =
        ActivityLog(
            logId = "log123",
            userId = "user456",
            userName = "John Doe",
            action = "created_trip",
            entityId = "trip789",
            entityType = "trip",
            description = "Created a trip to Paris",
            createdAt = LocalDate.of(2024, 5, 1))

    val firestoreActivityLog = FirestoreActivityLog.fromActivityLog(activityLog)
    assertEquals("2024-05-01", firestoreActivityLog.createdAt)
    assertEquals("log123", firestoreActivityLog.logId)
    assertEquals("user456", firestoreActivityLog.userId)
    assertEquals("created_trip", firestoreActivityLog.action)
    assertEquals("trip789", firestoreActivityLog.entityId)
    assertEquals("trip", firestoreActivityLog.entityType)
    assertEquals("Created a trip to Paris", firestoreActivityLog.description)
  }

  @Test
  fun testToActivityLogConversion() {
    val firestoreActivityLog =
        FirestoreActivityLog(
            logId = "log123",
            userId = "user456",
            userName = "John Doe",
            action = "created_trip",
            entityId = "trip789",
            entityType = "trip",
            description = "Created a trip to Paris",
            createdAt = "2024-05-01")

    val activityLog = firestoreActivityLog.toActivityLog()
    assertEquals(LocalDate.of(2024, 5, 1), activityLog.createdAt)
    assertEquals("log123", activityLog.logId)
    assertEquals("user456", activityLog.userId)
    assertEquals("created_trip", activityLog.action)
    assertEquals("trip789", activityLog.entityId)
    assertEquals("trip", activityLog.entityType)
    assertEquals("Created a trip to Paris", activityLog.description)
  }
}
