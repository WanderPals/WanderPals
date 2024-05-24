package com.github.se.wanderpals.dashboard

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.AdminViewModel
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.screens.Admin
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
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
    val user1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user1.userId, user1.name, user1.email, user1.role, profilePhoto = user1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule.onNodeWithTag("adminScreen").assertIsDisplayed()
  }

  @Test
  fun userNameDisplay() = run {
    val user1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user1.userId, user1.name, user1.email, user1.role, profilePhoto = user1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule.onNodeWithTag("userName").assertTextContains(user1.name)
  }

  // test case 3: check if the edit role button is displayed
  @Test
  fun editRoleButtonIsDisplayed() = run {
    val user1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user1.userId, user1.name, user1.email, user1.role, profilePhoto = user1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule.onNodeWithTag("editRoleButton" + user1.userId).assertExists()
  }

  // test case 4: check if the delete user button is displayed
  @Test
  fun deleteUserButtonIsDisplayed() = run {
    val user1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user1.userId, user1.name, user1.email, user1.role, profilePhoto = user1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule.onNodeWithTag("deleteUserButton" + user1.userId).assertExists()
  }

  // test case 5: check if the confirm delete user button is displayed
  @Test
  fun confirmDeleteUserButtonIsDisplayed() = run {
    val user1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user1.userId, user1.name, user1.email, user1.role, profilePhoto = user1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule.onNodeWithTag("deleteUserButton" + user1.userId).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("confirmDeleteUserButton").assertExists()
  }

  // test case 6: check if the cancel delete user button is displayed
  @Test
  fun cancelDeleteUserButtonIsDisplayed() = run {
    val user1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user1.userId, user1.name, user1.email, user1.role, profilePhoto = user1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule.onNodeWithTag("deleteUserButton" + user1.userId).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("cancelDeleteUserButton").assertExists()
  }

  // test case 7: check if the change role title is displayed
  @Test
  fun changeRoleTitleIsDisplayed() = run {
    val user1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user1.userId, user1.name, user1.email, user1.role, profilePhoto = user1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule.onNodeWithTag("editRoleButton" + user1.userId).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("changeRoleTitle").assertExists()
  }

  // test case 8: check if the radio button is displayed
  @Test
  fun radioButtonIsDisplayed() = run {
    val user1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user1.userId, user1.name, user1.email, user1.role, profilePhoto = user1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule.onNodeWithTag("editRoleButton" + user1.userId).performClick()
    composeTestRule.waitForIdle()
    // Check there are 4 radio buttons
    composeTestRule.onAllNodesWithTag("radioButton", useUnmergedTree = true).assertCountEquals(4)
  }

  // test case 9: check if the string role is displayed
  @Test
  fun stringRoleIsDisplayed() = run {
    val user1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user1.userId, user1.name, user1.email, user1.role, profilePhoto = user1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule.onNodeWithTag("editRoleButton" + user1.userId).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("stringRoleADMIN", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("stringRoleOWNER", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("stringRoleMEMBER", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("stringRoleVIEWER", useUnmergedTree = true).assertExists()
  }

  // test case 10: check if the confirm role change button is displayed
  @Test
  fun confirmRoleChangeButtonIsDisplayed() = run {
    val user1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user1.userId, user1.name, user1.email, user1.role, profilePhoto = user1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule.onNodeWithTag("editRoleButton" + user1.userId).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("confirmRoleChangeButton", useUnmergedTree = true).isDisplayed()
  }
  // test case 11: check if the confirm role change button is enabled
  @Test
  fun confirmRoleChangeButtonIsEnabled() = run {
    val user1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user1.userId, user1.name, user1.email, user1.role, profilePhoto = user1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule.onNodeWithTag("editRoleButton" + user1.userId).performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("confirmRoleChangeButton", useUnmergedTree = true)
        .assertIsEnabled()
  }

  @Test
  fun confirmRoleChangeButtonIsDisabledOffline() = run {
    SessionManager.setIsNetworkAvailable(false)
    val user1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user1.userId, user1.name, user1.email, user1.role, profilePhoto = user1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule.onNodeWithTag("editRoleButton" + user1.userId).performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("confirmRoleChangeButton", useUnmergedTree = true)
        .assertIsNotEnabled()
    SessionManager.setIsNetworkAvailable(true)
  }

  @Test
  fun checkUserProfilePictures() = run {
    val user1 = FakeAdminViewModel().listOfUsers.value[0]
    val user2 = FakeAdminViewModel().listOfUsers.value[1]
    val user3 = FakeAdminViewModel().listOfUsers.value[2]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user3.userId, user3.name, user3.email, user3.role, profilePhoto = user3.profilePictureURL)
      Admin(FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule
        .onNodeWithTag("userProfilePicture" + user1.userId, useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("userProfilePicture" + user2.userId, useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("userProfilePicture" + user3.userId, useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
  }

  @Test
  fun checkUserNames() = run {
    val user1 = FakeAdminViewModel().listOfUsers.value[0]
    val user2 = FakeAdminViewModel().listOfUsers.value[1]
    val user3 = FakeAdminViewModel().listOfUsers.value[2]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user3.userId, user3.name, user3.email, user3.role, profilePhoto = user3.profilePictureURL)
      Admin(FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule
        .onNodeWithTag("userName" + user1.userId, useUnmergedTree = true)
        .assertExists()
        .assertTextContains(user1.name)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("userName" + user2.userId, useUnmergedTree = true)
        .assertExists()
        .assertTextContains(user2.name)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("userName" + user3.userId, useUnmergedTree = true)
        .assertExists()
        .assertTextContains(user3.name)
        .assertIsDisplayed()
  }

  @Test
  fun checkCurrentUserDetails() = run {
    val user3 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user3.userId,
          user3.name,
          user3.email,
          role = user3.role,
          profilePhoto = user3.profilePictureURL,
          tripName = "Test Trip",
          nickname = "user")
      Admin(FakeAdminViewModel(), storageReference = null)
    }
    val currUser = FakeAdminViewModel().currentUser.value
    if (currUser != null) {
      composeTestRule
          .onNodeWithTag("userName", useUnmergedTree = true)
          .assertExists()
          .assertTextContains(currUser.name)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("tripName", useUnmergedTree = true)
          .assertExists()
          .assertTextContains("Current trip : " + currUser.tripName)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("userRole", useUnmergedTree = true)
          .assertExists()
          .assertTextContains("Your role : " + currUser.role.toString())
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("userNickname", useUnmergedTree = true)
          .assertExists()
          .assertTextContains(currUser.nickname)
          .assertIsDisplayed()
    }
  }

  @Test
  fun userNameEnabledOnline() = run {
    val user1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user1.userId, user1.name, user1.email, user1.role, profilePhoto = user1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule.onNodeWithTag("userName").assertIsEnabled()
  }

  @Test
  fun userNameNotEnabledOffline() = run {
    SessionManager.setIsNetworkAvailable(false)
    val user1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user1.userId, user1.name, user1.email, user1.role, profilePhoto = user1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule.onNodeWithTag("userName").assertIsNotEnabled()
    SessionManager.setIsNetworkAvailable(true)
  }

  @Test
  fun iconAdminScreenEnabledOnline() = run {
    val user1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user1.userId, user1.name, user1.email, user1.role, profilePhoto = user1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule.onNodeWithTag("IconAdminScreen").assertIsEnabled()
  }

  @Test
  fun iconAdminScreeNotEnabledOffline() = run {
    SessionManager.setIsNetworkAvailable(false)
    val user1 = FakeAdminViewModel().listOfUsers.value[0]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user1.userId, user1.name, user1.email, user1.role, profilePhoto = user1.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule.onNodeWithTag("IconAdminScreen").assertIsNotEnabled()
    SessionManager.setIsNetworkAvailable(true)
  }

  @Test
  fun promoteUserButtonEnabledOnline() = run {
    val user2 = FakeAdminViewModel().listOfUsers.value[1]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user2.userId, user2.name, user2.email, user2.role, profilePhoto = user2.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule.onNodeWithTag("promoteUserButton" + "3").assertIsEnabled()
  }

  @Test
  fun promoteUserButtonNotEnabledOffline() = run {
    SessionManager.setIsNetworkAvailable(false)
    val user2 = FakeAdminViewModel().listOfUsers.value[1]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user2.userId, user2.name, user2.email, user2.role, profilePhoto = user2.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule.onNodeWithTag("promoteUserButton" + "3").assertIsNotEnabled()
    SessionManager.setIsNetworkAvailable(true)
  }

  @Test
  fun deleteUserButtonEnabledOnline() = run {
    val user2 = FakeAdminViewModel().listOfUsers.value[1]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user2.userId, user2.name, user2.email, user2.role, profilePhoto = user2.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule.onNodeWithTag("deleteUserButton" + "3").assertIsEnabled()
  }

  @Test
  fun deleteUserButtonNotEnabledOffline() = run {
    SessionManager.setIsNetworkAvailable(false)
    val user2 = FakeAdminViewModel().listOfUsers.value[1]
    composeTestRule.setContent {
      SessionManager.setUserSession(
          user2.userId, user2.name, user2.email, user2.role, profilePhoto = user2.profilePictureURL)
      Admin(adminViewModel = FakeAdminViewModel(), storageReference = null)
    }
    composeTestRule.onNodeWithTag("deleteUserButton" + "3").assertIsNotEnabled()
    SessionManager.setIsNetworkAvailable(true)
  }
}
