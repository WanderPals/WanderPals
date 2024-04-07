package com.github.se.wanderpals.suggestion

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionFeedContent
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionBottomBar
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.Before
import io.mockk.every
import io.mockk.mockk
import org.junit.runner.RunWith
import io.mockk.junit4.MockKRule



class SuggestionFeedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var suggestionList: List<Suggestion>

    @RelaxedMockK
    lateinit var mockNavActions: NavigationActions

    @Before
    fun setup() {
        // Initialize the mock NavigationActions
        mockNavActions = mockk(relaxed = true)

        // Setup dummy data for testing
        val stop1 = Stop(
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
            imageUrl = ""
        )
        val stop2 = Stop(
            stopId = "OSK002",
            title = "Dotonbori",
            address = "Dotonbori, Chuo Ward, Osaka, 542-0071, Japan",
            date = LocalDate.of(2024, 4, 10),
            startTime = LocalTime.of(18, 0), // Best experienced in the evening
            duration = 180, // Approximately 3 hours
            budget = 3000.0, // Food, shopping, and other activities
            description = "Dotonbori is Osaka's most famous tourist destination, known for its bright neon lights, extravagant signage, and abundant dining options.",
            geoCords = GeoCords(latitude = 34.668723, longitude = 135.501295),
            website = "https://www.dotonbori.or.jp/en/",
            imageUrl = ""
        )
        val stop3 = Stop(
            stopId = "OSK003",
            title = "Umeda Sky Building",
            address = "1-1-88 Oyodonaka, Kita Ward, Osaka, 531-0076, Japan",
            date = LocalDate.of(2024, 4, 11),
            startTime = LocalTime.of(10, 30), // Opens at 10:30 AM
            duration = 90, // 1.5 hours visit
            budget = 1500.0, // Entrance fee and other possible expenses
            description = "The Umeda Sky Building is a spectacular high rise building in the Kita district of Osaka, featuring a futuristic observatory, the Floating Garden.",
            geoCords = GeoCords(latitude = 34.705938, longitude = 135.490357),
            website = "http://www.kuchu-teien.com/",
            imageUrl = ""
        )

        suggestionList = listOf(
            Suggestion("suggestionId1", "userId1", "userName1", "Let us go here!", LocalDate.of(2024, 1, 1), stop1, emptyList(), emptyList()),
            Suggestion("suggestionId2", "userId2", "userName2", "I love this place", LocalDate.of(2024, 2, 2), stop2, emptyList(), emptyList()),
            Suggestion("suggestionId3", "userId3", "userName3", "This is a great place to visit. Let us go here together! I am sure you will love it! I have been there before and it was amazing! " +
                    "Trying to convince you to go here with me. coz I know you will love it!",
                LocalDate.of(2024, 3, 29), stop3, emptyList(), emptyList()
            )
        )
    }

    // Set the UI content to your testing screen with the dummy data
    @Test
    fun suggestionFeedScreen_showsSuggestions_whenListIsNotEmpty() {
        composeTestRule.setContent {
            // Mock NavigationActions or use a dummy implementation for testing
//            val navigationActions = NavigationActions(/* pass necessary arguments or mocks */)
            SuggestionFeedContent(
                innerPadding = PaddingValues(),
                navigationActions = mockNavActions,
                suggestionList = suggestionList,
                searchText = ""
            )
        }


        // Assert that the suggestion titles are displayed using the test tags
        composeTestRule.onNodeWithTag("suggestion1").assertExists()
        composeTestRule.onNodeWithTag("suggestion2").assertExists()
        composeTestRule.onNodeWithTag("suggestion3").assertExists()

//        // Assert that the content within the tagged composables is correct
//        composeTestRule.onNodeWithTag("suggestion1").onNodeWithText("Osaka Castle").assertExists()
//        composeTestRule.onNodeWithTag("suggestion2").onNodeWithText("Dotonbori").assertExists()
//        composeTestRule.onNodeWithTag("suggestion3").onNodeWithText("Umeda Sky Building").assertExists()

    }

    // Set the UI content to your testing screen with an empty suggestion list
    @Test
    fun suggestionFeedScreen_showsNoSuggestionsText_whenListIsEmpty() {
        composeTestRule.setContent {
            SuggestionFeedContent(
                innerPadding = PaddingValues(),
                navigationActions = mockNavActions,
                suggestionList = emptyList(),
                searchText = ""
            )
        }

        // Assert that the no suggestions text is displayed
        composeTestRule.onNodeWithTag("noSuggestionsForUserText").assertExists()
    }

    //Test if the create suggestion button is displayed on the Suggestion Feed screen
    @Test
    fun suggestionBottomBar_hasCreateSuggestionButton() {
        // Arrange: Set the UI content with SuggestionBottomBar
        composeTestRule.setContent {
            // Since the onSuggestionClick isn't implemented yet, pass an empty lambda
            SuggestionBottomBar(onSuggestionClick = {})
            //             SuggestionBottomBar(onSuggestionClick = { /* Your click handling logic */ }) todo: William
        }

        // Act and Assert: Check if the button with the tag "suggestionButtonExists" is present and displayed
        composeTestRule
            .onNodeWithTag("suggestionButtonExists")
            .assertIsDisplayed()
    }

    // Add more tests as needed for other functionalities...
}

//todo: see OverviewTest.kt and TripNavigationTest.kt