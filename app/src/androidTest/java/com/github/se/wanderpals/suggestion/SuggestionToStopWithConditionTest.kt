package com.github.se.wanderpals.suggestion

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionFeedContent
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.math.ceil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SuggestionToStopWithConditionTest {

  @get:Rule val composeTestRule = createComposeRule()

  private var fakeTripsRepository =
      TripsRepository("userid", Dispatchers.IO) // Assume necessary mocking for repository

  private fun createViewModel(
      suggestions: List<Suggestion>,
      usersCount: Int
  ): SuggestionsViewModel {
    return object : SuggestionsViewModel(fakeTripsRepository, "trip1") {
      private val _state = MutableStateFlow(suggestions)
      override val state = _state.asStateFlow()
      override val addedSuggestionsToStops = MutableStateFlow<List<String>>(emptyList())

      private fun checkAndTransformSuggestion(suggestion: Suggestion, usersCount: Int) {
        val likesCount = suggestion.userLikes.size
        val threshold =
            if (usersCount % 2 == 0) ceil(usersCount / 2.0).toInt() + 1
            else ceil(usersCount / 2.0).toInt()
        if (likesCount >= threshold &&
            !addedSuggestionsToStops.value.contains(suggestion.suggestionId)) {
          addedSuggestionsToStops.value += suggestion.suggestionId
        }
      }

      init {
        viewModelScope.launch {
          _state.value.forEach { suggestion -> checkAndTransformSuggestion(suggestion, usersCount) }
        }
      }
    }
  }

  /**
   * Test the transformation of suggestions to stops based on the number of likes with odd number of
   * users.
   */
  @Test
  fun testSuggestionTransformToStopOddNumberOfUsers() {
    // two suggestions, one with 3 likes and the other with 1 like
    val suggestions =
        listOf(
            Suggestion(suggestionId = "s1", userLikes = listOf("user1", "user2", "user3")),
            Suggestion(suggestionId = "s2", userLikes = listOf("user1")))
    val viewModel = createViewModel(suggestions, 3) // 3 users, so threshold is 2

    composeTestRule.setContent {
      SuggestionFeedContent(
          innerPadding = PaddingValues(),
          suggestionList = viewModel.state.value,
          searchSuggestionText = "",
          tripId = "trip1",
          suggestionsViewModel = viewModel,
          navigationActions = mockk(relaxed = true) // mock navigation actions
          )
    }

    // Verify the automatic transformation
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("suggestionFeedContentList")
        .onChildren()
        .assertCountEquals(1) // Only one suggestion should be visible

    composeTestRule.onNodeWithTag("suggestion1").assertDoesNotExist()
    composeTestRule
        .onNodeWithTag("suggestion2")
        .assertExists() // The suggestion with 1 like should be visible (i.e. suggestion1 should be
    // transformed)
  }

  /**
   * Test the transformation of suggestions to stops based on the number of likes with even number
   * of users.
   */
  @Test
  fun testSuggestionTransformToStopEvenNumberOfUsers() {
    // five suggestions
    val suggestions =
        listOf(
            Suggestion(suggestionId = "s1", userLikes = listOf("user1", "user2", "user3", "user4")),
            Suggestion(suggestionId = "s2", userLikes = listOf("user1")),
            Suggestion(suggestionId = "s3", userLikes = listOf("user2", "user5")),
            Suggestion(suggestionId = "s4", userLikes = listOf("user1", "user3")),
            Suggestion(
                suggestionId = "s5",
                userLikes = listOf("user1", "user2", "user3", "user4", "user5")))
    val viewModel = createViewModel(suggestions, 6) // 6 users, so threshold is 4

    composeTestRule.setContent {
      SuggestionFeedContent(
          innerPadding = PaddingValues(),
          suggestionList = viewModel.state.value,
          searchSuggestionText = "",
          tripId = "trip1",
          suggestionsViewModel = viewModel,
          navigationActions = mockk(relaxed = true) // mock navigation actions
          )
    }

    // Verify the automatic transformation
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("suggestionFeedContentList")
        .onChildren()
        .assertCountEquals(3) // Only three suggestion should be visible
    composeTestRule
        .onNodeWithTag("suggestion1")
        .assertDoesNotExist() // The suggestion with 4 likes (i.e. suggestion1) should have been
    // transformed
    composeTestRule
        .onNodeWithTag("suggestion2")
        .assertExists() // The other suggestions should be visible
    composeTestRule.onNodeWithTag("suggestion3").assertExists()
    composeTestRule.onNodeWithTag("suggestion4").assertExists()
    composeTestRule
        .onNodeWithTag("suggestion5")
        .assertDoesNotExist() // The suggestion with 5 likes (i.e. suggestion5) should have been
    // transformed
  }

  /**
   * Test the transformation of suggestions to stops when a user manually clicks the like button.
   */
  @Test
  fun testUserClickTransformsSuggestionToStop() {

    // fakeTripsRepository as a mock instead of a concrete class
    fakeTripsRepository = mockk<TripsRepository>(relaxed = true)

    coEvery {
      fakeTripsRepository.getSuggestionFromTrip(tripId = "trip1", suggestionId = "s1")
    } returns Suggestion(suggestionId = "s1", userLikes = listOf("user1", "user2"))
    coEvery {
      fakeTripsRepository.getSuggestionFromTrip(tripId = "trip1", suggestionId = "s2")
    } returns Suggestion(suggestionId = "s2", userLikes = listOf("user1"))

    // Assume there are 4 users in the trip. Threshold for transformation is 3 likes.
    val suggestions =
        listOf(
            Suggestion(
                suggestionId = "s1",
                userLikes = listOf("user1", "user2"),
                stop = mockk(relaxed = true)),
            Suggestion(
                suggestionId = "s2", userLikes = listOf("user1"), stop = mockk(relaxed = true)))
    val viewModel = createViewModel(suggestions, 4)

    composeTestRule.setContent {
      SuggestionFeedContent(
          innerPadding = PaddingValues(),
          suggestionList = viewModel.state.value,
          searchSuggestionText = "",
          tripId = "trip1",
          suggestionsViewModel = viewModel,
          navigationActions = mockk(relaxed = true))
    }

    // Click the like button for the first suggestion, which should now meet the threshold.
    composeTestRule.onNodeWithTag("likeIconSuggestionFeedScreen_s1").performClick()

    composeTestRule.waitForIdle()

    // Ensure the second suggestion is still visible.
    composeTestRule.onNodeWithTag("suggestion2").assertExists()
  }
}
