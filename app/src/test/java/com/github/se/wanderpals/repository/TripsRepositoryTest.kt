package com.github.se.wanderpals.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.repository.TripsRepository
import com.google.firebase.FirebaseApp
import java.time.LocalDate
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TripsRepositoryTest {

  private val context = ApplicationProvider.getApplicationContext<Context>()

  private lateinit var repository: TripsRepository
  private val testUid = "testUser123"
  private val testTripId = "testTrip123"

  @Before
  fun setUp() {

    repository = TripsRepository(testUid, dispatcher = Dispatchers.IO)
    val app = FirebaseApp.initializeApp(context)!!
    repository.initFirestore(app)
  }

  @Test
  fun testAddAndGetAndRemoveTripId() = runBlocking {
    val elapsedTime = measureTimeMillis {
      try {
        withTimeout(5000) {
          assertTrue(repository.addTripId(testTripId))
          val tripIds = repository.getTripsIds()
          assertTrue(tripIds.contains(testTripId))
        }

        withTimeout(5000) {
          assertTrue(repository.removeTripId(testTripId))
          val tripIds = repository.getTripsIds()
          assertTrue(!tripIds.contains(testTripId))
        }
      } catch (e: TimeoutCancellationException) {
        // If a timeout occurs, fail the test
        fail("The operation timed out after 5 seconds")
      }
    }
    println("testAddAndGetAndRemoveTripId execution time: $elapsedTime ms")
  }

  @Test
  fun testAddAndGetAndRemoveAndModifyAndGetAllTrip() = runBlocking {
    val trip =
        Trip(
            tripId = "trip123",
            title = "Summer Vacation",
            startDate = LocalDate.of(2024, 5, 20), // Assuming format is YYYY, MM, DD
            endDate = LocalDate.of(2024, 6, 10),
            totalBudget = 2000.0,
            description = "Our summer vacation trip to Italy.",
            imageUrl = "https://example.com/image.png",
            stops = emptyList(),
            users = emptyList(),
            suggestions = emptyList())

    val trip2 =
        Trip(
            tripId = "trip123",
            title = "Summer Vacation",
            startDate = LocalDate.of(2024, 5, 20), // Assuming format is YYYY, MM, DD
            endDate = LocalDate.of(2024, 6, 10),
            totalBudget = 2000.0,
            description = "Our summer vacation trip to Italy.",
            imageUrl = "https://example.com/image.png",
            stops = emptyList(),
            users = emptyList(),
            suggestions = emptyList())

    val updatedTrip2 =
        Trip(
            tripId = "Not a trip at all",
            title = "Winter fun",
            startDate = LocalDate.of(2028, 5, 20), // Assuming format is YYYY, MM, DD
            endDate = LocalDate.of(2029, 6, 10),
            totalBudget = 20000.0,
            description = "TO MARS.",
            imageUrl = "https://example.com/image.png))))",
            stops = emptyList(),
            users = emptyList(),
            suggestions = emptyList())

    val elapsedTime = measureTimeMillis {
      try {
        withTimeout(10000) {
          assertTrue(repository.addTrip(trip))
          assertTrue(repository.addTrip(trip2))

          val tripIds = repository.getTripsIds()
          val tripId = tripIds[0]

          val getTrip = repository.getTrip(tripId)
          if (getTrip != null) {
            assertTrue(getTrip.tripId == tripId)
          }
          val getTrips = repository.getAllTrips(repository.getTripsIds())
          assertTrue(getTrips.size == 2)
        }

        withTimeout(10000) {
          val tripIds = repository.getTripsIds()
          val tripId = tripIds[0]
          val tripId2 = tripIds[1]

          assertTrue(repository.updateTrip(updatedTrip2.copy(tripId = tripId2)))

          val getTrip = repository.getTrip(tripId)
          if (getTrip != null) {
            assertTrue(getTrip.title == trip2.title)
            assertTrue(getTrip.description == trip2.description)
            assertTrue(getTrip.totalBudget == trip2.totalBudget)
          }
        }

        withTimeout(10000) {
          val tripIds = repository.getTripsIds()
          val tripId = tripIds[0]
          val tripId2 = tripIds[1]
          assertTrue(repository.deleteTrip(tripId))
          assertTrue(repository.deleteTrip(tripId2))
        }
      } catch (e: TimeoutCancellationException) {
        // If a timeout occurs, fail the test
        fail("The operation timed out after 10 seconds")
      }
    }
    println("testAddAndGetAndRemoveAndModifyAndGetAllTrip execution time: $elapsedTime ms")
  }

  @After
  fun tearDown() = runBlocking {
    // Attempt to retrieve all trip IDs that might have been added during tests
    val tripIds =
        try {
          repository.getTripsIds()
        } catch (e: Exception) {
          emptyList<String>() // In case of error, fallback to an empty list to avoid null issues
        }

    // Loop through each trip ID and attempt to remove it for cleanup
    tripIds.forEach { tripId ->
      try {
        repository.deleteTrip(tripId)
        repository.removeTripId(tripId)
      } catch (e: Exception) {
        println("Error cleaning up trip ID: $tripId")
      }
    }

    // Add any other cleanup logic here if necessary
  }
}
