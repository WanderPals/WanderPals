package com.github.se.wanderpals.suggestion

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.service.SessionManager
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
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Define a fake suggestion view model class for testing purposes
class FakeViewModelBottomSheetOptions(testSuggestions: List<Suggestion>) :
    SuggestionsViewModel(TripsRepository("userid", Dispatchers.IO), "") {
  private val _state = MutableStateFlow(emptyList<Suggestion>())
  override val state: StateFlow<List<Suggestion>> = _state

  private val _isLoading = MutableStateFlow(true)
  override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  // State flow to handle the displaying of the bottom sheet
  private val _bottomSheetVisible = MutableStateFlow(false)
  override val bottomSheetVisible: StateFlow<Boolean> = _bottomSheetVisible.asStateFlow()

  // State flow to remember the comment that is being interacted with
  private val _selectedComment = MutableStateFlow<Comment?>(null)
  override val selectedComment: StateFlow<Comment?> = _selectedComment.asStateFlow()

  private val _editingComment = MutableStateFlow(false)
  override val editingComment: StateFlow<Boolean> = _editingComment.asStateFlow()

  // State flow to handle the displaying of the delete dialog
  private val _showDeleteDialog = MutableStateFlow(false)
  override val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

  override val selectedSuggestion = MutableStateFlow(testSuggestions.first())

  private fun updateSuggestionList(suggestions: List<Suggestion>) {
    _state.value = suggestions
    _isLoading.value = false
  }

  init {
    updateSuggestionList(testSuggestions)
  }

  // Mock addComment function
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

  // Mock deleteComment function
  override fun deleteComment(suggestion: Suggestion) {
    // Just remove the comment from the list of comments of the suggestion and update the state with
    // the
    // new suggestion
    val newSuggestion =
        suggestion.copy(
            comments =
                suggestion.comments.filter { it.commentId != selectedComment.value?.commentId })
    updateSuggestionList(
        _state.value.map { if (it.suggestionId == suggestion.suggestionId) newSuggestion else it })
    _selectedComment.value = null
    if (selectedSuggestion.value.suggestionId == suggestion.suggestionId) {
      selectedSuggestion.value = newSuggestion
    }
    hideBottomSheet()
  }

  // Mock showBottomSheet
  override fun showBottomSheet(comment: Comment) {
    viewModelScope.launch {
      _bottomSheetVisible.value = true
      _selectedComment.value = comment
    }
  }

  // Mock hideBottomSheet
  override fun hideBottomSheet() {
    viewModelScope.launch {
      _bottomSheetVisible.value = false
      _selectedComment.value = null
    }
  }

  override fun showDeleteDialog() {
    _showDeleteDialog.value = true
  }

  override fun hideDeleteDialog() {
    _showDeleteDialog.value = false
  }

  override fun confirmDeleteComment(suggestion: Suggestion) {
    deleteComment(suggestion) // Assuming deleteComment handles all necessary logic
    hideDeleteDialog()
    hideBottomSheet()
  }

  override fun editCommentOption() {
    _editingComment.value = true
    _bottomSheetVisible.value = false
  }
}

@RunWith(AndroidJUnit4::class)
class CommentOptionsBottomSheetTest {

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
                      createdAtTime = LocalTime.now()),
              ),
          userLikes = listOf("user1"))

  @get:Rule val composeTestRule = createComposeRule()

  @RelaxedMockK private lateinit var mockNavActions: NavigationActions

  @Before
  fun setUp() {
    mockNavActions = mockk()

    // Set the user session for the test as ADMIN by default
    SessionManager.setUserSession(userId = "userid", role = Role.ADMIN)
  }

  // Add a test that checks the bottom sheet is indeed displayed when the comment 3 dot option icon
  // is clicked
  @Test
  fun testCommentOptionsBottomSheetVisible() {
    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = FakeViewModelBottomSheetOptions(listOf(mockSuggestion)),
          navActions = mockNavActions)
    }

    // Click on the comment 3 dot option icon
    composeTestRule
        .onNodeWithTag("commentOptionsIconcomment1", useUnmergedTree = true)
        .performClick()

    // Check if the bottom sheet is displayed
    composeTestRule.onNodeWithTag("commentBottomSheet").assertIsDisplayed()
  }

  // Add a test that checks the delete comment option is displayed in the bottom sheet
  @Test
  fun testDeleteCommentOptionVisible() {
    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = FakeViewModelBottomSheetOptions(listOf(mockSuggestion)),
          navActions = mockNavActions)
    }

    // Click on the comment 3 dot option icon
    composeTestRule
        .onNodeWithTag("commentOptionsIconcomment1", useUnmergedTree = true)
        .performClick()

    // Check if the delete comment option is displayed
    composeTestRule.onNodeWithTag("deleteCommentOption").assertIsDisplayed()
  }

  // Add a test that checks the delete comment option deletes the comment when clicked
  @Test
  fun testDeleteCommentOptionDeletesComment() {
    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = FakeViewModelBottomSheetOptions(listOf(mockSuggestion)),
          navActions = mockNavActions)
    }

    // Click on the comment 3 dot option icon
    composeTestRule
        .onNodeWithTag("commentOptionsIconcomment1", useUnmergedTree = true)
        .performClick()

    // Click on the delete comment option
    composeTestRule.onNodeWithTag("deleteCommentOption").performClick()

    // Click on the confirm delete comment button
    composeTestRule.onNodeWithTag("confirmDeleteCommentButton").performClick()

    // Check if the comment is deleted
    composeTestRule.onNodeWithTag("comment1").assertDoesNotExist()
  }

  // Add a test that checks the delete comment option deletes the comment when clicked
  @Test
  fun testDeleteCommentOptionDeletesCommentNotInOffline() {
    SessionManager.setIsNetworkAvailable(false)
    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = FakeViewModelBottomSheetOptions(listOf(mockSuggestion)),
          navActions = mockNavActions)
    }

    // Click on the comment 3 dot option icon
    composeTestRule
        .onNodeWithTag("commentOptionsIconcomment1", useUnmergedTree = true)
        .performClick()

    // Click on the delete comment option
    composeTestRule.onNodeWithTag("deleteCommentOption").performClick()

    // Click on the confirm delete comment button
    composeTestRule.onNodeWithTag("confirmDeleteCommentButton").performClick()

    // Check if the comment is deleted
    composeTestRule.onNodeWithTag("comment1").assertExists()
    SessionManager.setIsNetworkAvailable(true)
  }

  // Add a test that checks the bottom sheet is hidden when the delete comment option is clicked
  @Test
  fun testDeleteCommentOptionHidesBottomSheet() {
    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = FakeViewModelBottomSheetOptions(listOf(mockSuggestion)),
          navActions = mockNavActions)
    }

    // Click on the comment 3 dot option icon
    composeTestRule
        .onNodeWithTag("commentOptionsIconcomment1", useUnmergedTree = true)
        .performClick()

    // Click on the delete comment option
    composeTestRule.onNodeWithTag("deleteCommentOption").performClick()

    // Click on the confirm delete comment button
    composeTestRule.onNodeWithTag("confirmDeleteCommentButton").performClick()

    // Check if the bottom sheet is hidden
    composeTestRule.onNodeWithTag("commentBottomSheet").assertDoesNotExist()
  }

  // Add a test that checks the bottom sheet is hidden when the the user taps somewhere outside the
  // bottom sheet
  @Test
  fun testBottomSheetHidesWhenDismissed() {
    val viewModel = FakeViewModelBottomSheetOptions(listOf(mockSuggestion))

    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = viewModel,
          navActions = mockNavActions)
    }

    // Open the bottom sheet
    viewModel.showBottomSheet(mockSuggestion.comments.first())

    composeTestRule.onNodeWithTag("commentBottomSheet").assertExists()

    // Simulate the logic that would be executed on tapping outside
    viewModel.hideBottomSheet()

    composeTestRule.waitForIdle()

    // Assert that the bottom sheet is no longer visible
    composeTestRule.onNodeWithTag("commentBottomSheet").assertDoesNotExist()
  }

  // Add a test that checks the delete dialog is displayed when the delete comment option is clicked
  @Test
  fun testDeleteDialogVisible() {
    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = FakeViewModelBottomSheetOptions(listOf(mockSuggestion)),
          navActions = mockNavActions)
    }

    // Click on the comment 3 dot option icon
    composeTestRule
        .onNodeWithTag("commentOptionsIconcomment1", useUnmergedTree = true)
        .performClick()

    // Click on the delete comment option
    composeTestRule.onNodeWithTag("deleteCommentOption").performClick()

    // Check if the delete dialog is displayed
    composeTestRule.onNodeWithTag("deleteCommentDialog").assertIsDisplayed()
  }

  // Add a test that checks the delete dialog is hidden when the cancel button is clicked
  @Test
  fun testDeleteDialogHidesWhenCanceled() {
    val viewModel = FakeViewModelBottomSheetOptions(listOf(mockSuggestion))

    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = viewModel,
          navActions = mockNavActions)
    }

    // Click on the comment 3 dot option icon
    composeTestRule
        .onNodeWithTag("commentOptionsIconcomment1", useUnmergedTree = true)
        .performClick()

    // Click on the delete comment option
    composeTestRule.onNodeWithTag("deleteCommentOption").performClick()

    // Click on the cancel delete comment button
    composeTestRule.onNodeWithTag("cancelDeleteCommentButton").performClick()

    // Check if the delete dialog is hidden
    composeTestRule.onNodeWithTag("deleteCommentDialog").assertDoesNotExist()
  }

  // Add a test that checks the delete dialog is hidden when the confirm button is clicked
  @Test
  fun testDeleteDialogHidesWhenConfirmed() {
    val viewModel = FakeViewModelBottomSheetOptions(listOf(mockSuggestion))

    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = viewModel,
          navActions = mockNavActions)
    }

    // Click on the comment 3 dot option icon
    composeTestRule
        .onNodeWithTag("commentOptionsIconcomment1", useUnmergedTree = true)
        .performClick()

    // Click on the delete comment option
    composeTestRule.onNodeWithTag("deleteCommentOption").performClick()

    // Click on the confirm delete comment button
    composeTestRule.onNodeWithTag("confirmDeleteCommentButton").performClick()

    // Check if the delete dialog is hidden
    composeTestRule.onNodeWithTag("deleteCommentDialog").assertDoesNotExist()
  }

  // Add a test that checks that if the user doesn't have the permission the delete comment option
  // is not displayed
  @Test
  fun testDeleteCommentOptionNotVisibleForOtherUserNonAdmin() {
    SessionManager.setUserSession(
        // use a different user id to simulate a different user
        userId = "userid2",
        // must not be an admin
        role = Role.VIEWER)

    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = FakeViewModelBottomSheetOptions(listOf(mockSuggestion)),
          navActions = mockNavActions)
    }

    // Click on the comment 3 dot option icon
    composeTestRule
        .onNodeWithTag("commentOptionsIconcomment1", useUnmergedTree = true)
        .performClick()

    // Check if the delete comment option is not displayed
    composeTestRule.onNodeWithTag("deleteCommentOption").assertDoesNotExist()
  }

  // Add a test that checks that if the user is the owner of the comment the delete comment option
  // is displayed
  @Test
  fun testDeleteCommentOptionVisibleForOwner() {
    SessionManager.setUserSession(
        // use the same user id as the comment owner
        userId = "user1",
        // must not be an admin
        role = Role.VIEWER)

    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = FakeViewModelBottomSheetOptions(listOf(mockSuggestion)),
          navActions = mockNavActions)
    }

    // Click on the comment 3 dot option icon
    composeTestRule
        .onNodeWithTag("commentOptionsIconcomment1", useUnmergedTree = true)
        .performClick()

    // Check if the delete comment option is displayed
    composeTestRule.onNodeWithTag("deleteCommentOption").assertIsDisplayed()
  }

  // Add a test that checks that if the user is an admin the delete comment option is displayed
  @Test
  fun testDeleteCommentOptionVisibleForAdmin() {
    SessionManager.setUserSession(
        // use a different user id to simulate a different user
        userId = "userid2",
        // must be an admin
        role = Role.ADMIN)

    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = FakeViewModelBottomSheetOptions(listOf(mockSuggestion)),
          navActions = mockNavActions)
    }

    // Click on the comment 3 dot option icon
    composeTestRule
        .onNodeWithTag("commentOptionsIconcomment1", useUnmergedTree = true)
        .performClick()

    // Check if the delete comment option is displayed
    composeTestRule.onNodeWithTag("deleteCommentOption").assertIsDisplayed()
  }

  @Test
  fun testEditCommentOptionNotVisibleOffline() {
    SessionManager.setUserSession(
        // use the same user id as the comment owner
        userId = "user1",
        // must not be an admin
        role = Role.MEMBER)
    SessionManager.setIsNetworkAvailable(false)

    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = FakeViewModelBottomSheetOptions(listOf(mockSuggestion)),
          navActions = mockNavActions)
    }

    // Click on the comment 3 dot option icon
    composeTestRule
        .onNodeWithTag("commentOptionsIconcomment1", useUnmergedTree = true)
        .performClick()

    // Check if the edit comment option is displayed
    composeTestRule.onNodeWithTag("editCommentOption").assertIsNotDisplayed()
    SessionManager.setIsNetworkAvailable(true)
  }

  @Test
  fun testEditCommentOptionVisibleForOwner() {
    SessionManager.setUserSession(
        // use the same user id as the comment owner
        userId = "user1",
        // must not be an admin
        role = Role.MEMBER)

    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = FakeViewModelBottomSheetOptions(listOf(mockSuggestion)),
          navActions = mockNavActions)
    }

    // Click on the comment 3 dot option icon
    composeTestRule
        .onNodeWithTag("commentOptionsIconcomment1", useUnmergedTree = true)
        .performClick()

    // Check if the edit comment option is displayed
    composeTestRule.onNodeWithTag("editCommentOption").assertIsDisplayed()
  }

  @Test
  fun testEditCommentOptionVisibleForOwnerAndDisabledOffline() {
    SessionManager.setIsNetworkAvailable(false)
    SessionManager.setUserSession(
        // use the same user id as the comment owner
        userId = "user1",
        // must not be an admin
        role = Role.MEMBER)

    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = FakeViewModelBottomSheetOptions(listOf(mockSuggestion)),
          navActions = mockNavActions)
    }

    // Click on the comment 3 dot option icon
    composeTestRule
        .onNodeWithTag("commentOptionsIconcomment1", useUnmergedTree = true)
        .performClick()

    // Check if the edit comment option is displayed
    composeTestRule.onNodeWithTag("editCommentOption").assertIsNotEnabled()
    SessionManager.setIsNetworkAvailable(true)
  }

  @Test
  fun testEditCommentOptionVisibleForAdmin() {
    SessionManager.setUserSession(
        // use a different user id to simulate a different user
        userId = "userid2",
        // must be an admin
        role = Role.ADMIN)

    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = FakeViewModelBottomSheetOptions(listOf(mockSuggestion)),
          navActions = mockNavActions)
    }

    // Click on the comment 3 dot option icon
    composeTestRule
        .onNodeWithTag("commentOptionsIconcomment1", useUnmergedTree = true)
        .performClick()

    // Check if the edit comment option is displayed
    composeTestRule.onNodeWithTag("editCommentOption").assertIsDisplayed()
  }

  @Test
  fun testEditCommentOptionNotVisibleForOtherUserNonAdmin() {
    SessionManager.setUserSession(
        // use a different user id to simulate a different user
        userId = "userid2",
        // must not be an admin
        role = Role.VIEWER)

    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = FakeViewModelBottomSheetOptions(listOf(mockSuggestion)),
          navActions = mockNavActions)
    }

    // Click on the comment 3 dot option icon
    composeTestRule
        .onNodeWithTag("commentOptionsIconcomment1", useUnmergedTree = true)
        .performClick()

    // Check if the edit comment option is not displayed
    composeTestRule.onNodeWithTag("editCommentOption").assertDoesNotExist()
  }

  @Test
  fun testEditCommentOptionChangesText() {
    val viewModel = FakeViewModelBottomSheetOptions(listOf(mockSuggestion))

    composeTestRule.setContent {
      SuggestionDetail(
          suggestionId = mockSuggestion.suggestionId,
          viewModel = viewModel,
          navActions = mockNavActions)
    }

    // Click on the comment 3 dot option icon
    composeTestRule
        .onNodeWithTag("commentOptionsIconcomment1", useUnmergedTree = true)
        .performClick()

    viewModel.showBottomSheet(mockSuggestion.comments.first())

    // Click on the edit comment option
    composeTestRule.onNodeWithTag("editCommentOption").performClick()

    composeTestRule.onNodeWithTag("NewCommentInput").assertTextContains("This is a comment")
  }
}
