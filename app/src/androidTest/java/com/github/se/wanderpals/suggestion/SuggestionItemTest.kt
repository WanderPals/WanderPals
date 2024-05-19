package com.github.se.wanderpals.suggestion

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.*
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionItem
import com.github.se.wanderpals.ui.theme.WanderPalsTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SuggestionItemTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var suggestion: Suggestion
  private lateinit var fakeViewModel: FakeSuggestionsViewModel

  @Before
  fun setUp() {
    suggestion =
        Suggestion(
            suggestionId = "sugg1",
            userId = "user1",
            userName = "John Doe",
            text = "This is a suggestion",
            createdAt = LocalDate.now(),
            createdAtTime = LocalTime.now(),
            stop =
                Stop(
                    stopId = "stop1",
                    title = "Stop 1",
                    address = "123 Main St",
                    date = LocalDate.now(),
                    startTime = LocalTime.now(),
                    duration = 1,
                    budget = 100.0,
                    description = "This is a stop",
                    geoCords = GeoCords(0.0, 0.0),
                    website = "https://www.example.com",
                    imageUrl = "https://www.example.com/image.jpg"),
            comments =
                listOf(
                    Comment(
                        commentId = "comment1",
                        userId = "user1",
                        userName = "Jane Doe",
                        text = "This is a comment",
                        createdAt = LocalDate.now(),
                        createdAtTime = LocalTime.now())),
            userLikes = listOf("user1"),
            voteIconClicked = false,
            voteStartTime = LocalDateTime.MIN)

    fakeViewModel = FakeSuggestionsViewModel().apply { updateSuggestionList(listOf(suggestion)) }
  }

  @Test
  fun testUpIconExistsForAllUsers() {
    // Set current user role to MEMBER
    SessionManager.setUserSession(
        "userId",
        "user@example.com",
        "token",
        Role.MEMBER) // Set current user role to MEMBER, who has less permissions than ADMIN or
    // OWNER

    composeTestRule.setContent {
      WanderPalsTheme {
        SuggestionItem(
            suggestion = suggestion,
            onClick = {},
            tripId = "dummyTripId",
            viewModel = fakeViewModel,
            userRole = Role.MEMBER)
      }
    }

    // Ensure the "Up" icon is displayed
    composeTestRule.onNodeWithTag("upIcon").performScrollTo().assertExists()
  }

  @Test
  fun testSuggestionItemIsScrollableAdminCanVoteAndVoteIconIsVisibleForAdmin() {
    // Set current user role to ADMIN to ensure the admin can launch the vote
    SessionManager.setUserSession(
        "adminId",
        "admin@example.com",
        "token",
        Role.ADMIN) // the admin can launch the vote, so the vote icon should be visible for the
    // admin

    composeTestRule.setContent {
      WanderPalsTheme {
        SuggestionItem(
            suggestion = suggestion,
            onClick = {},
            tripId = "dummyTripId",
            viewModel = fakeViewModel,
            userRole = Role.ADMIN)
      }
    }

    // Check that the card is scrollable and the admin can launch the vote
    composeTestRule
        .onNodeWithTag("voteIcon")
        .performScrollTo()
        .performClick() // scroll to the vote icon and click it to launch the vote
    composeTestRule
        .onNodeWithTag("voteIcon")
        .performScrollTo()
        .assertExists() // ensure the vote icon exists for the admin
  }

  @Test
  fun testSuggestionItemIsScrollableOwnerCanVoteAndVoteIconIsVisibleForOwner() {
    // Set current user role to OWNER to ensure the owner can launch the vote
    SessionManager.setUserSession(
        "ownerId",
        "owner@example.com",
        "token",
        Role.OWNER) // the owner can launch the vote, so the vote icon should be visible for the
    // owner

    composeTestRule.setContent {
      WanderPalsTheme {
        SuggestionItem(
            suggestion = suggestion,
            onClick = {},
            tripId = "dummyTripId",
            viewModel = fakeViewModel,
            userRole = Role.OWNER)
      }
    }

    // Check that the card is scrollable and the owner can launch the vote
    composeTestRule
        .onNodeWithTag("voteIcon")
        .performScrollTo()
        .performClick() // scroll to the vote icon and click it to launch the vote
    composeTestRule
        .onNodeWithTag("voteIcon")
        .performScrollTo()
        .assertExists() // ensure the vote icon exists for the owner
  }
}
