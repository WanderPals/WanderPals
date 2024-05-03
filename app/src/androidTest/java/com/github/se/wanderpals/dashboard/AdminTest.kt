package com.github.se.wanderpals.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.AdminViewModel
import com.github.se.wanderpals.screens.AdminScreen
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.screens.Admin
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
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
          Role.ADMIN,
          GeoCords(0.0, 0.0),
      )
  private val user2: User =
      User("user2", "Johnson", "Johnson2@epfl.ch", "john", Role.OWNER, GeoCords(0.0, 0.0))

  private val user3 =
      User(
          userId = "3",
          name = "Kilo Yankee Sierra",
          email = "kilosierra@gmail.com",
          role = Role.MEMBER)

  // create a fake Admin ViewModel
  override var listOfUsers = MutableStateFlow(listOf(user1, user2, user3))
  override var currentUser = MutableStateFlow(SessionManager.getCurrentUser())

  override fun deleteUser(userId: String) {}

  override fun modifyUser(user: User) {}

  override fun getUsers() {}
}

@RunWith(AndroidJUnit4::class)
class AdminTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @Test
  fun mainComponentAreDisplayed() = run {
    val User1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          User1.userId, User1.name, User1.email, User1.role, profilePhoto = User1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel())
    }
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      adminScreenCard { assertIsDisplayed() }
      IconAdminScreen { assertIsDisplayed() }
      AdminTitle { assertIsDisplayed() }
      AdminDivider { assertIsDisplayed() }
    }
  }

  @Test
  fun userNameDisplay() = run {
    val User1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          User1.userId, User1.name, User1.email, User1.role, profilePhoto = User1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel())
    }
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) { userName { assertIsDisplayed() } }
  }
  // test case 3: check if the edit role button is displayed
  @Test
  fun editRoleButtonIsDisplayed() = run {
    val User1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          User1.userId, User1.name, User1.email, User1.role, profilePhoto = User1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel())
    }
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      editRoleButton { assertIsDisplayed() }
    }
  }
  // test case 4: check if the delete user button is displayed
  @Test
  fun deleteUserButtonIsDisplayed() = run {
    val User1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          User1.userId, User1.name, User1.email, User1.role, profilePhoto = User1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel())
    }
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      deleteUserButton { assertIsDisplayed() }
    }
  }
  // test case 5: check if the confirm delete user button is displayed
  @Test
  fun confirmDeleteUserButtonIsDisplayed() = run {
    val User1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          User1.userId, User1.name, User1.email, User1.role, profilePhoto = User1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel())
    }
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      deleteUserButton { performClick() }
      confirmDeleteUserButton { assertIsDisplayed() }
    }
  }
  // test case 6: check if the cancel delete user button is displayed
  @Test
  fun cancelDeleteCommentButtonIsDisplayed() = run {
    val User1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          User1.userId, User1.name, User1.email, User1.role, profilePhoto = User1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel())
    }
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      deleteUserButton { performClick() }
      cancelDeleteCommentButton { assertIsDisplayed() }
    }
  }
  // test case 7: check if the change role title is displayed
  @Test
  fun changeRoleTitleIsDisplayed() = run {
    val User1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          User1.userId, User1.name, User1.email, User1.role, profilePhoto = User1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel())
    }
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      editRoleButton { performClick() }
      changeRoleTitle { assertIsDisplayed() }
    }
  }
  // test case 8: check if the radio button is displayed
  @Test
  fun radioButtonIsDisplayed() = run {
    val User1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          User1.userId, User1.name, User1.email, User1.role, profilePhoto = User1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel())
    }
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      editRoleButton { performClick() }
      composeTestRule.onNodeWithText("MEMBER").performClick()
    }
  }
  // test case 9: check if the string role is displayed
  @Test
  fun stringRoleIsDisplayed() = run {
    val User1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          User1.userId, User1.name, User1.email, User1.role, profilePhoto = User1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel())
    }
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      editRoleButton { performClick() }
      composeTestRule.onNodeWithTag("stringRoleMEMBER").isDisplayed()
      composeTestRule.onNodeWithTag("stringRoleVIEWER").isDisplayed()
      composeTestRule.onNodeWithTag("stringRoleADMIN").isDisplayed()
      composeTestRule.onNodeWithTag("stringRoleOWNER").isDisplayed()
    }
  }
  // test case 10: check if the confirm role change button is displayed
  @Test
  fun confirmRoleChangeButtonIsDisplayed() = run {
    val User1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          User1.userId, User1.name, User1.email, User1.role, profilePhoto = User1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel())
    }
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      editRoleButton { performClick() }
      ConfirmRoleChangeButton { assertIsDisplayed() }
    }
  }
  // test case 11: check if the confirm role change button is enabled
  @Test
  fun confirmRoleChangeButtonIsEnabled() = run {
    val User1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          User1.userId, User1.name, User1.email, User1.role, profilePhoto = User1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel())
    }
    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      editRoleButton { performClick() }
      ConfirmRoleChangeButton { assertIsEnabled() }
    }
  }

  @Test
  fun checkViews() = run {
    val User1 = FakeAdminViewModel().listOfUsers.value[0]
    val User2 = FakeAdminViewModel().listOfUsers.value[1]
    val User3 = FakeAdminViewModel().listOfUsers.value[2]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          User3.userId, User3.name, User3.email, User3.role, profilePhoto = User3.profilePictureURL)
      Admin(FakeAdminViewModel())
    }

    composeTestRule
        .onNodeWithTag("memberCard" + User1.userId, useUnmergedTree = true)
        .assertExists()
    composeTestRule
        .onNodeWithTag("memberCard" + User2.userId, useUnmergedTree = true)
        .assertExists()

    composeTestRule.onNodeWithTag("divider" + User1.userId, useUnmergedTree = true).assertExists()

    composeTestRule
        .onNodeWithTag("memberName" + User1.userId, useUnmergedTree = true)
        .assertExists()
        .assertTextContains(User1.name)
    composeTestRule
        .onNodeWithTag("memberName" + User2.userId, useUnmergedTree = true)
        .assertExists()
        .assertTextContains(User2.name)

    composeTestRule
        .onNodeWithTag("memberRole" + User1.userId, useUnmergedTree = true)
        .assertExists()
        .assertTextContains("${User1.role}")
    composeTestRule
        .onNodeWithTag("memberRole" + User2.userId, useUnmergedTree = true)
        .assertExists()
        .assertTextContains("${User2.role}")
  }

  @Test
  fun checkViewsSingle() = run {
    val viewModel = FakeAdminViewModel()
    val User3 = viewModel.listOfUsers.value[2]
    SessionManager.setUserSession(
        userId = User3.userId,
        User3.name,
        User3.email,
        User3.role,
        profilePhoto = User3.profilePictureURL)
    viewModel.listOfUsers.value = listOf(User3)
    viewModel.currentUser.value = SessionManager.getCurrentUser()
    composeTestRule.setContent {
      SessionManager.setUserSession(
          userId = User3.userId,
          User3.name,
          User3.email,
          User3.role,
          profilePhoto = User3.profilePictureURL)
      Admin(viewModel)
    }

    composeTestRule
        .onNodeWithTag("memberCard" + User3.userId, useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("divider" + User3.userId, useUnmergedTree = true)
        .assertDoesNotExist()
  }

  @Test
  fun checkDetailsAdmin() = run {
    val User1 = FakeAdminViewModel().listOfUsers.value[0]
    val User3 = FakeAdminViewModel().listOfUsers.value[2]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          User3.userId, User3.name, User3.email, User3.role, profilePhoto = User3.profilePictureURL)
      Admin(FakeAdminViewModel())
    }
    composeTestRule
        .onNodeWithTag("memberCard" + User1.userId, useUnmergedTree = true)
        .performClick()

    composeTestRule
        .onNodeWithTag("memberDetail" + User1.userId, useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("memberDetailName" + User1.userId, useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains(User1.name)
    composeTestRule
        .onNodeWithTag("memberDetailRole" + User1.userId, useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("${Role.ADMIN}")
    composeTestRule
        .onNodeWithTag("memberDetailEmail" + User1.userId, useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains(User1.email)
  }

  @Test
  fun checkDetailsOwner() = run {
    val membersViewModel = FakeAdminViewModel()
    val User2 = membersViewModel.listOfUsers.value[1]
    val User3 = membersViewModel.listOfUsers.value[2]

    composeTestRule.setContent {
      SessionManager.setUserSession(User3.userId, User3.name, User3.email, User3.role)
      Admin(membersViewModel)
    }
    composeTestRule
        .onNodeWithTag("memberCard" + User2.userId, useUnmergedTree = true)
        .performClick()

    composeTestRule
        .onNodeWithTag("memberDetail" + User2.userId, useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("memberDetailName" + User2.userId, useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Johnson")
    composeTestRule
        .onNodeWithTag("memberDetailRole" + User2.userId, useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("OWNER")
    composeTestRule
        .onNodeWithTag("ownerDetailIcon" + User2.userId, useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("memberDetailEmail" + User2.userId, useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Johnson2@epfl.ch")
  }

  @Test
  fun OwnerCanModifyRoles() {
    val viewModel = FakeAdminViewModel()
    val User2 = viewModel.listOfUsers.value[1]
    val User1 = viewModel.listOfUsers.value[0]
    SessionManager.setUserSession(
        userId = User2.userId,
        User2.name,
        User2.email,
        User2.role,
        profilePhoto = User2.profilePictureURL)
    viewModel.listOfUsers.value = listOf(User2, User1)
    viewModel.currentUser.value = SessionManager.getCurrentUser()
    composeTestRule.setContent {
      SessionManager.setUserSession(
          userId = User2.userId,
          User2.name,
          User2.email,
          User2.role,
          profilePhoto = User2.profilePictureURL)
      Admin(viewModel)
    }

    ComposeScreen.onComposeScreen<AdminScreen>(composeTestRule) {
      editRoleButton { performClick() }
      composeTestRule.onNodeWithText("MEMBER").performClick()
      ConfirmRoleChangeButton { performClick() }
    }
  }
}
