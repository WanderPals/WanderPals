package com.github.se.wanderpals.suggestion

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.screens.SuggestionFeedScreen
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionBottomBar
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionFeedContent
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

///*
class FakeSuggestionsViewModel(tripsRepository: TripsRepository? = TripsRepository("fake_uid", Dispatchers.Unconfined)
                               , tripId: String = "") :
    SuggestionsViewModel(tripsRepository, tripId) {
    //todo: change values for No duplications
        private val testSuggestion: Suggestion =
        Suggestion(
            suggestionId = "",
            userId = "",
            userName = "tempUsername",
            text = "",
            createdAt = LocalDate.now(),
            stop =
            Stop(
                stopId = "",
                title = "Stop",
                address = "",
                date = LocalDate.of(2024, 4, 16),
                startTime = LocalTime.of(12, 0),
                budget = 20.0,
                duration = 120,
                description = "This is a Stop",
                geoCords = GeoCords(0.0, 0.0),
                website = "www.example.com",
            ))

    private val testSuggestion2: Suggestion =
        Suggestion(
            suggestionId = "",
            userId = "",
            userName = "tempUsername",
            text = "",
            createdAt = LocalDate.now(),
            stop =
            Stop(
                stopId = "",
                title = "Stop",
                address = "",
                date = LocalDate.of(2024, 4, 16),
                startTime = LocalTime.of(12, 0),
                budget = 0.0,
                duration = 120,
                description = "This is a Stop",
                geoCords = GeoCords(0.0, 0.0),
            ))

    private val _state = MutableStateFlow(listOf(testSuggestion, testSuggestion2))
    override val state: StateFlow<List<Suggestion>> = _state

    // Override any other necessary open functions with dummy implementations if required
}
//*/

@RunWith(AndroidJUnit4::class)
class SuggestionFeedTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var suggestionList: List<Suggestion>

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Before
  fun testSetup() {
    // Initialize the mock NavigationActions
    mockNavActions = mockk(relaxed = true)

      // Initialize the suggestion list with dummy data
      suggestionList = listOf(
          Suggestion(
              suggestionId = "sugg1",
              userId = "user1",
              userName = "userOne",
              text = "First suggestion",
              createdAt = LocalDate.now(),
              stop = Stop(
                  stopId = "stop1",
                  title = "First Stop",
                  address = "123 First Street",
                  date = LocalDate.of(2024, 4, 16),
                  startTime = LocalTime.of(12, 0),
                  budget = 20.0,
                  duration = 60,
                  description = "Description for first stop",
                  geoCords = GeoCords(37.7749, -122.4194),
                  website = "http://firststop.example.com"
              )
          ),
          Suggestion(
              suggestionId = "sugg2",
              userId = "user2",
              userName = "userTwo",
              text = "Second suggestion",
              createdAt = LocalDate.now(),
              stop = Stop(
                  stopId = "stop2",
                  title = "Second Stop",
                  address = "456 Second Avenue",
                  date = LocalDate.of(2024, 4, 17),
                  startTime = LocalTime.of(14, 30),
                  budget = 50.0,
                  duration = 90,
                  description = "Description for second stop",
                  geoCords = GeoCords(40.7128, -74.0060),
                  website = "http://secondstop.example.com"
              )
          ),
          Suggestion(
              suggestionId = "sugg3",
              userId = "user3",
              userName = "userThree",
              text = "Third suggestion",
              createdAt = LocalDate.now(),
              stop = Stop(
                  stopId = "stop3",
                  title = "Third Stop",
                  address = "789 Third Boulevard",
                  date = LocalDate.of(2024, 4, 18),
                  startTime = LocalTime.of(10, 0),
                  budget = 30.0,
                  duration = 120,
                  description = "Description for third stop",
                  geoCords = GeoCords(34.0522, -118.2437),
                  website = "http://thirdstop.example.com"
              )
          )
      )


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
            suggestionRepository = FakeSuggestionsViewModel())

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
          suggestionRepository = FakeSuggestionsViewModel())
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
          suggestionsViewModel = FakeSuggestionsViewModel())
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
      // Place the SuggestionBottomBar composable within the test context
      SuggestionBottomBar(onSuggestionClick = {}) // todo: will have onSuggestionClick after William
    }

    // Now check if the button with the testTag "suggestionButtonExists" is displayed
    onComposeScreen<SuggestionFeedScreen>(composeTestRule) {
      suggestionButtonExists.assertIsDisplayed()
    }
  }
}
