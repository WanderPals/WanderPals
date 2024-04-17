import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SuggestionDetailTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var viewModel: SuggestionsViewModel
  private lateinit var navActions: NavigationActions

  @Before
  fun setup() {
    // Mocking the ViewModel and NavigationActions
    viewModel = mockk(relaxed = true)
    navActions = mockk(relaxed = true)

    // Set up the UI for tests
    composeTestRule.setContent {
      SuggestionDetail(suggestionId = "sugg1", viewModel = viewModel, navActions = navActions)
    }
  }

  @Test
  fun suggestionDetail_displaysTitleAndDescription() {
    composeTestRule.onNodeWithTag("suggestionTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("suggestionDescription").assertIsDisplayed()
  }

  @Test
  fun backButton_click_callsNavigationBack() {
    composeTestRule.onNodeWithTag("backButton").performClick()
    verify { navActions.goBack() }
  }

  @Test
  fun noComments_displaysNoCommentsMessage() {
    composeTestRule.onNodeWithTag("noCommentsMessage").assertIsDisplayed()
  }
}
