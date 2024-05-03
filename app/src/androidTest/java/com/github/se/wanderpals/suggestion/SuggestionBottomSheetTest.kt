package com.github.se.wanderpals.suggestion

import androidx.compose.ui.test.assertIsDisplayed
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
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionBottomSheet
import com.github.se.wanderpals.ui.screens.trip.Suggestion
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

/** Mock SuggestionsViewModelSheetTest class to test the SuggestionBottomSheet. */
class SuggestionsViewModelSheetTest(testSuggestions: List<Suggestion>) :
    SuggestionsViewModel(TripsRepository("userid", Dispatchers.IO), "") {
  private val _state = MutableStateFlow(testSuggestions)
  override val state: StateFlow<List<Suggestion>> = _state

  private val _isLoading = MutableStateFlow(true)
  override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _bottomSheetVisible = MutableStateFlow(false)
  override val bottomSheetVisible: StateFlow<Boolean> = _bottomSheetVisible.asStateFlow()

  // State flow to remember the comment that is being interacted with
  private val _selectedSuggestion = MutableStateFlow<Suggestion?>(null)
  override val selectedSuggestion: StateFlow<Suggestion?> = _selectedSuggestion.asStateFlow()

  // State flow to handle the displaying of the delete dialog
  private val _showDeleteDialog = MutableStateFlow(false)
  override val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

  override fun deleteSuggestion(suggestion: Suggestion) {
    _state.value = _state.value.filter { it.suggestionId != suggestion.suggestionId }
  }

  override fun showSuggestionBottomSheet(suggestion: Suggestion) {
    viewModelScope.launch {
      _bottomSheetVisible.value = true
      _selectedSuggestion.value = suggestion
    }
  }

  // Mock hideBottomSheet
  override fun hideBottomSheet() {
    viewModelScope.launch {
      _bottomSheetVisible.value = false
      _selectedSuggestion.value = null
    }
  }

  override fun showDeleteDialog() {
    _showDeleteDialog.value = true
  }

  override fun hideDeleteDialog() {
    _showDeleteDialog.value = false
  }

  override fun confirmDeleteSuggestion(suggestion: Suggestion) {
    deleteSuggestion(suggestion) // Assuming deleteComment handles all necessary logic
    hideDeleteDialog()
    hideBottomSheet()
  }

  override fun transformToStop(suggestion: Suggestion) {
    hideBottomSheet()
  }

  init {
    _isLoading.value = false
  }

  override fun loadSuggestion(tripId: String) {
    _isLoading.value = false
  }
}

@RunWith(AndroidJUnit4::class)
class SuggestionBottomSheetTest {
  private val mockSuggestion =
      Suggestion(
          suggestionId = "1",
          userId = "1",
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
                      commentId = "1",
                      userId = "user1",
                      userName = "Jane Doe",
                      text = "This is a comment",
                      createdAt = LocalDate.now(),
                      createdAtTime = LocalTime.now()),
              ),
          userLikes = listOf("1"))

  @get:Rule val composeTestRule = createComposeRule()

  @RelaxedMockK private lateinit var mockNavActions: NavigationActions

  @Before
  fun setUp() {
    mockNavActions = mockk()

    // Set the user session for the test as ADMIN by default
    SessionManager.setUserSession(userId = "userid", role = Role.ADMIN)
  }

  @Test
  fun testSuggestionBottomSheetDeletes() {
    val viewModel = SuggestionsViewModelSheetTest(listOf(mockSuggestion))

    // Launch the composable with the view model
    composeTestRule.setContent {
      Suggestion(oldNavActions = mockNavActions, tripId = "", suggestionsViewModel = viewModel) {}
    }

    // Verify that the bottom sheet is not visible initially
    composeTestRule
        .onNodeWithTag("suggestionBottomSheet", useUnmergedTree = true)
        .assertDoesNotExist()
    composeTestRule
        .onNodeWithTag("suggestionOptionIcon1", useUnmergedTree = true)
        .assertExists()
        .performClick()
    composeTestRule
        .onNodeWithTag("suggestion1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()

    viewModel.showSuggestionBottomSheet(mockSuggestion)

    composeTestRule.onNodeWithTag("suggestionBottomSheet", useUnmergedTree = true).assertExists()

    composeTestRule
        .onNodeWithTag("deleteSuggestionOption", useUnmergedTree = true)
        .assertExists()
        .performClick()
    composeTestRule
        .onNodeWithTag("confirmDeleteSuggestionButton", useUnmergedTree = true)
        .assertExists()
        .performClick()

    // Verify that the bottom sheet is not visible
    composeTestRule
        .onNodeWithTag("suggestionBottomSheet", useUnmergedTree = true)
        .assertDoesNotExist()
    composeTestRule.onNodeWithTag("suggestion1", useUnmergedTree = true).assertDoesNotExist()

    composeTestRule.onNodeWithTag("suggestionFeedScreen", useUnmergedTree = true).performClick()
  }

  @Test
  fun testSuggestionBottomSheetCancel() {
    val viewModel = SuggestionsViewModelSheetTest(listOf(mockSuggestion))

    // Launch the composable with the view model
    composeTestRule.setContent {
      Suggestion(oldNavActions = mockNavActions, tripId = "", suggestionsViewModel = viewModel) {}
    }

    // Verify that the bottom sheet is not visible initially
    composeTestRule
        .onNodeWithTag("suggestionBottomSheet", useUnmergedTree = true)
        .assertDoesNotExist()
    composeTestRule
        .onNodeWithTag("suggestionOptionIcon1", useUnmergedTree = true)
        .assertExists()
        .performClick()
    composeTestRule
        .onNodeWithTag("suggestion1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()

    viewModel.showSuggestionBottomSheet(mockSuggestion)

    composeTestRule.onNodeWithTag("suggestionBottomSheet", useUnmergedTree = true).assertExists()

    composeTestRule
        .onNodeWithTag("deleteSuggestionOption", useUnmergedTree = true)
        .assertExists()
        .performClick()
    composeTestRule
        .onNodeWithTag("cancelDeleteSuggestionButton", useUnmergedTree = true)
        .assertExists()
        .performClick()
    composeTestRule.onNodeWithTag("suggestion1", useUnmergedTree = true).assertExists()
  }

  @Test
  fun testSuggestionBottomSheetEdit() {
    val viewModel = SuggestionsViewModelSheetTest(listOf(mockSuggestion))

    // Launch the composable with the view model
    composeTestRule.setContent { SuggestionBottomSheet(viewModel) }

    viewModel.showSuggestionBottomSheet(mockSuggestion)

    composeTestRule
        .onNodeWithTag("editSuggestionOption", useUnmergedTree = true)
        .assertIsDisplayed()
        .performClick()

    composeTestRule
        .onNodeWithTag("suggestionBottomSheet", useUnmergedTree = true)
        .assertDoesNotExist()
  }

  @Test
  fun testSuggestionBottomSheetTransform() {
    val viewModel = SuggestionsViewModelSheetTest(listOf(mockSuggestion))

    // Launch the composable with the view model
    composeTestRule.setContent { SuggestionBottomSheet(viewModel) }

    viewModel.showSuggestionBottomSheet(mockSuggestion)

    composeTestRule
        .onNodeWithTag("transformSuggestionOption", useUnmergedTree = true)
        .assertIsDisplayed()
        .performClick()

    composeTestRule
        .onNodeWithTag("suggestionBottomSheet", useUnmergedTree = true)
        .assertDoesNotExist()
  }
}
