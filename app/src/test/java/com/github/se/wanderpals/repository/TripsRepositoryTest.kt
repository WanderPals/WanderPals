package com.github.se.wanderpals.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.google.firebase.FirebaseApp
import java.time.LocalDate
import java.time.LocalTime
import junit.framework.TestCase.assertFalse
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
  fun testAddAndGetAndRemoveTripIdFalseIds() = runBlocking {
    val elapsedTime = measureTimeMillis {
      try {
        val nonExistentTripId = "ImInvalidTripId"

        withTimeout(10000) {
          assertFalse(repository.addTripId(nonExistentTripId))
          assertFalse(repository.removeTripId(nonExistentTripId))
          assertTrue(repository.getTripsIds().isEmpty())
        }
      } catch (e: TimeoutCancellationException) {
        // If a timeout occurs, fail the test
        fail("The operation timed out after 10 seconds")
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
          val getTrips = repository.getAllTrips()
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

  @Test
  fun testTripLifecycleWithStops() = runBlocking {
    // Initialize a trip with details for a summer vacation in Italy.
    val trip =
        Trip(
            tripId = "trip123",
            title = "Summer Vacation",
            startDate = LocalDate.of(2024, 5, 20),
            endDate = LocalDate.of(2024, 6, 10),
            totalBudget = 2000.0,
            description = "Our summer vacation trip to Italy.",
            imageUrl = "https://example.com/image.png",
            stops = emptyList(),
            users = emptyList(),
            suggestions = emptyList())

    // Initialize a stop at the Colosseum with detailed information.
    val colosseumStop =
        Stop(
            stopId = "", // ID to be assigned by addStopToTrip method
            title = "Colosseum",
            address = "Piazza del Colosseo, 1, 00184 Roma RM, Italy",
            date = LocalDate.of(2024, 5, 21),
            startTime = LocalTime.of(10, 0),
            duration = 120,
            budget = 0.0,
            description = "Visit the ancient Roman gladiatorial arena.",
            geoCords = GeoCords(41.8902, 12.4922),
            website = "https://example.com/colosseum",
            imageUrl = "https://example.com/colosseum.png")

    val elapsedTime = measureTimeMillis {
      try {
        withTimeout(10000) {
          // Add the trip and validate the addition.
          assertTrue(repository.addTrip(trip))

          // Retrieve the trip ID, assuming this is the first and only trip.
          val tripId = repository.getTripsIds()[0]

          // Add the Colosseum stop to the trip and validate.
          assertTrue(repository.addStopToTrip(tripId, colosseumStop))

          val fetchedTrip = repository.getTrip(tripId)
          assertTrue(fetchedTrip != null)
          if (fetchedTrip != null) {
            val stopId = fetchedTrip.stops[0]

            // Fetch and validate the stop's details.
            val fetchedStop = repository.getStopFromTrip(fetchedTrip.tripId, stopId)
            assertTrue(fetchedStop != null)
            if (fetchedStop != null) {

              assertTrue(fetchedStop.description == colosseumStop.description)

              // Update the stop's description, validate the update.
              repository.updateStopInTrip(
                  fetchedTrip.tripId, fetchedStop.copy(description = "NEW DESCRIPTION"))
              val updatedStop = repository.getStopFromTrip(fetchedTrip.tripId, stopId)
              assertTrue(updatedStop != null)
              if (updatedStop != null) {
                assertTrue(updatedStop.description == "NEW DESCRIPTION")
              }
            }

            // Remove the stop from the trip and validate its removal.
            assertTrue(
                "Failed to delete stop from trip.", repository.removeStopFromTrip(tripId, stopId))

            // Ensure the stop list is empty after deletion.
            val finalStopList = repository.getAllStopsFromTrip(tripId)
            assertTrue("Stop list should be empty after deletion.", finalStopList.isEmpty())

            assertTrue(repository.deleteTrip(tripId))
          }
        }
      } catch (e: TimeoutCancellationException) {
        // Handle operation timeout.
        fail("The operation timed out after 10 seconds")
      }
    }
    println("Execution time for testTripLifecycleWithStops: $elapsedTime ms")
  }

  @Test
  fun testTripLifecycleWithUsers() = runBlocking {
    // Initialize a trip with details for a summer vacation in Italy.
    val trip =
        Trip(
            tripId = "trip123",
            title = "Summer Vacation",
            startDate = LocalDate.of(2024, 5, 20),
            endDate = LocalDate.of(2024, 6, 10),
            totalBudget = 2000.0,
            description = "Our summer vacation trip to Italy.",
            imageUrl = "https://example.com/image.png",
            stops = emptyList(),
            users = emptyList(),
            suggestions = emptyList())

    // Initialize a stop at the Colosseum with detailed information.
    val user1 =
        User(
            userId = "user1234",
            name = "John Doe",
            email = "john.doe@example.com",
            role = "Traveler",
            permissions = listOf("view", "edit"))

    val elapsedTime = measureTimeMillis {
      try {
        withTimeout(10000) {
          // Add the trip and validate the addition.
          assertTrue(repository.addTrip(trip))

          // Retrieve the trip ID, assuming this is the first and only trip.
          val tripId = repository.getTripsIds().first()

          // Add the user1 User to the trip and validate.
          assertTrue(repository.addUserToTrip(tripId, user1))

          val fetchedTrip = repository.getTrip(tripId)
          assertTrue(fetchedTrip != null)
          if (fetchedTrip != null) {
            val userId = fetchedTrip.users[fetchedTrip.users.indexOf(user1.userId)]

            // Fetch and validate the stop's details.
            val fetchedUser = repository.getUserFromTrip(fetchedTrip.tripId, userId)

            val fetchedUsers = repository.getAllUsersFromTrip(fetchedTrip.tripId)
            assertTrue(fetchedUsers.isNotEmpty())
            assertTrue(fetchedUser != null)
            if (fetchedUser != null) {

              assertTrue(fetchedUser.email == user1.email)

              // Update the stop's description, validate the update.
              repository.updateUserInTrip(fetchedTrip.tripId, fetchedUser.copy(name = "Johan"))
              val updatedUser = repository.getUserFromTrip(fetchedTrip.tripId, userId)
              assertTrue(updatedUser != null)
              if (updatedUser != null) {
                assertTrue(updatedUser.name == "Johan")
              }
            }

            // Remove the stop from the trip and validate its removal.
            // Remove the user from the trip and validate its removal.
            assertTrue(repository.removeUserFromTrip(tripId, user1.userId))

            // Fetch and ensure the user list does not contain the removed user.
            val finalUserList = repository.getTrip(tripId)?.users ?: emptyList()
            assertFalse(finalUserList.contains(user1.userId))
            assertTrue(repository.deleteTrip(tripId))
          }
        }
      } catch (e: TimeoutCancellationException) {
        // Handle operation timeout.
        fail("The operation timed out after 10 seconds")
      }
    }
    println("Execution time for testTripLifecycleWithUsers: $elapsedTime ms")
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
        val trip = repository.getTrip(tripId)
        if (trip != null) {
          val stopIds = trip.stops

          stopIds.forEach { stopId ->
            repository.removeStopFromTrip(tripId, stopId)
          } // delete all stops

          val userIds = trip.users
          userIds.forEach { userId -> repository.removeUserFromTrip(tripId, userId) }
          repository.deleteTrip(tripId)
          repository.removeTripId(tripId)
        }
      } catch (e: Exception) {
        println("Error cleaning up trip ID: $tripId")
      }
    }

    // Add any other cleanup logic here if necessary
  }
}
