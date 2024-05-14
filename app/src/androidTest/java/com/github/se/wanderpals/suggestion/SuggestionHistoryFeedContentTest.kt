package com.github.se.wanderpals.suggestion

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionHistoryFeedContent
import com.github.se.wanderpals.ui.screens.trip.agenda.CalendarUiState
import java.time.LocalDate
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
class FakeSuggestionsViewModelForHistory(
    suggestionRepository: TripsRepository? =
        TripsRepository("", Dispatchers.IO), // to avoid null pointer exception
    tripId: String = ""
) : SuggestionsViewModel(suggestionRepository, tripId) {
  private val _state = MutableStateFlow(emptyList<Suggestion>())
  override val state = _state

  private val _isLoading = MutableStateFlow(false)
  override val isLoading = _isLoading

  fun updateSuggestionList(suggestions: List<Suggestion>) {
    _state.value = suggestions
  }

  fun setLoadingState(isLoading: Boolean) {
    _isLoading.value = isLoading
  }
}

@RunWith(AndroidJUnit4::class)
class SuggestionHistoryFeedContentTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var suggestionList: List<Suggestion>

  @Before
  fun setup() {
    val stop1 =
        Stop(
            stopId = "stop1",
            title = "First Stop",
            description = "Description for first stop",
            geoCords = GeoCords(37.7749, -122.4194))

    val stop2 =
        Stop(
            stopId = "stop2",
            title = "Second Stop",
            description = "Description for second stop",
            geoCords = GeoCords(40.7128, -74.0060))

    suggestionList =
        listOf(
            Suggestion(
                suggestionId = "sugg1",
                userName = "User1",
                stop = stop1,
                createdAt = LocalDate.now(),
                stopStatus = CalendarUiState.StopStatus.ADDED),
            Suggestion(
                suggestionId = "sugg2",
                userName = "User2",
                stop = stop2,
                createdAt = LocalDate.now(),
                stopStatus = CalendarUiState.StopStatus.ADDED))
  }

  /**
   * A test to verify that the SuggestionHistoryFeedContent composable displays the list of
   * suggestion histories when the view model has suggestions to display.
   */
  @Test
  fun testSuggestionHistoryFeedContent_DisplaysSuggestions() {
    val fakeViewModel =
        FakeSuggestionsViewModelForHistory().apply {
          updateSuggestionList(suggestionList)
          setLoadingState(false)
        }

    composeTestRule.setContent {
      SuggestionHistoryFeedContent(tripId = "dummyTripId", suggestionsViewModel = fakeViewModel)
    }

    composeTestRule.onNodeWithTag("suggestionHistoryFeedContentList").assertIsDisplayed()
    composeTestRule.onNodeWithTag("suggestionHistory1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("suggestionHistory2").assertIsDisplayed()
  }

  /**
   * A test to verify that the SuggestionHistoryFeedContent composable displays a loading indicator
   * when the view model is in a loading state.
   */
  @Test
  fun testSuggestionHistoryFeedContent_ShowsLoading() {
    val fakeViewModel = FakeSuggestionsViewModelForHistory().apply { setLoadingState(true) }

    composeTestRule.setContent {
      SuggestionHistoryFeedContent(tripId = "dummyTripId", suggestionsViewModel = fakeViewModel)
    }

    composeTestRule.onNodeWithTag("suggestionHistoryLoading").assertIsDisplayed()
  }

  /**
   * A test to verify that the SuggestionHistoryFeedContent composable displays a message when there
   * are no suggestions to display.
   */
  @Test
  fun testSuggestionHistoryFeedContent_ShowsNoSuggestionsMessage() {
    val fakeViewModel =
        FakeSuggestionsViewModelForHistory().apply {
          updateSuggestionList(emptyList())
          setLoadingState(false)
        }

    composeTestRule.setContent {
      SuggestionHistoryFeedContent(tripId = "dummyTripId", suggestionsViewModel = fakeViewModel)
    }

    composeTestRule.onNodeWithTag("noSuggestionsHistoryToDisplay").assertIsDisplayed()
  }
}
