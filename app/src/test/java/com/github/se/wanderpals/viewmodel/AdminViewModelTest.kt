package com.github.se.wanderpals.viewmodel

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.AdminViewModel
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.service.SessionUser
import com.google.firebase.FirebaseApp
import io.mockk.coEvery
import io.mockk.mockk
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
class AdminViewModelTest {

  private val context = ApplicationProvider.getApplicationContext<Context>()
  private lateinit var viewModel: AdminViewModel
  private lateinit var mockTripsRepository: TripsRepository
  private val testDispatcher = StandardTestDispatcher()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    // Set the main dispatcher to a test dispatcher to control coroutine execution
    Dispatchers.setMain(testDispatcher)
    SessionManager.setUserSession(userId = "testUser333")
    FirebaseApp.initializeApp(context)
    // Mock the TripsRepository to be used in the ViewModel
    mockTripsRepository = mockk(relaxed = true)
    setupMockResponses()

    // Create the ViewModel using a factory with the mocked repository
    val factory = AdminViewModel.AdminViewModelFactory("tripId", mockTripsRepository)
    viewModel = factory.create(AdminViewModel::class.java)
  }

  private fun setupMockResponses() {
    // Define responses from the mocked repository to be returned when methods are called
    coEvery { mockTripsRepository.getAllUsersFromTrip(any()) } returns
        listOf(User(userId = "testUser333"))
    coEvery { mockTripsRepository.updateUserInTrip(any(), any()) } returns true
    coEvery { mockTripsRepository.removeUserFromTrip(any(), any()) } returns true
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testModifyCurrentUserRole() =
      runBlockingTest(testDispatcher) {
        // Given
        viewModel.currentUser.value = SessionUser(userId = "currentUser", role = Role.MEMBER)
        viewModel.listOfUsers.value = listOf(User(userId = "currentUser", role = Role.MEMBER))

        // When
        viewModel.modifyCurrentUserRole(Role.ADMIN)

        // Then
        assertEquals(Role.ADMIN, viewModel.currentUser.value?.role)
        assertEquals(
            Role.ADMIN, viewModel.listOfUsers.value.find { it.userId == "currentUser" }?.role)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testModifyCurrentUserProfilePhoto() =
      runBlockingTest(testDispatcher) {
        SessionManager.setIsNetworkAvailable(true)
        // Given
        viewModel.currentUser.value =
            SessionUser(userId = "currentUser", role = Role.MEMBER, profilePhoto = "oldUrl")
        viewModel.listOfUsers.value =
            listOf(User(userId = "currentUser", profilePictureURL = "oldUrl"))

        // When
        viewModel.modifyCurrentUserProfilePhoto("newUrl")

        // Then
        assertEquals("newUrl", viewModel.currentUser.value?.profilePhoto)
        assertEquals(
            "newUrl",
            viewModel.listOfUsers.value.find { it.userId == "currentUser" }?.profilePictureURL)
        SessionManager.setIsNetworkAvailable(true)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testModifyCurrentUserProfilePhotoOffline() =
      runBlockingTest(testDispatcher) {
        SessionManager.setIsNetworkAvailable(false)
        // Given
        viewModel.currentUser.value =
            SessionUser(userId = "currentUser", role = Role.MEMBER, profilePhoto = "oldUrl")
        viewModel.listOfUsers.value =
            listOf(User(userId = "currentUser", profilePictureURL = "oldUrl"))

        // When
        viewModel.modifyCurrentUserProfilePhoto("newUrl")

        // Then
        assertEquals("oldUrl", viewModel.currentUser.value?.profilePhoto)
        assertEquals(
            "oldUrl",
            viewModel.listOfUsers.value.find { it.userId == "currentUser" }?.profilePictureURL)
        SessionManager.setIsNetworkAvailable(true)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testGetUsers() =
      runBlockingTest(testDispatcher) {
        // When
        viewModel.getUsers()
        advanceUntilIdle()

        // Then
        assertEquals(listOf(User(userId = "testUser333")), viewModel.listOfUsers.value)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testDeleteUser() =
      runBlockingTest(testDispatcher) {
        // Setup initial users
        viewModel.listOfUsers.value =
            listOf(User(userId = "testUser333"), User(userId = "testUser444"))

        // When
        viewModel.deleteUser("testUser333")

        // Then
        assertEquals(1, viewModel.listOfUsers.value.size)
        assertEquals("testUser444", viewModel.listOfUsers.value[0].userId)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testModifyUser() =
      runBlockingTest(testDispatcher) {
        // Given
        val originalUser = User(userId = "testUser333", profilePictureURL = "oldUrl")
        val updatedUser = User(userId = "testUser333", profilePictureURL = "newUrl")
        viewModel.listOfUsers.value = listOf(originalUser)

        // When
        viewModel.modifyUser(updatedUser)

        // Then
        assertEquals("newUrl", viewModel.listOfUsers.value[0].profilePictureURL)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    Dispatchers.resetMain() // Reset the main dispatcher to the original Main dispatcher
  }
}
