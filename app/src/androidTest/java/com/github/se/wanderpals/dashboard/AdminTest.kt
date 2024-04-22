package com.github.se.wanderpals.dashboard

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.AdminViewModel
import com.github.se.wanderpals.screens.AdminScreen
import com.github.se.wanderpals.ui.screens.Admin
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class FakeAdminViewModel :
    AdminViewModel(tripsRepository = TripsRepository("", Dispatchers.IO), "") {

  private val user1: User =
      User(
          "user1",
          "Kennedy",
          "jack.kennedy@gov.us",
          "jacky",
          Role.MEMBER,
          GeoCords(0.0, 0.0),
      )
  private val user2: User =
      User("user2", "Johnson", "Johnson2@epfl.ch", "john", Role.VIEWER, GeoCords(0.0, 0.0))

  // create a fake Admin ViewModel
  override var listOfUsers = MutableStateFlow(listOf(user1, user2))

  override fun deleteUser(userId: String) {}

  override fun modifyUser(user: User) {}

  override fun getUsers() {}
}

@RunWith(AndroidJUnit4::class)
class AdminTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @Before
  fun setUp() {
    val fakeAdminViewModel = FakeAdminViewModel()
    composeTestRule.setContent { Admin(adminViewModel = fakeAdminViewModel) }
  }

  @Test
  fun mainComponentAreDisplayed() = run {
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      adminScreenCard { assertIsDisplayed() }
      IconAdminScreen { assertIsDisplayed() }
      AdminTitle { assertIsDisplayed() }
      AdminDivider { assertIsDisplayed() }
      // deleteUserButton { performClick() }
    }
  }

  @Test
  fun userNameDisplay() = run {
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) { userName { assertIsDisplayed() } }
  }
  // test case 3: check if the edit role button is displayed
  @Test
  fun editRoleButtonIsDisplayed() = run {
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      editRoleButton { assertIsDisplayed() }
    }
  }
  // test case 4: check if the delete user button is displayed
  @Test
  fun deleteUserButtonIsDisplayed() = run {
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      deleteUserButton { assertIsDisplayed() }
    }
  }
  // test case 5: check if the confirm delete user button is displayed
  @Test
  fun confirmDeleteUserButtonIsDisplayed() = run {
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      deleteUserButton { performClick() }
      confirmDeleteUserButton { assertIsDisplayed() }
    }
  }
  // test case 6: check if the cancel delete user button is displayed
  @Test
  fun cancelDeleteCommentButtonIsDisplayed() = run {
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      deleteUserButton { performClick() }
      cancelDeleteCommentButton { assertIsDisplayed() }
    }
  }
  // test case 7: check if the change role title is displayed
  @Test
  fun changeRoleTitleIsDisplayed() = run {
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      editRoleButton { performClick() }
      changeRoleTitle { assertIsDisplayed() }
    }
  }
  // test case 8: check if the radio button is displayed
  @Test
  fun radioButtonIsDisplayed() = run {
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      editRoleButton { performClick() }
      composeTestRule.onNodeWithText("MEMBER").performClick()
    }
  }
  // test case 9: check if the string role is displayed
  @Test
  fun stringRoleIsDisplayed() = run {
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      editRoleButton { performClick() }
      composeTestRule.onNodeWithText("MEMBER").isDisplayed()
      composeTestRule.onNodeWithText("VIEWER").isDisplayed()
      composeTestRule.onNodeWithText("ADMIN").isDisplayed()
      composeTestRule.onNodeWithText("OWNER").isDisplayed()
    }
  }
  // test case 10: check if the confirm role change button is displayed
  @Test
  fun confirmRoleChangeButtonIsDisplayed() = run {
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      editRoleButton { performClick() }
      ConfirmRoleChangeButton { assertIsDisplayed() }
    }
  }
  // test case 11: check if the confirm role change button is enabled
  @Test
  fun confirmRoleChangeButtonIsEnabled() = run {
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      editRoleButton { performClick() }
      ConfirmRoleChangeButton { assertIsEnabled() }
    }
  }
}
