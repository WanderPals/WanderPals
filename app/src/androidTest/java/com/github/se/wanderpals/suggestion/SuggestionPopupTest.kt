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

    // Initialize the suggestion list with dummy data:
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
                "suggestionId4",
                "userId4",
                "userName4",
                "This is a great place to visit. Let us go here together! I am sure you will love it! I have been there before and it was amazing! " +
                    "Trying to convince you to go here with me. coz I know you will love it!",
                LocalDate.of(2024, 9, 29),
                stop4,
                dummyCommentList4,
                userLikes4))
  }

  /**
   * Test that the suggestion details popup of the first suggestion displays correctly when comments
   * are present. Note that the address and website are tested in separate tests.
   */
  @Test
  fun suggestionDetailPopup_displaysCommentsCorrectly_whenCommentsArePresent() {
    composeTestRule.setContent {
      SuggestionDetailPopup(
          suggestion = suggestionList[0],
          comments = commentList,
          viewModel = FakeSuggestionsViewModel(),
          onDismiss = {},
          onLikeClicked = {})
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
   * comments are not present. Note that the address and website are tested in separate tests.
   */
  @Test
  fun suggestionDetailPopup_displaysCorrectly_whenCommentsAreNotPresent() {
    composeTestRule.setContent {
      SuggestionDetailPopup(
          suggestion = suggestionList[1],
          comments = emptyList(),
          viewModel = FakeSuggestionsViewModel(),
          onDismiss = {},
          onLikeClicked = {})
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
      SuggestionDetailPopup(
          suggestion = suggestionList[0],
          comments = commentList,
          viewModel = FakeSuggestionsViewModel(),
          onDismiss = {},
          onLikeClicked = {})
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
      SuggestionDetailPopup(
          suggestion = suggestionList[3],
          comments = commentList,
          viewModel = FakeSuggestionsViewModel(),
          onDismiss = {},
          onLikeClicked = {})
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
          suggestionList = suggestionList,
          searchSuggestionText = "",
          tripId = "dummyTestTripId",
          suggestionRepository = FakeSuggestionsViewModel())
    }

    // Simulate a click on the first SuggestionItem for testing purpose
    onComposeScreen<SuggestionPopupScreen>(composeTestRule) { suggestion1.performClick() }

    // Verify SuggestionDetailPopup is displayed
    onComposeScreen<SuggestionPopupScreen>(composeTestRule) {
      suggestionPopupScreen.assertIsDisplayed()
    }
  }
}
