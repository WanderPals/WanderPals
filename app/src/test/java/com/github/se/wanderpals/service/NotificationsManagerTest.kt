package com.github.se.wanderpals.service

import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.ui.navigation.Route
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NotificationsManagerTest {

  private val notification1 =
      TripNotification(
          title = "username joined the trip",
          route = Route.ADMIN_PAGE,
          timestamp = LocalDateTime.of(2021, 1, 1, 0, 0, 0))

  private val notification2 =
      TripNotification(
          title = "A new suggestion has been created",
          route = "",
          timestamp = LocalDateTime.of(2021, 1, 1, 0, 0, 0))

  private val notification3 =
      TripNotification(
          title = "A new stop has been added",
          route = Route.STOPS_LIST,
          timestamp = LocalDateTime.of(2021, 1, 1, 0, 0, 0))

  private val notification4 =
      TripNotification(
          title = "A new suggestion has been created",
          route = Route.SUGGESTION_DETAIL,
          timestamp = LocalDateTime.of(2021, 1, 1, 0, 0, 0),
          "currentTrip: 1 |latitude: 0.0|longitude: 0.0|currentAddress: |suggestionId: suggestionId|expenseId: 0")

  private val notification5 =
      TripNotification(
          title = "A new Expense has been created",
          route = Route.EXPENSE_INFO,
          timestamp = LocalDateTime.of(2021, 1, 1, 0, 0, 0),
          "currentTrip: 1 |latitude: 0.0|longitude: 0.0|currentAddress: |suggestionId: |expenseId: expenseId")

  private val notification6 =
      TripNotification(
          title = "user wants to meet at Pizza My Heart",
          route = Route.MAP,
          timestamp = LocalDateTime.of(2021, 1, 1, 0, 0, 0),
          "currentTrip: 1 |latitude: 37.7088766|longitude: -121.9302073|currentAddress: 7281 Amador Plaza Rd, Dublin, CA 94568, USA|suggestionId: |expenseId: 1")

  private lateinit var mockTripsRepository: TripsRepository
  private val testDispatcher = StandardTestDispatcher()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher)

    navigationActions = mockk(relaxed = true)

    SessionManager.setUserSession(
        userId = "userId",
        name = "username",
    )

    mockTripsRepository = mockk(relaxed = true)
    NotificationsManager.initNotificationsManager(mockTripsRepository)

    coEvery { mockTripsRepository.getNotificationList(any()) } returns
        listOf(
            notification1,
            notification2,
            notification3,
            notification4,
            notification5,
            notification6)
    coEvery { mockTripsRepository.setNotificationList(any(), any()) } returns true
    coEvery { mockTripsRepository.getTrip(any()) } returns null
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun removeSuggestionPath() =
      runBlockingTest(testDispatcher) {
        val tripId = "tripId"
        val suggestionId = "suggestionId"
        val notification4_copy = notification4.copy(route = "", navActionVariables = "")

        NotificationsManager.removeSuggestionPath(tripId, suggestionId)
        advanceUntilIdle()
        coVerify {
          mockTripsRepository.setNotificationList(
              tripId,
              listOf(
                  notification1,
                  notification2,
                  notification3,
                  notification4_copy,
                  notification5,
                  notification6))
        }
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun removeExpensePath() =
      runBlockingTest(testDispatcher) {
        val tripId = "tripId"
        val expenseId = "expenseId"
        val notification5_copy = notification5.copy(route = "", navActionVariables = "")

        NotificationsManager.removeExpensePath(tripId, expenseId)
        advanceUntilIdle()
        coVerify {
          mockTripsRepository.setNotificationList(
              tripId,
              listOf(
                  notification1,
                  notification2,
                  notification3,
                  notification4,
                  notification5_copy,
                  notification6))
        }
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun addJoinTripNotification() =
      runBlockingTest(testDispatcher) {
        val tripId = "tripId"
        val newNotif =
            TripNotification("username joined the trip ", Route.ADMIN_PAGE, LocalDateTime.now(), "")

        NotificationsManager.addJoinTripNotification(tripId)
        advanceUntilIdle()
        coVerify { mockTripsRepository.setNotificationList(tripId, any()) }
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun addCreateSuggestionNotification() =
      runBlockingTest(testDispatcher) {
        val tripId = "tripId"
        val suggestionId = "suggestionId"

        NotificationsManager.addCreateSuggestionNotification(tripId, suggestionId)
        advanceUntilIdle()
        coVerify { mockTripsRepository.setNotificationList(tripId, any()) }
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun addStopNotification() =
      runBlockingTest(testDispatcher) {
        val tripId = "tripId"
        val stop = Stop("stopId", "stopName")

        NotificationsManager.addStopNotification(tripId, stop)
        advanceUntilIdle()
        coVerify { mockTripsRepository.setNotificationList(tripId, any()) }
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun addMeetingStopNotification() =
      runBlockingTest(testDispatcher) {
        val tripId = "tripId"
        val stop = Stop("stopId", "Pizza My Heart")

        NotificationsManager.addMeetingStopNotification(tripId, stop)
        advanceUntilIdle()
        coVerify { mockTripsRepository.setNotificationList(tripId, any()) }
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun addExpenseNotification() =
      runBlockingTest(testDispatcher) {
        val tripId = "tripId"
        val expense = Expense("expenseId", "expenseName")

        NotificationsManager.addExpenseNotification(tripId, expense)
        advanceUntilIdle()
        coVerify { mockTripsRepository.setNotificationList(tripId, any()) }
      }
}
