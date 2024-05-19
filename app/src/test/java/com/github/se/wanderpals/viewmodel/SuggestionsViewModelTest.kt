package com.github.se.wanderpals.viewmodel

import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.service.NotificationsManager
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.screens.trip.agenda.CalendarUiState
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class SuggestionsViewModelTest {
  private lateinit var viewModel: SuggestionsViewModel
  private lateinit var mockTripsRepository: TripsRepository
  private val testDispatcher = StandardTestDispatcher()
  private val tripId = "tripId"

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    // Set the Coroutine dispatcher for main thread to the test dispatcher
    Dispatchers.setMain(testDispatcher)

    // Initialize the mock for navigation actions and session manager
    navigationActions = mockk(relaxed = true)
    every { navigationActions.goBack() } just Runs

    // Setup Mock repository and session manager
    mockTripsRepository = mockk(relaxed = true)
    SessionManager.setUserSession("user", "user@example.com", "token", Role.MEMBER)

    // Mock NotificationsManager and its interactions
    NotificationsManager.initNotificationsManager(mockTripsRepository)
    mockkObject(NotificationsManager)
    coEvery { NotificationsManager.addJoinTripNotification(any()) } returns Unit
    coEvery { NotificationsManager.addStopNotification(any(), any()) } returns Unit
    coEvery { NotificationsManager.removeSuggestionPath(any(), any()) } returns Unit
    coEvery { NotificationsManager.addCreateSuggestionNotification(any(), any()) } returns Unit

    // Initialize the ViewModel with the factory pattern
    val factory = SuggestionsViewModel.SuggestionsViewModelFactory(mockTripsRepository, tripId)
    viewModel = factory.create(SuggestionsViewModel::class.java)

    // Set up mock responses for repository interactions
    setupMockResponses()
  }

  private fun setupMockResponses() {

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
            suggestionId = "suggestionId",
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
    // Define responses from the mocked repository to be returned when methods are called
    coEvery { mockTripsRepository.getAllSuggestionsFromTrip(any()) } returns listOf(suggestion1)
    coEvery { mockTripsRepository.getAllUsersFromTrip(any()) } returns listOf(user1)
    coEvery { mockTripsRepository.getSuggestionFromTrip(any(), any()) } returns suggestion1
    coEvery { mockTripsRepository.updateSuggestionInTrip(tripId, any()) } returns true

    coEvery { mockTripsRepository.addStopToTrip(any(), any()) } returns true
    coEvery { mockTripsRepository.removeSuggestionFromTrip(any(), any()) } returns true
  }

  // Example test for loading suggestions
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testLoadSuggestions() =
      runBlockingTest(testDispatcher) {
        viewModel.loadSuggestion(tripId)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertEquals(1, viewModel.state.value.size)
        assertEquals("The Colosseum", viewModel.state.value.first().stop.title)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testAddComment() =
      runBlockingTest(testDispatcher) {
        // Load existing suggestions to set initial state
        viewModel.loadSuggestion(tripId)
        advanceUntilIdle()

        // Capture the state before adding the new comment
        val suggestion = viewModel.state.value.first()
        val newComment =
            Comment(
                commentId = UUID.randomUUID().toString(),
                userId = "user456",
                userName = "Bob",
                text = "Looks awesome!",
                createdAt = LocalDate.now(),
                createdAtTime = LocalTime.now())

        // Prepare the updated suggestion with the new comment
        val updatedSuggestion = suggestion.copy(comments = suggestion.comments + newComment)
        coEvery { mockTripsRepository.updateSuggestionInTrip(tripId, updatedSuggestion) } returns
            true
        coEvery { mockTripsRepository.getAllSuggestionsFromTrip(tripId) } returns
            listOf(updatedSuggestion)

        // Perform the action to add a comment
        viewModel.addComment(suggestion, newComment)
        advanceUntilIdle()

        // Assert the new comment is included and the comments count is correct
        assertTrue(viewModel.state.value.first().comments.contains(newComment))
        assertEquals(suggestion.comments.size + 1, viewModel.state.value.first().comments.size)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testUpdateComment() =
      runBlockingTest(testDispatcher) {
        // Load the existing suggestions to establish initial state
        viewModel.loadSuggestion(tripId)
        advanceUntilIdle()

        // Identify the original suggestion and comment
        val originalSuggestion = viewModel.state.value.first()
        val originalComment = originalSuggestion.comments.first()

        // Create the updated comment with modified text
        val updatedText = "Absolutely must visit!"
        val updatedComment = originalComment.copy(text = updatedText)

        // Prepare the suggestion with the updated comment
        val updatedSuggestion =
            originalSuggestion.copy(
                comments =
                    originalSuggestion.comments.map {
                      if (it.commentId == updatedComment.commentId) updatedComment else it
                    })
        coEvery { mockTripsRepository.updateSuggestionInTrip(tripId, updatedSuggestion) } returns
            true
        coEvery { mockTripsRepository.getAllSuggestionsFromTrip(tripId) } returns
            listOf(updatedSuggestion)

        // Execute the action to update the comment
        viewModel.updateComment(originalSuggestion, updatedComment)
        advanceUntilIdle()

        // Verify the comment text has been updated as expected
        assertEquals(updatedText, viewModel.state.value.first().comments.first().text)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testToggleLikeSuggestion() =
      runBlockingTest(testDispatcher) {
        // Load suggestions to establish initial state
        viewModel.loadSuggestion(tripId)
        advanceUntilIdle()

        // Capture the initial state of the like status for the suggestion
        val suggestion = viewModel.state.value.first()
        val initialLikeStatus = viewModel.getIsLiked(suggestion.suggestionId)

        // Mock the repository to reflect a change in the like status
        coEvery { mockTripsRepository.getAllSuggestionsFromTrip(any()) } returns
            listOf(suggestion.copy(userLikes = listOf("")))

        // Execute the toggle like action
        viewModel.toggleLikeSuggestion(suggestion)
        advanceUntilIdle()

        // Assert that the like status has toggled as expected
        assertEquals(!initialLikeStatus, viewModel.getIsLiked(suggestion.suggestionId))
      }

  /**
   * Test the toggle like suggestion method with a majority check with the stop status set to ADDED.
   */
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testToggleLikeSuggestionWithMajorityCheck() =
      runBlockingTest(testDispatcher) {
        // Setup initial conditions
        val stop =
            Stop(
                stopId = UUID.randomUUID().toString(),
                title = "Test Stop",
            )

        val suggestion =
            Suggestion(
                suggestionId = "testSuggestionId",
                userId = "testUserId",
                userName = "testUserName",
                text = "Test Suggestion",
                createdAt = LocalDate.now(),
                createdAtTime = LocalTime.now(),
                stop = stop,
                comments = listOf(),
                userLikes = listOf("user1", "user2") // Initial likes
                )

        // Set viewModel state before accessing it
        viewModel.loadSuggestion(
            tripId) // Ensure this method properly populates the state or directly set it for
        // testing:
        coEvery { mockTripsRepository.getAllSuggestionsFromTrip(tripId) } returns listOf(suggestion)

        // Trigger loading to ensure state is populated
        viewModel.loadSuggestion(tripId)
        advanceUntilIdle()

        // Modify userLikes to simulate a toggle that reaches majority
        val updatedLikes = suggestion.userLikes + "currentUser"
        val updatedStop = suggestion.stop.copy(stopStatus = CalendarUiState.StopStatus.ADDED)
        val updatedSuggestion = suggestion.copy(userLikes = updatedLikes, stop = updatedStop)

        coEvery { mockTripsRepository.getSuggestionFromTrip(any(), any()) } returns
            updatedSuggestion
        coEvery { mockTripsRepository.getAllSuggestionsFromTrip(tripId) } returns
            listOf(updatedSuggestion)
        coEvery { mockTripsRepository.updateSuggestionInTrip(tripId, any()) } returns true
        coEvery { mockTripsRepository.addStopToTrip(any(), any()) } returns true

        // Perform the action to toggle a like
        viewModel.toggleLikeSuggestion(suggestion)
        advanceUntilIdle() // Ensure all coroutines are executed before checking

        // Assertions
        assertFalse(
            viewModel.state.value.contains(suggestion)) // Assuming suggestion should be removed
        assertTrue(
            viewModel.addedSuggestionsToStops.value.contains(
                suggestion.suggestionId)) // Check if added to stops
        coVerify {
          mockTripsRepository.updateSuggestionInTrip(
              tripId, match { it.stop.stopStatus == CalendarUiState.StopStatus.ADDED })
        }
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testTransformToStop() =
      runBlockingTest(testDispatcher) {
        // Load suggestions to establish the initial state
        viewModel.loadSuggestion(tripId)
        advanceUntilIdle()

        // Identify the suggestion to be transformed
        val suggestion = viewModel.state.value.first()

        // Mock the repository to simulate the suggestion being transformed and removed from the
        // list
        val updatedSuggestions =
            viewModel.state.value.filterNot { it.suggestionId == suggestion.suggestionId }
        coEvery { mockTripsRepository.getAllSuggestionsFromTrip(tripId) } returns updatedSuggestions

        // Execute the transformation of the suggestion to a stop
        viewModel.transformToStop(suggestion)
        advanceUntilIdle()

        // Assert that the suggestion is no longer present in the state
        assertFalse(viewModel.state.value.contains(suggestion))
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testDeleteSuggestion() =
      runBlockingTest(testDispatcher) {
        // Load suggestions to establish initial state
        viewModel.loadSuggestion(tripId)
        advanceUntilIdle()

        // Identify the suggestion to be deleted
        val suggestion = viewModel.state.value.first()

        // Mock the repository response for deleting a suggestion
        coEvery {
          mockTripsRepository.removeSuggestionFromTrip(tripId, suggestion.suggestionId)
        } returns true

        // Execute the deletion of the suggestion
        viewModel.deleteSuggestion(suggestion)
        advanceUntilIdle()

        // Verify repository methods were called as expected
        coVerify { mockTripsRepository.removeSuggestionFromTrip(tripId, suggestion.suggestionId) }
        coVerify { mockTripsRepository.getAllSuggestionsFromTrip(tripId) }
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testConfirmDeleteSuggestion() =
      runBlockingTest(testDispatcher) {
        // Load suggestions to prepare for the operation
        viewModel.loadSuggestion(tripId)
        advanceUntilIdle()

        // Select the suggestion to confirm its deletion
        val suggestion = viewModel.state.value.first()

        // Perform the deletion confirmation action
        viewModel.confirmDeleteSuggestion(suggestion)
        advanceUntilIdle()

        // Verify the delete action was confirmed at the repository level
        coVerify { mockTripsRepository.removeSuggestionFromTrip(tripId, suggestion.suggestionId) }
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testShowSuggestionBottomSheet() =
      runBlockingTest(testDispatcher) {
        // Load suggestions to get the current state
        viewModel.loadSuggestion(tripId)
        advanceUntilIdle()

        // Select a suggestion to display in the bottom sheet
        val suggestion = viewModel.state.value.first()

        // Execute the action to show the bottom sheet for the selected suggestion
        viewModel.showSuggestionBottomSheet(suggestion)
        advanceUntilIdle()

        // Verify the bottom sheet is visible and the selected suggestion is correct
        assertTrue(viewModel.bottomSheetVisible.value)
        assertEquals(suggestion, viewModel.selectedSuggestion.value)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testDeleteComment() =
      runBlockingTest(testDispatcher) {
        // Prepare by loading suggestions and setting the initial UI state
        viewModel.loadSuggestion(tripId)
        advanceUntilIdle()

        // Identify the suggestion and the comment to be deleted
        val suggestion = viewModel.state.value.first()
        val initialComment = suggestion.comments.first()

        // Simulate UI action that selects the comment to be deleted
        viewModel.showBottomSheet(initialComment)
        advanceUntilIdle()

        // Define the expected state of comments after deletion
        val expectedComments = suggestion.comments - initialComment
        coEvery { mockTripsRepository.updateSuggestionInTrip(tripId, any()) } returns true

        // Execute the deletion of the comment
        viewModel.deleteComment(suggestion)
        advanceUntilIdle()

        // Verify the correct repository method was called with the expected state
        coVerify {
          mockTripsRepository.updateSuggestionInTrip(
              tripId, match { it.comments == expectedComments })
        }
        // Assert the UI reflects that the bottom sheet should be hidden post-deletion
        assertFalse(viewModel.bottomSheetVisible.value)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testConfirmDeleteComment() =
      runBlockingTest(testDispatcher) {
        // Set up by loading suggestions and selecting a comment
        viewModel.loadSuggestion(tripId)
        advanceUntilIdle()

        // Select a comment to set up state for deletion confirmation
        val suggestion = viewModel.state.value.first()
        val initialComment = suggestion.comments.first()
        viewModel.showBottomSheet(initialComment)
        advanceUntilIdle()

        // Mock the repository update to always succeed
        coEvery { mockTripsRepository.updateSuggestionInTrip(tripId, any()) } returns true

        // Perform the confirmation action for deleting the comment
        viewModel.confirmDeleteComment(suggestion)
        advanceUntilIdle()

        // Verify that the UI state reflects no selected comment after deletion
        assertNull(viewModel.selectedComment.value)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testShowDeleteDialog() =
      runBlockingTest(testDispatcher) {
        // Directly test the UI reaction to showing a delete dialog
        viewModel.showDeleteDialog()
        assertTrue(viewModel.showDeleteDialog.value)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testHideDeleteDialog() =
      runBlockingTest(testDispatcher) {
        // Directly test the UI reaction to hiding a delete dialog
        viewModel.hideDeleteDialog()
        // Assert that the delete dialog is no longer visible
        assertFalse(viewModel.showDeleteDialog.value)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testSetSelectedSuggestion() =
      runBlockingTest(testDispatcher) {
        // Load suggestions to set the initial state and select one
        viewModel.loadSuggestion(tripId)
        advanceUntilIdle()

        // Identify and select a specific suggestion
        val suggestion = viewModel.state.value.first()
        viewModel.setSelectedSuggestion(suggestion)
        advanceUntilIdle()

        // Verify that the selected suggestion is correctly set in the ViewModel
        assertEquals(suggestion, viewModel.selectedSuggestion.value)
      }

  @Test
  fun testGetNbrLiked() =
      runBlockingTest(testDispatcher) {
        // Load suggestions to ensure there is data to test
        viewModel.loadSuggestion(tripId)
        advanceUntilIdle()

        // Test the method that fetches the number of likes for a specific suggestion ID
        val likesCount = viewModel.getNbrLiked("suggestion1")

        // Assert that the number of likes matches the expected count (note: test seems
        // contradictory in comment, adjust if needed)
        assertEquals(0, likesCount) // Assuming the test setup has no users liking the suggestion
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testEditCommentOption() =
      runBlockingTest(testDispatcher) {
        // Simulate starting the comment editing process
        viewModel.editCommentOption()

        // Verify that the state reflects that the bottom sheet is closed and editing mode is active
        assertFalse(viewModel.bottomSheetVisible.value)
        assertTrue(viewModel.editingComment.value)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testNonAdminNonOwnerCannotSeeVoteIcon() =
      runBlockingTest(testDispatcher) {
        // Set current user role to MEMBER for testing the role restriction on the vote icon click.
        // This is the case when the user is not an admin or owner.
        SessionManager.setUserSession("user", "user@example.com", "token", Role.MEMBER)

        // Load suggestions to establish initial state
        viewModel.loadSuggestion(tripId)
        advanceUntilIdle()

        // Identify the suggestion
        val suggestion = viewModel.state.value.first()

        // Ensure non-admin/non-owner cannot see the vote icon
        assertFalse(
            viewModel.getCurrentUserRole() == Role.ADMIN ||
                viewModel.getCurrentUserRole() == Role.OWNER)
        assertFalse(viewModel.getVoteIconClicked(suggestion.suggestionId))
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testVoteIconClickWithStartTime() =
      runBlockingTest(testDispatcher) {
        // Load suggestions to establish initial state
        viewModel.loadSuggestion(tripId)
        advanceUntilIdle()

        // Identify the suggestion to vote on
        val suggestion = viewModel.state.value.first()

        // Ensure initial state has the vote icon not clicked
        assertFalse(viewModel.getVoteIconClicked(suggestion.suggestionId))

        // Mock the repository to simulate the vote icon click and countdown start
        val updatedSuggestion =
            suggestion.copy(voteIconClicked = true, voteStartTime = LocalDateTime.now())
        coEvery { mockTripsRepository.getSuggestionFromTrip(any(), any()) } returns
            updatedSuggestion
        coEvery { mockTripsRepository.updateSuggestionInTrip(tripId, any()) } returns true

        // Perform the action to click the vote icon
        viewModel.toggleVoteIconClicked(suggestion)
        advanceUntilIdle()

        // Verify that the vote icon click has started the countdown
        assertFalse(
            viewModel.getVoteIconClicked(
                suggestion
                    .suggestionId)) // as the icon has been clicked, it is no longer clickable, so
        // it returns false

        val startTime = viewModel.getStartTime(suggestion.suggestionId)
        assertNotNull(startTime) // Check if the start time is not null
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testStartCountdown() =
      runBlockingTest(testDispatcher) {
        // Load the suggestions to set initial state
        viewModel.loadSuggestion(tripId)
        advanceUntilIdle()

        // Capture the suggestion state before clicking the vote icon
        val suggestion = viewModel.state.value.first()
        assertFalse(viewModel.getVoteIconClicked(suggestion.suggestionId))

        // Perform the action to toggle the vote icon and start the countdown
        viewModel.toggleVoteIconClicked(suggestion)
        advanceUntilIdle()

        // Assert that the countdown is running
        val remainingTimeFlow = viewModel.getRemainingTimeFlow(suggestion.suggestionId)
        assertTrue(remainingTimeFlow.value == "23:59:59") // the countdown starts at 23:59:59
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testCountdownFormat() =
      runBlockingTest(testDispatcher) {
        // Load the suggestions to set initial state
        viewModel.loadSuggestion(tripId)
        advanceUntilIdle()

        // Capture the suggestion state before clicking the vote icon
        val suggestion = viewModel.state.value.first()
        assertFalse(viewModel.getVoteIconClicked(suggestion.suggestionId))

        // Perform the action to toggle the vote icon and start the countdown
        viewModel.toggleVoteIconClicked(suggestion)
        advanceUntilIdle()

        // Assert that the countdown is in the correct format
        val remainingTimeFlow = viewModel.getRemainingTimeFlow(suggestion.suggestionId)
        assertTrue(remainingTimeFlow.value.matches(Regex("\\d{2}:\\d{2}:\\d{2}")))
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testIconReplacement() =
      runBlockingTest(testDispatcher) {
        // Load the suggestions to set initial state
        viewModel.loadSuggestion(tripId)
        advanceUntilIdle()

        // Capture the suggestion state before clicking the vote icon
        val suggestion = viewModel.state.value.first()
        assertFalse(viewModel.getVoteIconClicked(suggestion.suggestionId))

        // Perform the action to toggle the vote icon and start the countdown
        viewModel.toggleVoteIconClicked(suggestion)
        advanceUntilIdle()

        // Reload suggestions to simulate the updated state from the backend
        coEvery { mockTripsRepository.getAllSuggestionsFromTrip(any()) } returns
            listOf(suggestion.copy(voteIconClicked = true))
        viewModel.loadSuggestion(tripId)
        advanceUntilIdle()

        // Assert that the vote icon is replaced and the up icon is displayed
        assertTrue(viewModel.getVoteIconClicked(suggestion.suggestionId))
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    // Reset the main dispatcher to the original Main dispatcher after tests
    Dispatchers.resetMain()
  }
}
