package com.github.se.wanderpals.suggestion

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.screens.SuggestionPopupScreen
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionDetailPopup
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionFeedContent
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SuggestionPopupTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

    @get:Rule val composeTestRule = createComposeRule()
    private lateinit var suggestionList: List<Suggestion>
    private lateinit var commentList: List<Comment>

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
                website = "",
                imageUrl = "")
        val stop3 =
            Stop(
                stopId = "OSK003",
                title = "Umeda Sky Building",
                address = "",
                date = LocalDate.of(2024, 4, 11),
                startTime = LocalTime.of(10, 30), // Opens at 10:30 AM
                duration = 90, // 1.5 hours visit
                budget = 1500.0, // Entrance fee and other possible expenses
                description =
                "The Umeda Sky Building is a spectacular high rise building in the Kita district of Osaka, featuring a futuristic observatory, the Floating Garden.",
                geoCords = GeoCords(latitude = 34.705938, longitude = 135.490357),
                website = "http://www.kuchu-teien.com/",
                imageUrl = "")
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
        this.commentList = listOf(comment1, comment2, comment3, comment4)

        //    /*
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
                    commentList,
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
                    commentList,
                    emptyList()),
                Suggestion(
                    "suggestionId4",
                    "userId4",
                    "userName4",
                    "This is a great place to visit. Let us go here together! I am sure you will love it! I have been there before and it was amazing! " +
                            "Trying to convince you to go here with me. coz I know you will love it!",
                    LocalDate.of(2024, 3, 29),
                    stop4,
                    commentList,
                    emptyList()))
    }

    /**
     * Test that the suggestion details popup of the first suggestion displays correctly when comments
     * are present. Note that the address and website are tested in separate tests.
     */
    @Test
    fun suggestionDetailPopup_displaysCommentsCorrectly_whenCommentsArePresent() {
        composeTestRule.setContent {
            SuggestionDetailPopup(suggestion = suggestionList[0], comments = commentList, onDismiss = {})
        }

        onComposeScreen<SuggestionPopupScreen>(composeTestRule) {
            suggestionPopupTitle.assertIsDisplayed()
            suggestionPopupCommentsIcon.assertIsDisplayed()
            suggestionPopupLikesIcon.assertIsDisplayed()
            suggestionPopupUserName.assertIsDisplayed()
            suggestionPopupDate.assertIsDisplayed()

            suggestionPopupDescription.assertIsDisplayed()
            suggestionPopupDescriptionText.assertIsDisplayed()

            suggestionPopupStartDateTimeEndDateTime.assertIsDisplayed()

            suggestionPopupComments.assertIsDisplayed()
            // Verify the comments are displayed:
            // by checking the presence of each suggestionComment, we check the presence of the
            // commentList
            suggestionComment1.assertIsDisplayed()
            suggestionComment2.assertIsDisplayed()
            suggestionComment3.assertIsDisplayed()
            suggestionComment4.assertIsDisplayed()

            suggestionPopupDivider.assertIsDisplayed()
        }
    }

    /**
     * Test that the suggestion details popup of the second suggestion displays correctly when
     * comments are not present.
     * Note that the address and website are tested in separate tests.
     */
    @Test
    fun suggestionDetailPopup_displaysCorrectly_whenCommentsAreNotPresent() {
        composeTestRule.setContent {
            SuggestionDetailPopup(suggestion = suggestionList[1], comments = emptyList(), onDismiss = {})
        }

        onComposeScreen<SuggestionPopupScreen>(composeTestRule) {
            suggestionPopupTitle.assertIsDisplayed()
            suggestionPopupCommentsIcon.assertIsDisplayed()
            suggestionPopupLikesIcon.assertIsDisplayed()
            suggestionPopupUserName.assertIsDisplayed()
            suggestionPopupDate.assertIsDisplayed()

            suggestionPopupDescription.assertIsDisplayed()
            suggestionPopupDescriptionText.assertIsDisplayed()

            suggestionPopupStartDateTimeEndDateTime.assertIsDisplayed()

            suggestionPopupComments.assertIsDisplayed()
            // Verify the "No comments yet" message is displayed
            noSuggestionCommentList.assertIsDisplayed()
        }
    }

    /**
     * Test that the suggestion details popup of the first suggestion displays correctly the address
     * and website when both are present.
     */
    @Test
    fun suggestionDetailPopup_displaysAddrAndWebsiteCorrectly_whenAddrAndWebsiteArePresent() {
        composeTestRule.setContent {
            SuggestionDetailPopup(suggestion = suggestionList[0], comments = commentList, onDismiss = {})
        }

        onComposeScreen<SuggestionPopupScreen>(composeTestRule) {
            // Verify the address and website are present
            suggestionPopupAddrTextNotEmpty.assertIsDisplayed()
            suggestionPopupWebsiteTextNotEmpty.assertIsDisplayed()
        }
    }

    /**
     * Test that the suggestion details popup of the fourth suggestion displays correctly the address
     * and website when both are absent.
     */
    @Test
    fun suggestionDetailPopup_displaysAddrAndWebsiteCorrectly_whenAddrAndWebsiteAreAbsent() {
        composeTestRule.setContent {
            SuggestionDetailPopup(suggestion = suggestionList[3], comments = commentList, onDismiss = {})
        }

        onComposeScreen<SuggestionPopupScreen>(composeTestRule) {
            // Verify the address and website are absent
            suggestionPopupAddr.assertIsDisplayed()
            suggestionPopupAddrTextEmpty.assertIsDisplayed()
            suggestionPopupWebsite.assertIsDisplayed()
            suggestionPopupWebsiteTextEmpty.assertIsDisplayed()
        }
    }

    /**
     * Test when clicking on the suggestion details popup of the first suggestion (for testing
     * purpose), the popup of the clicked suggestion item is displayed, and the overlay effect is
     * working correctly.
     */
    @Test
    fun suggestionItem_click_displaysPopup_overlayEffect() {
        composeTestRule.setContent {
            SuggestionFeedContent(
                innerPadding = PaddingValues(),
                navigationActions = mockNavActions,
                suggestionList = suggestionList,
                searchSuggestionText = "")
        }

        // Simulate a click on the first SuggestionItem for testing purpose
        onComposeScreen<SuggestionPopupScreen>(composeTestRule) { suggestion1.performClick() }

        // Verify SuggestionDetailPopup is displayed
        onComposeScreen<SuggestionPopupScreen>(composeTestRule) {
            suggestionPopupScreen.assertIsDisplayed()
        }
    }
}
