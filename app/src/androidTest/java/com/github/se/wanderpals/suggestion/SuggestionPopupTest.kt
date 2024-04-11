package com.github.se.wanderpals.suggestion

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionDetailPopup
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
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

  @Test
  fun suggestionCommentDisplaysProperly() {
    composeTestRule.setContent {
      SuggestionDetailPopup(
          suggestion = suggestionList[0],
          comments = commentList,
          onDismiss = {},
          isLiked = false,
          onLikeClicked = {},
          onComment = { it -> },
          likesCount = 0)
    }
    composeTestRule
        .onNodeWithTag("commentUserNamecommentId1", useUnmergedTree = true)
        .assertExists()
        .assertTextContains("userNamecmt1")
    composeTestRule
        .onNodeWithTag("commentUserNamecommentId2", useUnmergedTree = true)
        .assertExists()
        .assertTextContains("userNamecmt2")
    composeTestRule
        .onNodeWithTag("commentUserNamecommentId3", useUnmergedTree = true)
        .assertExists()
        .assertTextContains("userNamecmt3")
    composeTestRule
        .onNodeWithTag("commentUserNamecommentId4", useUnmergedTree = true)
        .assertExists()
        .assertTextContains("userNamecmt4")

    composeTestRule
        .onNodeWithTag("commentCreatedAtcommentId1", useUnmergedTree = true)
        .assertExists()
    composeTestRule
        .onNodeWithTag("commentCreatedAtcommentId2", useUnmergedTree = true)
        .assertExists()
    composeTestRule
        .onNodeWithTag("commentCreatedAtcommentId3", useUnmergedTree = true)
        .assertExists()
    composeTestRule
        .onNodeWithTag("commentCreatedAtcommentId4", useUnmergedTree = true)
        .assertExists()

    composeTestRule.onNodeWithTag("commentDividercommentId1", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("commentDividercommentId2", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("commentDividercommentId3", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("commentDividercommentId4", useUnmergedTree = true).assertExists()

    composeTestRule
        .onNodeWithTag("commentTextcommentId1", useUnmergedTree = true)
        .assertExists()
        .assertTextContains("Great idea!")
    composeTestRule
        .onNodeWithTag("commentTextcommentId2", useUnmergedTree = true)
        .assertExists()
        .assertTextContains("I've been here before, it's wonderful.")
    composeTestRule
        .onNodeWithTag("commentTextcommentId3", useUnmergedTree = true)
        .assertExists()
        .assertTextContains("This fits our schedule.")
    composeTestRule
        .onNodeWithTag("commentTextcommentId4", useUnmergedTree = true)
        .assertExists()
        .assertTextContains("This place seems great.")

    composeTestRule
        .onNodeWithTag("suggestionPopupCommentTextField", useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("suggestionPopupSendCommentButton", useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("suggestionPopupDivider", useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("noSuggestionCommentList", useUnmergedTree = true)
        .assertDoesNotExist()

    composeTestRule.onNodeWithTag("suggestionPopupDivider0", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("suggestionPopupDivider1", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("suggestionPopupDivider2", useUnmergedTree = true).assertExists()
    composeTestRule
        .onNodeWithTag("suggestionPopupDivider3", useUnmergedTree = true)
        .assertDoesNotExist()
  }

  @Test
  fun suggestionNoCommentDisplaysProperly() {
    composeTestRule.setContent {
      SuggestionDetailPopup(
          suggestion = suggestionList[0],
          comments = emptyList(),
          onDismiss = {},
          isLiked = false,
          onLikeClicked = {},
          onComment = { it -> },
          likesCount = 0)
    }

    composeTestRule
        .onNodeWithTag("noSuggestionCommentList", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
  }
}
