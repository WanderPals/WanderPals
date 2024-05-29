package com.github.se.wanderpals.suggestion

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.screens.SuggestionFeedScreen
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionFeedContent
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionFilterButton
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionFilterOptions
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionSearchBar
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A fake implementation of the SuggestionsViewModel class for testing purposes. It is used anywhere
 * when the SuggestionsViewModel class is required.
 */
class FakeSuggestionsViewModel(
    tripsRepository: TripsRepository? = TripsRepository("fake_uid", Dispatchers.Unconfined),
    tripId: String = ""
) : SuggestionsViewModel(tripsRepository, tripId) {

  // Override any other necessary open functions with dummy implementations if required

  private val _state = MutableStateFlow(emptyList<Suggestion>())
  private val _likedSuggestions = MutableStateFlow<List<String>>(emptyList())
  private val currentLoggedInUId = tripsRepository?.uid!!

  // Function to directly set the suggestion list state for testing
  fun updateSuggestionList(suggestions: List<Suggestion>) {
    _state.value = suggestions
    _likedSuggestions.value =
        suggestions.filter { it.userLikes.contains(currentLoggedInUId) }.map { it.suggestionId }
  }

  override fun toggleVoteIconClicked(suggestion: Suggestion) {}

  fun toggleLikeSuggestion(tripId: String, suggestion: Suggestion) {
    val suggestionIndex = _state.value.indexOfFirst { it.suggestionId == suggestion.suggestionId }
    val isLiked = _likedSuggestions.value.contains(suggestion.suggestionId)

    if (isLiked) {
      val updatedSuggestion = suggestion.copy(userLikes = suggestion.userLikes - currentLoggedInUId)
      _state.value = _state.value.toMutableList().apply { set(suggestionIndex, updatedSuggestion) }
      _likedSuggestions.value -= suggestion.suggestionId
    } else {
      val updatedSuggestion = suggestion.copy(userLikes = suggestion.userLikes + currentLoggedInUId)
      _state.value = _state.value.toMutableList().apply { set(suggestionIndex, updatedSuggestion) }
      _likedSuggestions.value += suggestion.suggestionId
    }
  }
}

@RunWith(AndroidJUnit4::class)
class SuggestionFeedTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var suggestionList: List<Suggestion>
  private lateinit var commentList: List<Comment>

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Before
  fun testSetup() {
    // Initialize the mock NavigationActions
    mockNavActions = mockk(relaxed = true)

    // Initialize the suggestion list with dummy data
    val stop4 =
        Stop(
            stopId = "OSK004",
            title = "Umeda Sky Building 2",
            address = "",
            date = LocalDate.of(2024, 4, 11),
            startTime = LocalTime.of(10, 30), // Opens at 10:30 AM
            duration = 90, // 1.5 hours visit
            budget = 1500.0, // Entrance fee and other possible expenses
            description =
                "The Umeda Sky Building is a spectacular high rise building in the Kita district of Osaka, featuring a futuristic observatory, the Floating Garden.",
            geoCords = GeoCords(latitude = 34.705938, longitude = 135.490357),
            website = "",
            imageUrl = "")

    val comment1 =
        Comment(
            "commentId1",
            "usercmtId1",
            "userNamecmt1",
            "Great idea!",
            LocalDate.now(),
            LocalTime.now())
    val comment2 =
        Comment(
            "commentId2",
            "usercmtId2",
            "userNamecmt2",
            "I've been here before, it's wonderful.",
            LocalDate.now(),
            LocalTime.now())
    val comment3 =
        Comment(
            "commentId3",
            "usercmtId3",
            "userNamecmt3",
            "This fits our schedule.",
            LocalDate.now(),
            LocalTime.now())
    val comment4 =
        Comment(
            "commentId4",
            "usercmtId4",
            "userNamecmt4",
            "This place seems great.",
            LocalDate.now(),
            LocalTime.now())

    // Example list of comments
    val dummyCommentList2 = listOf(comment1, comment2)
    val dummyCommentList3 = listOf(comment1, comment2, comment3)
    val dummyCommentList4 = listOf(comment1, comment2, comment3, comment4)

    val userLikes1 = listOf("ulId1", "ulId2")
    val userLikes3 = listOf("ulId1", "ulId2", "ulId3", "ulId5", "ulId6")
    val userLikes4 = listOf("ulId1", "ulId2", "ulId3", "ulId4")

    commentList = listOf(comment1, comment2, comment3, comment4)

    suggestionList =
        listOf(
            Suggestion(
                suggestionId = "sugg1",
                userId = "user1",
                userName = "userOne",
                text = "First suggestion",
                createdAt = LocalDate.now(),
                createdAtTime = LocalTime.now(),
                stop =
                    Stop(
                        stopId = "stop1",
                        title = "First Stop",
                        address = "123 First Street",
                        date = LocalDate.of(2024, 4, 16),
                        startTime = LocalTime.of(12, 0),
                        budget = 20.0,
                        duration = 60,
                        description = "Description for first stop",
                        geoCords = GeoCords(37.7749, -122.4194),
                        website = "http://firststop.example.com"),
                emptyList(),
                userLikes1),
            Suggestion(
                suggestionId = "sugg2",
                userId = "user2",
                userName = "userTwo",
                text = "Second suggestion",
                createdAt = LocalDate.now(),
                createdAtTime = LocalTime.now(),
                stop =
                    Stop(
                        stopId = "stop2",
                        title = "Second Stop",
                        address = "456 Second Avenue",
                        date = LocalDate.of(2024, 4, 17),
                        startTime = LocalTime.of(14, 30),
                        budget = 50.0,
                        duration = 90,
                        description = "Description for second stop",
                        geoCords = GeoCords(40.7128, -74.0060),
                        website = "http://secondstop.example.com"),
                dummyCommentList2,
                emptyList()),
            Suggestion(
                suggestionId = "sugg3",
                userId = "user3",
                userName = "userThree",
                text = "Third suggestion",
                createdAt = LocalDate.now(),
                createdAtTime = LocalTime.now(),
                stop =
                    Stop(
                        stopId = "stop3",
                        title = "Third Stop",
                        address = "789 Third Boulevard",
                        date = LocalDate.of(2024, 4, 18),
                        startTime = LocalTime.of(10, 0),
                        budget = 30.0,
                        duration = 120,
                        description = "Description for third stop",
                        geoCords = GeoCords(34.0522, -118.2437),
                        website = "http://thirdstop.example.com"),
                dummyCommentList3,
                userLikes3),
            Suggestion(
                "sugg4",
                "userId4",
                "userName4",
                "This is a great place to visit. Let us go here together! I am sure you will love it! I have been there before and it was amazing! " +
                    "Trying to convince you to go here with me. coz I know you will love it!",
                LocalDate.of(2024, 9, 29),
                createdAtTime = LocalTime.now(),
                stop4,
                dummyCommentList4,
                userLikes4))
  }

  /** Test that the suggestion feed screen displays the suggestions when the list is not empty. */
  @Test
  fun suggestionFeedScreen_showsSuggestions_whenListIsNotEmpty() {
    composeTestRule.setContent {
      SuggestionFeedContent(
          innerPadding = PaddingValues(),
          suggestionList = suggestionList,
          searchSuggestionText = "",
          tripId = "dummyTestTripId",
          suggestionsViewModel = FakeSuggestionsViewModel(),
          navigationActions = mockNavActions)
    }

    // Check if the three suggestions are displayed on the Suggestions Feed screen
    onComposeScreen<SuggestionFeedScreen>(composeTestRule) {
      suggestion1.assertIsDisplayed()
      suggestion2.assertIsDisplayed()
      suggestion3.assertIsDisplayed()
    }
  }

  /** Set the UI content to your testing screen with an empty suggestion list */
  @Test
  fun noSuggestionsMessageDisplayed_whenListIsEmpty() {
    composeTestRule.setContent {
      SuggestionFeedContent(
          innerPadding = PaddingValues(),
          suggestionList = emptyList(),
          searchSuggestionText = "",
          tripId = "dummyTestTripId", // a dummy trip ID
          suggestionsViewModel = FakeSuggestionsViewModel(),
          navigationActions = mockNavActions)
    }
    // Check if the message that has the testTag "noSuggestionsForUserText" is displayed
    onComposeScreen<SuggestionFeedScreen>(composeTestRule) {
      noSuggestionsForUserText.assertIsDisplayed()
    }
  }

  /** Test that the suggestion feed screen is displayed. */
  @Test
  fun suggestionFeedScreen_isDisplayed() {
    composeTestRule.setContent {
      // Simulate the Suggestion composable with the provided tripId
      com.github.se.wanderpals.ui.screens.trip.Suggestion(
          oldNavActions = mockNavActions,
          tripId = "dummyTestTripId", // a dummy trip ID
          suggestionsViewModel = FakeSuggestionsViewModel(),
          onSuggestionClick = {
            mockNavActions.setVariablesTrip("dummyTestTripId")
            mockNavActions.navigateTo(Route.CREATE_SUGGESTION)
          })
    }

    // Now check if the suggestion feed screen is displayed
    onComposeScreen<SuggestionFeedScreen>(composeTestRule) {
      suggestionFeedScreen.assertIsDisplayed()
    }
  }

  /**
   * Test that the suggestion creation button exists and is displayed on the Suggestions Feed
   * screen.
   */
  @Test
  fun suggestionButtonExists_isDisplayed() {
    composeTestRule.setContent {
      // Simulate the Suggestion composable with the provided tripId
      com.github.se.wanderpals.ui.screens.trip.Suggestion(
          oldNavActions = mockNavActions,
          tripId = "dummyTestTripId", // a dummy trip ID
          suggestionsViewModel = FakeSuggestionsViewModel(),
          onSuggestionClick = {
            mockNavActions.setVariablesTrip("dummyTestTripId")
            mockNavActions.navigateTo(Route.CREATE_SUGGESTION)
          })

    }

    // Now check if the button with the testTag "suggestionButtonExists" is displayed
    onComposeScreen<SuggestionFeedScreen>(composeTestRule) {
      suggestionButtonExists.assertIsDisplayed()
    }
  }

  /**
   * Test that the suggestion sorting options exists and is displayed on the Suggestions Feed
   * screen.
   */
  @Test
  fun suggestionFilterOptions_ExistsAndIsDisplayed() {
    composeTestRule.setContent { SuggestionFilterOptions(onFilterSelected = {}) }

    onComposeScreen<SuggestionFeedScreen>(composeTestRule) {
      suggestionSortingOptions.assertExists()
      suggestionSortingOptions.assertIsDisplayed()
    }
  }

  /**
   * Test that the suggestion sorting button exists and is displayed on the Suggestions Feed screen.
   */
  @Test
  fun suggestionSortingButton_ExistsAndIsDisplayed() {
    composeTestRule.setContent {
      SuggestionFilterButton(text = "Creation date", isSelected = false, onSelect = {})
    }

    onComposeScreen<SuggestionFeedScreen>(composeTestRule) {
      suggestionSortingButton.assertExists()
      suggestionSortingButton.assertIsDisplayed()
    }
  }

  /** Test that the suggestion search bar exists and is displayed on the Suggestions Feed screen. */
  @Test
  fun suggestionSearchBar_ExistsAndIsDisplayed() {
    composeTestRule.setContent {
      SuggestionSearchBar(searchSuggestionText = "", onSearchSuggestionTextChanged = {})
    }

    onComposeScreen<SuggestionFeedScreen>(composeTestRule) {
      suggestionSearchBar.assertExists()
      suggestionSearchBar.assertIsDisplayed()
    }
  }

  /**
   * Test that the clear suggestion search button exists, is displayed, and performs a click action.
   */
  @Test
  fun clearSuggestionSearchButton_ExistsAndIsDisplayedAndPerformsClick() {
    composeTestRule.setContent {
      SuggestionSearchBar(searchSuggestionText = "test", onSearchSuggestionTextChanged = {})
    }

    onComposeScreen<SuggestionFeedScreen>(composeTestRule) {
      clearSuggestionSearchButton.assertExists()
      clearSuggestionSearchButton.assertIsDisplayed()
      clearSuggestionSearchButton.performClick()
    }
  }
}
