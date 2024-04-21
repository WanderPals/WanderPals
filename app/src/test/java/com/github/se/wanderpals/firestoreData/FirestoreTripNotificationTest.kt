package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.Announcement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import junit.framework.TestCase.assertEquals
import org.junit.Test

class AnnouncementTest {

  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  @Test
  fun testFromAnnouncementToFirestoreAnnouncement() {
    val announcement =
        Announcement(
            announcementId = "notif123",
            userId = "user456",
            title = "Trip Alert",
            userName = "Jane Doe",
            description = "Your trip to Paris is coming up soon!",
            timestamp = LocalDateTime.parse("2024-04-01T12:30:00", formatter))

    val firestoreAnnouncement = FirestoreAnnouncement.fromAnnouncement(announcement)

    assertEquals(announcement.announcementId, firestoreAnnouncement.announcementId)
    assertEquals(announcement.userId, firestoreAnnouncement.userId)
    assertEquals(announcement.title, firestoreAnnouncement.title)
    assertEquals(announcement.userName, firestoreAnnouncement.userName)
    assertEquals(announcement.description, firestoreAnnouncement.description)
    assertEquals(announcement.timestamp.format(formatter), firestoreAnnouncement.timestamp)
  }

  @Test
  fun testFromFirestoreAnnouncementToAnnouncement() {
    val firestoreAnnouncement =
        FirestoreAnnouncement(
            announcementId = "notif123",
            userId = "user456",
            title = "Trip Alert",
            userName = "Jane Doe",
            description = "Your trip to Paris is coming up soon!",
            timestamp = "2024-04-01T12:30:00")

    val announcement = firestoreAnnouncement.toAnnouncement()

    assertEquals(firestoreAnnouncement.announcementId, announcement.announcementId)
    assertEquals(firestoreAnnouncement.userId, announcement.userId)
    assertEquals(firestoreAnnouncement.title, announcement.title)
    assertEquals(firestoreAnnouncement.userName, announcement.userName)
    assertEquals(firestoreAnnouncement.description, announcement.description)
    assertEquals(
        LocalDateTime.parse(firestoreAnnouncement.timestamp, formatter),
        announcement.timestamp)
  }
}
