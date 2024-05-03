package com.github.se.wanderpals.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.data.Category
import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.google.firebase.FirebaseApp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
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
  private val usernames =
      listOf( // Initialize a trip with details for a summer vacation in Italy.
          "GrigoryVeryWeirdUsernameWhichNoOneWillEverUseElseThisTestFails123456789", // likely
          // unique
          // usernames
          "ViktorVeryWeirdUsernameWhichNoOneWillEverUseElseThisTestFails123456789") // likely unique
  // usernames)

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
            suggestions = emptyList(),
            announcements = emptyList())

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
            suggestions = emptyList(),
            announcements = emptyList())

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
            suggestions = emptyList(),
            announcements = emptyList())

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
            suggestions = emptyList(),
            announcements = emptyList())

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
            suggestions = emptyList(),
            announcements = emptyList())

    // Initialize a stop at the Colosseum with detailed information.
    val user1 =
        User(
            userId = "testUser123",
            name = "John Doe",
            email = "john.doe@example.com",
            nickname = "", // Assuming an empty nickname
            role = Role.MEMBER, // Adjusted from "Traveler" to a valid enum, assuming MEMBER as a
            // placeholder
            lastPosition = GeoCords(0.0, 0.0), // Assuming default coordinates
            profilePictureURL = "" // Assuming no profile picture URL provided
            )

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

  @Test
  fun testTripLifecycleWithSuggestions() = runBlocking {
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
            suggestions = emptyList(),
            announcements = emptyList())

    // Initialize a stop within the suggestion for the trip.
    val stop =
        Stop(
            stopId = UUID.randomUUID().toString(),
            title = "The Colosseum",
            address = "Piazza del Colosseo, 1, 00184 Roma RM, Italy",
            date = LocalDate.of(2024, 5, 21),
            startTime = LocalTime.of(10, 0),
            duration = 120,
            budget = 50.0,
            description = "Visit the iconic Roman Colosseum and learn about its history.",
            geoCords = GeoCords(latitude = 41.8902, longitude = 12.4922),
            website = "http://www.the-colosseum.net/",
            imageUrl = "https://example.com/colosseum.png")

    // Initialize a suggestion for the trip including the stop.
    val suggestion1 =
        Suggestion(
            suggestionId = "",
            userId = "",
            userName = "Alice",
            text =
                "Suggesting a visit to the Colosseum, one of the greatest architectural achievements in Rome.",
            createdAt = LocalDate.now(),
            createdAtTime = LocalTime.now(),
            stop = stop, // Embed the Stop object directly within the suggestion.
            comments =
                listOf(
                    Comment(
                        commentId = "comment123",
                        userId = "user456",
                        userName = "Bob",
                        text = "Great idea! It's a must-see.",
                        createdAt = LocalDate.now(),
                        createdAtTime = LocalTime.now())),
            userLikes = emptyList())

    val elapsedTime = measureTimeMillis {
      try {
        withTimeout(10000) {
          // Add the trip and validate the addition.
          assertTrue(repository.addTrip(trip))

          var fetchedTrip = repository.getTrip(repository.getTripsIds().first())!!
          // Add the suggestion to the trip and validate.
          assertTrue(repository.addSuggestionToTrip(tripId = fetchedTrip.tripId, suggestion1))

          // Validate that the suggestion was added successfully.
          val suggestions = repository.getAllSuggestionsFromTrip(fetchedTrip.tripId)

          assertTrue(suggestions.isNotEmpty())

          // Fetch and validate the added suggestion.
          fetchedTrip = repository.getTrip(repository.getTripsIds().first())!!

          val fetchedSuggestion =
              repository.getSuggestionFromTrip(fetchedTrip.tripId, fetchedTrip.suggestions.first())
          assertNotNull(fetchedSuggestion)
          assertEquals(
              "Suggesting a visit to the Colosseum, one of the greatest architectural achievements in Rome.",
              fetchedSuggestion?.text)

          // Update the suggestion and validate the update.
          val updatedSuggestionText = "Updated: Visit both the Colosseum and nearby Roman Forum."
          assertTrue(
              repository.updateSuggestionInTrip(
                  fetchedTrip.tripId, fetchedSuggestion!!.copy(text = updatedSuggestionText)))

          // Validate the update was successful.
          val updatedSuggestion =
              repository.getSuggestionFromTrip(fetchedTrip.tripId, fetchedTrip.suggestions.first())
          assertEquals(updatedSuggestionText, updatedSuggestion?.text)

          // Remove the suggestion from the trip and validate its removal.
          assertTrue(
              repository.removeSuggestionFromTrip(
                  fetchedTrip.tripId, fetchedTrip.suggestions.first()))

          // Validate the suggestion list is empty after deletion.
          assertTrue(repository.getAllSuggestionsFromTrip(fetchedTrip.tripId).isEmpty())

          // Cleanup: Delete the trip.
          assertTrue(repository.deleteTrip(fetchedTrip.tripId))
        }
      } catch (e: TimeoutCancellationException) {
        fail("The operation timed out after 10 seconds")
      }
    }
    println("Execution time for testTripLifecycleWithSuggestions: $elapsedTime ms")
  }

  @Test
  fun testTripLifecycleWithAnnouncements() = runBlocking {
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
            suggestions = emptyList(),
            announcements = emptyList())

    // Define a trip Announcement object.
    val announcement =
        Announcement(
            announcementId = "",
            userId = "user123",
            title = "Flight Booking Reminder",
            userName = "System",
            description = "Reminder to book your flight to Italy",
            timestamp = LocalDateTime.now())

    val elapsedTime = measureTimeMillis {
      try {
        withTimeout(10000) {
          // Add the trip and validate the addition.
          assertTrue(repository.addTrip(trip))

          var fetchedTrip = repository.getTrip(repository.getTripsIds().first())!!
          // Add a trip Announcement to the trip and validate.
          assertTrue(repository.addAnnouncementToTrip(tripId = fetchedTrip.tripId, announcement))

          // Fetch and validate the added trip Announcement.
          val announcements = repository.getAllAnnouncementsFromTrip(fetchedTrip.tripId)
          assertTrue(announcements.isNotEmpty())

          val fetchedAnnouncement = announcements.first()
          assertNotNull(fetchedAnnouncement)
          assertEquals("Reminder to book your flight to Italy", fetchedAnnouncement.description)

          // Fetch and validate the added Announcement.
          fetchedTrip = repository.getTrip(repository.getTripsIds().first())!!

          val fetchedTripAnnouncements =
              repository.getAnnouncementFromTrip(
                  fetchedTrip.tripId, fetchedTrip.announcements.first())
          assertNotNull(fetchedTripAnnouncements)
          assertEquals(
              "Reminder to book your flight to Italy", fetchedTripAnnouncements?.description)

          // Update the trip Announcement and validate the update.
          val updatedAnnouncement =
              fetchedAnnouncement.copy(description = "Updated: Confirm your hotel booking as well.")
          assertTrue(repository.updateAnnouncementInTrip(fetchedTrip.tripId, updatedAnnouncement))

          // Validate the update was successful.
          val updatedFetchedAnnouncement =
              repository.getAnnouncementFromTrip(
                  fetchedTrip.tripId, updatedAnnouncement.announcementId)
          assertNotNull(updatedFetchedAnnouncement)
          assertEquals(
              "Updated: Confirm your hotel booking as well.",
              updatedFetchedAnnouncement?.description)

          // Remove the Announcement from the trip and validate its removal.
          assertTrue(
              repository.removeAnnouncementFromTrip(
                  fetchedTrip.tripId, fetchedTrip.announcements.first()))

          // Validate the Announcement list is empty after deletion.
          assertTrue(repository.getAllAnnouncementsFromTrip(fetchedTrip.tripId).isEmpty())

          // Cleanup: Delete the trip.
          assertTrue(repository.deleteTrip(fetchedTrip.tripId))
        }
      } catch (e: TimeoutCancellationException) {
        fail("The operation timed out after 10 seconds")
      }
    }
    println("Execution time for testTripLifecycleWithAnnouncements: $elapsedTime ms")
  }

  @Test
  fun testTripLifecycleWithExpenses() = runBlocking {
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
            suggestions = emptyList(),
            announcements = emptyList())

    // Define a trip Announcement object.
    val expense =
        Expense(
            expenseId = "exp123",
            title = "Lunch Meeting",
            amount = 45.50,
            category = Category.FOOD,
            userId = "user123",
            userName = "Alice Smith",
            participantsIds = listOf("user124", "user125"),
            names = listOf("Bob Johnson", "Carol White"),
            localDate = LocalDate.of(2024, 1, 15))

    val elapsedTime = measureTimeMillis {
      try {
        withTimeout(10000) {
          // Add the trip and validate the addition.
          assertTrue(repository.addTrip(trip))

          var fetchedTrip = repository.getTrip(repository.getTripsIds().first())!!

          val expenseId = repository.addExpenseToTrip(tripId = fetchedTrip.tripId, expense)
          assertTrue(expenseId.isNotEmpty()) // Ensure expense ID is returned and not empty

          // Fetch and validate the added trip Expense.
          val expenses = repository.getAllExpensesFromTrip(fetchedTrip.tripId)
          assertTrue(expenses.isNotEmpty())

          val fetchedExpense = expenses.first()
          assertNotNull(fetchedExpense)
          assertEquals(expense.title, fetchedExpense.title)

          // Fetch and validate the added Expense.
          fetchedTrip = repository.getTrip(repository.getTripsIds().first())!!

          val fetchedTripExpense =
              repository.getExpenseFromTrip(fetchedTrip.tripId, fetchedTrip.expenses.first())
          assertNotNull(fetchedTripExpense)
          if (fetchedTripExpense != null) {
            assertEquals(expense.title, fetchedTripExpense.title)
          }

          // Update the trip Expense and validate the update.
          val updatedExpense = fetchedTripExpense?.copy(title = "New Title")!!
          assertTrue(repository.updateExpenseInTrip(fetchedTrip.tripId, updatedExpense))

          // Validate the update was successful.
          val updatedFetchedExpense =
              repository.getExpenseFromTrip(fetchedTrip.tripId, updatedExpense.expenseId)
          assertNotNull(updatedFetchedExpense)
          assertEquals("New Title", updatedFetchedExpense?.title)

          // Remove the Expense from the trip and validate its removal.
          assertTrue(
              repository.removeExpenseFromTrip(fetchedTrip.tripId, fetchedTrip.expenses.first()))

          // Validate the Expense list is empty after deletion.
          assertTrue(repository.getAllExpensesFromTrip(fetchedTrip.tripId).isEmpty())

          // Cleanup: Delete the trip.
          assertTrue(repository.deleteTrip(fetchedTrip.tripId))
        }
      } catch (e: TimeoutCancellationException) {
        fail("The operation timed out after 10 seconds")
      }
    }
    println("Execution time for testTripLifecycleWithExpenses: $elapsedTime ms")
  }

  @Test
  fun testTripLifecycleWithNotifications() = runBlocking {
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
            suggestions = emptyList(),
            announcements = emptyList())

    // Define a list of notifications for the trip.
    val notifications =
        listOf(
            TripNotification(
                title = "Flight Booking", route = "/flights", timestamp = LocalDateTime.now()),
            TripNotification(
                title = "Hotel Reservation",
                route = "/hotels",
                timestamp = LocalDateTime.now().plusDays(1)))

    val elapsedTime = measureTimeMillis {
      try {
        withTimeout(10000) {
          // Add the trip and validate the addition.
          assertTrue(repository.addTrip(trip))

          var fetchedTrip = repository.getTrip(repository.getTripsIds().first())!!

          // Set notifications for the trip and validate that they are set.
          assertTrue(repository.setNotificationList(fetchedTrip.tripId, notifications))

          // Retrieve and validate notifications.
          val retrievedNotifications = repository.getNotificationList(fetchedTrip.tripId)
          assertEquals(notifications.size, retrievedNotifications.size)
          assertTrue(
              retrievedNotifications.map { it.title }.containsAll(notifications.map { it.title }))

          assertTrue(repository.setNotificationList(fetchedTrip.tripId, emptyList()))

          fetchedTrip = repository.getTrip(repository.getTripsIds().first())!!

          // Cleanup: Delete the trip.
          assertTrue(repository.deleteTrip(fetchedTrip.tripId))
        }
      } catch (e: TimeoutCancellationException) {
        fail("The operation timed out after 10 seconds")
      }
    }
    println("Execution time for testTripLifecycleWithNotifications: $elapsedTime ms")
  }

  @Test
  fun testTripLifecycleWithBalances() = runBlocking {
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
            suggestions = emptyList(),
            announcements = emptyList())

    // Define a balance map for the trip.
    val balances = mapOf("Alice" to 150.0, "Bob" to -50.0)

    val elapsedTime = measureTimeMillis {
      try {
        withTimeout(10000) {
          // Add the trip and validate the addition.
          assertTrue(repository.addTrip(trip))

          val fetchedTripId = repository.getTripsIds().first()
          assertNotNull(fetchedTripId)

          // Set balances for the trip and validate that they are set.
          assertTrue(repository.setBalances(fetchedTripId, balances))

          // Retrieve and validate balances.
          val retrievedBalances = repository.getBalances(fetchedTripId)
          assertEquals(balances.size, retrievedBalances.size)
          assertTrue(retrievedBalances.all { balances[it.key] == it.value })

          // Clear the balances and validate clearance.
          assertTrue(repository.setBalances(fetchedTripId, emptyMap()))

          val retrievedBalancesAfterClearance = repository.getBalances(fetchedTripId)
          assertTrue(retrievedBalancesAfterClearance.isEmpty())

          // Cleanup: Delete the trip.
          assertTrue(repository.deleteTrip(fetchedTripId))
        }
      } catch (e: TimeoutCancellationException) {
        fail("The operation timed out after 10 seconds")
      }
    }
    println("Execution time for testTripLifecycleWithNotifications: $elapsedTime ms")
  }

  @Test
  fun testAddAndGetEmailWithUsername() = runBlocking {
    val emails = listOf("gregory1@example.com", "viktor@example.com")
    // usernames list of string of 2 usernames
    val elapsedTime = measureTimeMillis {
      try {
        withTimeout(10000) {
          // Add the usernames and emails.
          assertTrue(repository.addEmailToUsername(usernames[0], emails[0]))
          assertTrue(repository.addEmailToUsername(usernames[1], emails[1]))

          // Retrieve and verify emails.
          assertEquals(emails[0], repository.getUserEmail(usernames[0]))
          assertEquals(emails[1], repository.getUserEmail(usernames[1]))

          // Attempt to add one of the usernames again.
          assertFalse(repository.addEmailToUsername(usernames[0], "newemail@example.com"))

          // Delete a non-existent username.
          assertTrue(repository.deleteEmailByUsername("nonExistentUser"))

          // Delete an existing username.
          assertTrue(repository.deleteEmailByUsername(usernames[1]))
          assertNull(repository.getUserEmail(usernames[1]))
          assertTrue(repository.deleteEmailByUsername(usernames[0]))
        }
      } catch (e: TimeoutCancellationException) {
        fail("The operation timed out after 10 seconds")
      }
    }
    println("Execution time for testAddAndGetEmailWithUsername: $elapsedTime ms")
  }

  @After
  fun tearDown() = runBlocking {
    // for debugging
    // true false
    val debug = false

    // Attempt to retrieve all trip IDs that might have been added during tests
    val tripIds =
        try {
          repository.getTripsIds()
        } catch (e: Exception) {
          emptyList<String>() // In case of error, fallback to an empty list to avoid null issues
        }

    if (!debug) {
      usernames.forEach { username -> repository.deleteEmailByUsername(username) }
    }

    // Loop through each trip ID and attempt to remove it for cleanup
    tripIds.forEach { tripId ->
      try {
        val trip = repository.getTrip(tripId)

        if (trip != null && !debug) {

          repository.setNotificationList(tripId, emptyList())

          val stopIds = trip.stops
          stopIds.forEach { stopId ->
            repository.removeStopFromTrip(tripId, stopId)
          } // delete all stops

          val userIds = trip.users
          userIds.forEach { userId -> repository.removeUserFromTrip(tripId, userId) }

          val suggestionIds = trip.suggestions
          suggestionIds.forEach { suggestionId ->
            repository.removeSuggestionFromTrip(tripId, suggestionId)
          }

          val announcementIds = trip.announcements
          announcementIds.forEach { announcementId ->
            repository.removeAnnouncementFromTrip(tripId, announcementId)
          }

          val expensesIds = trip.expenses
          expensesIds.forEach { expensesId -> repository.removeExpenseFromTrip(tripId, expensesId) }

          repository.setBalances(tripId, emptyMap())

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
