package com.github.se.wanderpals.suggestion

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.TripsRepository
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

@RunWith(AndroidJUnit4::class)
class SuggestionFeedTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var suggestionList: List<Suggestion>

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Before
  fun testSetup() {
    // Initialize the mock NavigationActions
    mockNavActions = mockk(relaxed = true)

    // Setup dummy data for testing:

    //        val tripId = "dummyTripId"

    val stop1 =
        Stop(
            stopId = "OSK001",
            title = "Osaka Castle",
            address = "1-1 Osakajo, Chuo Ward, Osaka, 540-0002, Japan",
            date = LocalDate.of(2024, 4, 10),
            startTime = LocalTime.of(9, 0),
            duration = 120,
            budget = 600.0,
            description = "Osaka Castle is one of Japan's most famous landmarks...",
            geoCords = GeoCords(latitude = 34.687315, longitude = 135.526201),
            website = "https://www.osakacastle.net/",
            imageUrl = "")
    val stop2 =
        Stop(
            stopId = "OSK002",
            title = "Dotonbori",
            address = "Dotonbori, Chuo Ward, Osaka, 542-0071, Japan",
            date = LocalDate.of(2024, 4, 10),
            startTime = LocalTime.of(18, 0), // Best experienced in the evening
            duration = 180, // Approximately 3 hours
            budget = 3000.0, // Food, shopping, and other activities
            description =
                "Dotonbori is Osaka's most famous tourist destination, known for its bright neon lights, extravagant signage, and abundant dining options.",
            geoCords = GeoCords(latitude = 34.668723, longitude = 135.501295),
            website = "https://www.dotonbori.or.jp/en/",
            imageUrl = "")
    val stop3 =
        Stop(
            stopId = "OSK003",
            title = "Umeda Sky Building",
            address = "1-1-88 Oyodonaka, Kita Ward, Osaka, 531-0076, Japan",
            date = LocalDate.of(2024, 4, 11),
            startTime = LocalTime.of(10, 30), // Opens at 10:30 AM
            duration = 90, // 1.5 hours visit
            budget = 1500.0, // Entrance fee and other possible expenses
            description =
                "The Umeda Sky Building is a spectacular high rise building in the Kita district of Osaka, featuring a futuristic observatory, the Floating Garden.",
            geoCords = GeoCords(latitude = 34.705938, longitude = 135.490357),
            website = "http://www.kuchu-teien.com/",
            imageUrl = "")

    val comment1 =
        Comment("commentId1", "usercmtId1", "userNamecmt1", "Great idea!", LocalDate.now())
    val comment2 =
        Comment(
            "commentId2",
            "usercmtId2",
            "userNamecmt2",
            "I've been here before, it's wonderful.",
            LocalDate.now())
    val comment3 =
        Comment(
            "commentId3", "usercmtId3", "userNamecmt3", "This fits our schedule.", LocalDate.now())
    val comment4 =
        Comment(
            "commentId4", "usercmtId4", "userNamecmt4", "This place seems great.", LocalDate.now())

    // Example list of comments
    val dummyCommentList = listOf(comment1, comment2, comment3, comment4)

    // Use `this.suggestionList` to ensure we're assigning to the class-level variable.
    this.suggestionList =
        listOf(
            Suggestion(
                "suggestionId1",
                "userId1",
                "userName1",
                "Let us go here!",
                LocalDate.of(2024, 1, 1),
                stop1,
                dummyCommentList,
                emptyList()),
            Suggestion(
                "suggestionId2",
                "userId2",
                "userName2",
                "I love this place",
                LocalDate.of(2024, 2, 2),
                stop2,
                emptyList(),
                emptyList()),
            Suggestion(
                "suggestionId3",
                "userId3",
                "userName3",
                "This is a great place to visit. Let us go here together! I am sure you will love it! I have been there before and it was amazing! " +
                    "Trying to convince you to go here with me. coz I know you will love it!",
                LocalDate.of(2024, 3, 29),
                stop3,
                dummyCommentList,
                emptyList()))
  }

  /** Test that the suggestion feed screen displays the suggestions when the list is not empty. */
  @Test
  fun suggestionFeedScreen_showsSuggestions_whenListIsNotEmpty() {
    composeTestRule.setContent {
      // Mock NavigationActions or use a dummy implementation for testing
      //            val navigationActions = NavigationActions(/* pass necessary arguments or mocks
      // */)
      SuggestionFeedContent(
          innerPadding = PaddingValues(),
          navigationActions = mockNavActions,
          suggestionList = suggestionList,
          searchSuggestionText = "")
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
          navigationActions = mockNavActions,
          suggestionList = emptyList(),
          searchSuggestionText = "")
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
      com.github.se.wanderpals.ui.screens.trip.Suggestion(tripId = "dummyTestTripId", tripsRepository = TripsRepository("a", Dispatchers.IO))
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
