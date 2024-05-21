package com.github.se.wanderpals.viewmodel

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.MapViewModel
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.service.SharedPreferencesManager
import com.google.android.gms.maps.model.LatLng
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MapViewModelTest {

  private val context = ApplicationProvider.getApplicationContext<Context>()

  private lateinit var viewModel: MapViewModel
  private lateinit var mockTripsRepository: TripsRepository
  private val testDispatcher = StandardTestDispatcher()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    // Set the main dispatcher to a test dispatcher to control coroutine execution
    Dispatchers.setMain(testDispatcher)
    mockkObject(SharedPreferencesManager)
    SharedPreferencesManager.init(context)

    every { SharedPreferencesManager.clearAll() } just Runs
    every { SharedPreferencesManager.savePlaceData(any()) } returns
        listOf(GeoCords(placeId = "someplaceId")).toMutableList()
    every { SharedPreferencesManager.deletePlaceData(any()) } returns
        listOf<GeoCords>().toMutableList()

    SessionManager.setUserSession(userId = "user1")
    // Mock the TripsRepository to be used in the ViewModel
    mockTripsRepository = mockk(relaxed = true)
    setupMockResponses()

    // Create the ViewModel using a factory with the mocked repository
    val factory = MapViewModel.MapViewModelFactory(mockTripsRepository, "tripId")
    viewModel = factory.create(MapViewModel::class.java)
  }

  private fun setupMockResponses() {
    // Define responses from the mocked repository to be returned when methods are called
    coEvery { mockTripsRepository.getAllStopsFromTrip("tripId") } returns
        listOf(
            Stop(
                "stop1",
                "Central Park",
                "123 Park Ave",
                LocalDate.now(),
                LocalTime.NOON,
                60,
                0.0,
                "Nice place",
                GeoCords(40.785091, -73.968285),
                "https://example.com",
                "https://image.url"))
    coEvery { mockTripsRepository.getAllSuggestionsFromTrip("tripId") } returns
        listOf(
            Suggestion(
                "suggestion1",
                "user1",
                "Alice",
                "Check this out!",
                LocalDate.now(),
                LocalTime.now(),
                Stop(
                    "stop1",
                    "Statue of Liberty",
                    "Liberty Island",
                    LocalDate.now(),
                    LocalTime.MIDNIGHT,
                    120,
                    25.0,
                    "Must see",
                    GeoCords(40.689247, -74.044502),
                    "https://liberty.com",
                    "https://liberty.img"),
                emptyList(),
                emptyList()))
    coEvery { mockTripsRepository.getAllUsersFromTrip("tripId") } returns
        listOf(
            User(
                "user1",
                "Alice",
                "alice@example.com",
                "AliceInNY",
                Role.MEMBER,
                GeoCords(40.712776, -74.005974),
                "https://alice.img",
                "token123"))

    coEvery { mockTripsRepository.getUserFromTrip(any(), any()) } returns User(name = "user")

    coEvery { mockTripsRepository.updateUserInTrip(any(), any()) } returns true
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `getAllUsersPositions fetches and updates LiveData correctly`() =
      runBlockingTest(testDispatcher) {
        SessionManager.setUserSession("userNew")
        viewModel.refreshData()

        advanceUntilIdle()
        assertEquals(1, viewModel.usersPositions.value.size)
        assertEquals(40.712776, viewModel.usersPositions.value.first().latitude, 0.001)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `getAllStops fetches and updates LiveData correctly`() =
      runBlockingTest(testDispatcher) {
        viewModel.refreshData()

        advanceUntilIdle()
        assertEquals(1, viewModel.stops.value.size)
        assertEquals("Central Park", viewModel.stops.value.first().title)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `getAllSuggestions fetches and updates LiveData correctly`() =
      runBlockingTest(testDispatcher) {
        viewModel.refreshData()

        advanceUntilIdle()
        assertEquals(1, viewModel.suggestions.value.size)
        assertEquals("Statue of Liberty", viewModel.suggestions.value[0].title)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `updateLastPosition updates repository and LiveData`() =
      runBlockingTest(testDispatcher) {
        val newLatLng = LatLng(40.730610, -73.935242)
        viewModel.updateLastPosition(newLatLng)

        advanceUntilIdle()
        coVerify { mockTripsRepository.updateUserInTrip("tripId", any()) }
        assertEquals(newLatLng, viewModel.userPosition.value)
      }

  @Test
  fun `clearAllSharedPreferences clears all data`() = runBlockingTest {
    viewModel.clearAllSharedPreferences()

    verify { SharedPreferencesManager.clearAll() }
    assertEquals(emptyList<GeoCords>(), viewModel.listOfTempPlaceData.value)
  }

  @Test
  fun `savePlaceDataState saves place data and updates LiveData`() = runBlockingTest {
    val placeData = GeoCords(placeId = "someplaceId")

    viewModel.savePlaceDataState(placeData)

    verify { SharedPreferencesManager.savePlaceData(placeData) }
    assertEquals(listOf(placeData), viewModel.listOfTempPlaceData.value)
  }

  @Test
  fun `deletePlaceDataState deletes place data and updates LiveData`() = runBlockingTest {
    val placeData = GeoCords(placeId = "Some place")

    viewModel.deletePlaceDataState(placeData)

    verify { SharedPreferencesManager.deletePlaceData(placeData) }
    assertEquals(emptyList<GeoCords>(), viewModel.listOfTempPlaceData.value)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    Dispatchers.resetMain() // Reset the main dispatcher to the original Main dispatcher
  }
}
