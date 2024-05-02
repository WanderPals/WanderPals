package com.github.se.wanderpals.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.MembersViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.screens.dashboard.DashboardMemberList
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private val user1 =
    User(userId = "1", name = "Alice", email = "al_ice75@gmail.com", role = Role.MEMBER)

private val user2 =
    User(userId = "2", name = "Bob", email = "bob9999@outlook.com", role = Role.MEMBER)

private val user3 =
    User(
        userId = "3",
        name = "Kilo Yankee Sierra",
        email = "kilosierra@gmail.com",
        role = Role.OWNER)

class MembersViewModelTest(list: List<User>) :
    MembersViewModel(tripId = "", tripsRepository = TripsRepository("", Dispatchers.IO)) {
  private val _isLoading = MutableStateFlow(false)
  override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _members = MutableStateFlow(list)
  override val members: StateFlow<List<User>> = _members.asStateFlow()

  override fun loadMembers() {}

  fun setLoading(isLoading: Boolean) {
    _isLoading.value = isLoading
  }
}

@RunWith(AndroidJUnit4::class)
class DashboardMemberTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Test
  fun checkViews() = run {
    val membersViewModel = MembersViewModelTest(listOf(user1, user2, user3))
    composeTestRule.setContent { DashboardMemberList(membersViewModel, mockNavActions) }

    composeTestRule.onNodeWithTag("memberCard1", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("memberCard2", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("memberCard3", useUnmergedTree = true).assertExists()

    composeTestRule.onNodeWithTag("divider1", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("divider2", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("divider3", useUnmergedTree = true).assertDoesNotExist()

    composeTestRule
        .onNodeWithTag("memberName1", useUnmergedTree = true)
        .assertExists()
        .assertTextContains("Alice")
    composeTestRule
        .onNodeWithTag("memberName2", useUnmergedTree = true)
        .assertExists()
        .assertTextContains("Bob")
    composeTestRule
        .onNodeWithTag("memberName3", useUnmergedTree = true)
        .assertExists()
        .assertTextContains("Kilo Yankee Sierra")

    composeTestRule
        .onNodeWithTag("memberRole1", useUnmergedTree = true)
        .assertExists()
        .assertTextContains("MEMBER")
    composeTestRule
        .onNodeWithTag("memberRole2", useUnmergedTree = true)
        .assertExists()
        .assertTextContains("MEMBER")
    composeTestRule
        .onNodeWithTag("memberRole3", useUnmergedTree = true)
        .assertExists()
        .assertTextContains("OWNER")
    composeTestRule.onNodeWithTag("ownerIcon3", useUnmergedTree = true).assertExists()
  }

  @Test
  fun checkViewsEmpty() = run {
    val membersViewModel = MembersViewModelTest(listOf(user1, user2, user3))
    composeTestRule.setContent { DashboardMemberList(membersViewModel, mockNavActions) }

    composeTestRule
        .onNodeWithTag("MemberListTitle", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("BackButton", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
  }

  @Test
  fun checkViewsSingle() = run {
    val membersViewModel = MembersViewModelTest(listOf(user1))
    composeTestRule.setContent { DashboardMemberList(membersViewModel, mockNavActions) }

    composeTestRule
        .onNodeWithTag("memberCard1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("divider1", useUnmergedTree = true).assertDoesNotExist()
  }

  @Test
  fun checkViewsTwo() = run {
    val membersViewModel = MembersViewModelTest(listOf(user1, user2))
    composeTestRule.setContent { DashboardMemberList(membersViewModel, mockNavActions) }

    composeTestRule
        .onNodeWithTag("memberCard1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("memberCard2", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("divider1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("divider2", useUnmergedTree = true).assertDoesNotExist()
  }

  @Test
  fun checkGoBack() = run {
    val membersViewModel = MembersViewModelTest(listOf(user1, user2, user3))
    composeTestRule.setContent { DashboardMemberList(membersViewModel, mockNavActions) }
    composeTestRule.onNodeWithTag("BackButton", useUnmergedTree = true).performClick()

    verify { mockNavActions.goBack() }
    confirmVerified(mockNavActions)
  }

  @Test
  fun checkDetailsMember() = run {
    val membersViewModel = MembersViewModelTest(listOf(user1, user2, user3))
    composeTestRule.setContent { DashboardMemberList(membersViewModel, mockNavActions) }
    composeTestRule.onNodeWithTag("memberCard1", useUnmergedTree = true).performClick()

    composeTestRule
        .onNodeWithTag("memberDetail1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("memberDetailName1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Alice")
    composeTestRule
        .onNodeWithTag("memberDetailRole1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("MEMBER")
    composeTestRule
        .onNodeWithTag("memberDetailEmail1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("al_ice75@gmail.com")
  }

  @Test
  fun checkDetailsOwner() = run {
    val membersViewModel = MembersViewModelTest(listOf(user1, user2, user3))
    composeTestRule.setContent { DashboardMemberList(membersViewModel, mockNavActions) }
    composeTestRule.onNodeWithTag("memberCard3", useUnmergedTree = true).performClick()

    composeTestRule
        .onNodeWithTag("memberDetail3", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("memberDetailName3", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Kilo Yankee Sierra")
    composeTestRule
        .onNodeWithTag("memberDetailRole3", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("OWNER")
    composeTestRule
        .onNodeWithTag("ownerDetailIcon3", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("memberDetailEmail3", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("kilosierra@gmail.com")
  }
}
