package com.github.se.wanderpals.suggestion

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionDetail
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Define a fake suggestion view model class for testing purposes
class FakeViewModel(testSuggestions: List<Suggestion>) :
    SuggestionsViewModel(TripsRepository("userid", Dispatchers.IO), "") {
  private val _state = MutableStateFlow(emptyList<Suggestion>())
  override val state: StateFlow<List<Suggestion>> = _state

  private val _isLoading = MutableStateFlow(true)
  override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private fun updateSuggestionList(suggestions: List<Suggestion>) {
    _state.value = suggestions
    _isLoading.value = false
  }

  init {
    updateSuggestionList(testSuggestions)
  }

  override fun getIsLiked(suggestionId: String): Boolean {
    // Check whether the uid is present in the list of likes
    return _state.value.find { it.suggestionId == suggestionId }?.userLikes?.contains("userid")
        ?: false
  }

  override fun getNbrLiked(suggestionId: String): Int {
    return _state.value.find { it.suggestionId == suggestionId }?.userLikes?.size ?: 0
  }

  // Mock toggleLikeSuggestion function
  override fun toggleLikeSuggestion(suggestion: Suggestion) {
    // just add the user to the list of likes
    val newSuggestion = suggestion.copy(userLikes = suggestion.userLikes + "userid")
    updateSuggestionList(
        _state.value.map { if (it.suggestionId == suggestion.suggestionId) newSuggestion else it })
  }

  // Mock AddComment function
  override fun addComment(suggestion: Suggestion, comment: Comment) {
    // Just add the comment to the list of comments of the suggestion and update the state with the
    // new suggestion
    // modify the comment to change the commentId
    val newSuggestion =
        suggestion.copy(
            comments =
                suggestion.comments +
                    comment.copy(userId = "userid", userName = "John Doe", commentId = "comment2"))
    updateSuggestionList(
        _state.value.map { if (it.suggestionId == suggestion.suggestionId) newSuggestion else it })
  }
}

@RunWith(AndroidJUnit4::class)
class SuggestionDetailTest {

  private val mockSuggestion =
      Suggestion(
          suggestionId = "suggestion1",
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
          userLikes = listOf("user1"))

  @get:Rule val composeTestRule = createComposeRule()

  @RelaxedMockK private lateinit var mockNavActions: NavigationActions

  @Before
  fun setUp() {
    mockNavActions = mockk()
  }

  @Test
  fun testSuggestionDetailsVisible() {
    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = FakeViewModel(listOf(mockSuggestion)),
          navActions = mockNavActions)
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("SuggestionTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("CreatedByText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DescriptionText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AddressText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("WebsiteText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ScheduleText").assertIsDisplayed()
  }

  @Test
  fun testSuggestionDetailIconsAreVisible() {
    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = FakeViewModel(listOf(mockSuggestion)),
          navActions = mockNavActions)
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("LikeButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("CommentButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("BackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("LocationIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("WebsiteIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SendButton").assertIsDisplayed()
  }

  @Test
  fun testSuggestionDetailCommentsAreVisible() {
    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = FakeViewModel(listOf(mockSuggestion)),
          navActions = mockNavActions)
    }

    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag("commentUserNamecomment1", useUnmergedTree = true)
        .assertExists()
        .assertTextEquals("Jane Doe")
  }

  @Test
  fun testNoCommentsMessageIsDisplayedWhenNoCommentsPresent() {
    // Prepare a mock suggestion with no comments
    val mockSuggestionNoComments = mockSuggestion.copy(comments = emptyList())

    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestionNoComments.suggestionId,
          viewModel = FakeViewModel(listOf(mockSuggestionNoComments)),
          navActions = mockNavActions)
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("NoCommentsMessage").assertIsDisplayed()
  }

  @Test
  fun testAddCommentButtonIsVisible() {
    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = FakeViewModel(listOf(mockSuggestion)),
          navActions = mockNavActions)
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("NewCommentInput").assertIsDisplayed()
  }

  // Test that likes are incremented when the like button is clicked
  @Test
  fun testLikeButtonIncrementsLikes() {
    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = FakeViewModel(listOf(mockSuggestion)),
          navActions = mockNavActions)
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("LikeButton").performClick()

    composeTestRule.onNodeWithTag("LikesCount").assertTextEquals("2")
  }

  // Test that the comment is added when the send button is clicked
  @Test
  fun testAddComment() {
    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = FakeViewModel(listOf(mockSuggestion)),
          navActions = mockNavActions)
    }

    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag("NewCommentInput")
        .performClick()
        .performTextInput("This is a new comment")

    composeTestRule.onNodeWithTag("SendButton").performClick()

    composeTestRule.onNodeWithTag("commentUserNamecomment2", useUnmergedTree = true).assertExists()
  }
}
