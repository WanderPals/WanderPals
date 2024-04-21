package com.github.se.wanderpals.announcement

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.CreateAnnouncementViewModel
import com.github.se.wanderpals.screens.CreateAnnouncementScreen
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.announcement.CreateAnnouncement
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.Called
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private val testAnnouncement1: Announcement =
    Announcement("", "", "Title1", "testUser001", "This is Title1.", LocalDateTime.now())

private val testAnnouncement2: Announcement =
    Announcement(
        "", "", "Title2", "testUser002", "This is Title2.", LocalDateTime.now().minusDays(2))

open class CreateAnnouncementViewModelTest(tripsRepository: TripsRepository) :
    CreateAnnouncementViewModel(tripsRepository) {
  override fun addAnnouncement(tripId: String, announcement: Announcement): Boolean {
    // Check only relevant fields, i.e. title and description, as the rest are not user inputs of
    // the create Announcement screen
    assert(
        announcement.title == testAnnouncement1.title ||
            announcement.title == testAnnouncement2.title)
    assert(
        announcement.description == testAnnouncement1.description ||
            announcement.description == testAnnouncement2.description)

    return true
  }
}

@RunWith(AndroidJUnit4::class)
class CreateAnnouncementTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Test
  fun createAnnouncementReturnToAnnouncementBack() = run {
    ComposeScreen.onComposeScreen<CreateAnnouncementScreen>(composeTestRule) {
      val vm = CreateAnnouncementViewModelTest(TripsRepository("testUser001", Dispatchers.IO))
      composeTestRule.setContent {
        CreateAnnouncement(
            "tripId001",
            vm,
            onNavigationBack = { mockNavActions.navigateTo(Route.NOTIFICATION) },
        )
      }

      tripAnnouncementGoBackButton {
        assertIsDisplayed()
        assertIsEnabled()
        performClick()
      }
    }
  }

  @Test
  fun createTripAnnouncementReturnToAnnouncementWhenSuccessfulAndWorksWithOnlyMandatoryField() =
      run {
        ComposeScreen.onComposeScreen<CreateAnnouncementScreen>(composeTestRule) {
          val vm = CreateAnnouncementViewModelTest(TripsRepository("testUser001", Dispatchers.IO))
          composeTestRule.setContent {
            CreateAnnouncement(
                "tripId1",
                vm,
                onNavigationBack = { mockNavActions.navigateTo(Route.NOTIFICATION) },
            )
          }

          step("Open create tripAnnouncement screen") {
            inputAnnouncementTitle {
              assertIsDisplayed()
              performClick()

              assertTextContains("Trip Announcement Title*")

              performTextClearance()
              performTextInput("Title1")
            }

            announcementTitleLengthText {
              assertIsDisplayed()
              assertTextEquals("6/55")
            }

            inputAnnouncementDescription {
              assertIsDisplayed()
              performClick()

              assertTextContains("Trip Announcement Description*")

              performTextClearance()
              performTextInput("This is Title1.")
            }

            createAnnouncementButton {
              assertIsDisplayed()
              performClick()
            }

            verify { mockNavActions.navigateTo(Route.NOTIFICATION) }
            confirmVerified(mockNavActions)
          }
        }
      }

  @Test
  fun createAnnouncementFailsWithMissingTitleAndDoesNotWorkWithEmptyTitle() = run {
    ComposeScreen.onComposeScreen<CreateAnnouncementScreen>(composeTestRule) {
      val vm = CreateAnnouncementViewModelTest(TripsRepository("testUser001", Dispatchers.IO))
      composeTestRule.setContent {
        CreateAnnouncement(
            "tripId1",
            vm,
            onNavigationBack = { mockNavActions.navigateTo(Route.NOTIFICATION) },
        )
      }

      step("Open create tripAnnouncement screen") {
        inputAnnouncementTitle {
          assertIsDisplayed()
          performClick()

          assertTextContains("Trip Announcement Title*")

          performTextClearance()
          // no text input
        }

        announcementTitleLengthText {
          assertIsDisplayed()
          assertTextEquals("0/55") // because no text input
        }

        inputAnnouncementDescription {
          assertIsDisplayed()
          performClick()

          assertTextContains("Trip Announcement Description*")

          performTextClearance()
          performTextInput("This is Title1.")
        }

        createAnnouncementButton {
          assertIsDisplayed()
          performClick()
        }

        verify { mockNavActions wasNot Called }
        confirmVerified(mockNavActions)
      }
    }
  }

  @Test
  fun createAnnouncementFailsWithMissingDescription() = run {
    ComposeScreen.onComposeScreen<CreateAnnouncementScreen>(composeTestRule) {
      val vm = CreateAnnouncementViewModelTest(TripsRepository("testUser001", Dispatchers.IO))
      composeTestRule.setContent {
        CreateAnnouncement(
            "tripId1",
            vm,
            onNavigationBack = { mockNavActions.navigateTo(Route.NOTIFICATION) },
        )
      }

      step("Open create tripAnnouncement screen") {
        inputAnnouncementTitle {
          assertIsDisplayed()
          performClick()

          assertTextContains("Trip Announcement Title*")

          performTextClearance()
          performTextInput("Title1")
        }

        announcementTitleLengthText {
          assertIsDisplayed()
          assertTextEquals("6/55")
        }

        inputAnnouncementDescription {
          assertIsDisplayed()
          performClick()

          assertTextContains("Trip Announcement Description*")

          performTextClearance()
          // no description input
        }

        createAnnouncementButton {
          assertIsDisplayed()
          performClick()
        }

        verify { mockNavActions wasNot Called }
        confirmVerified(mockNavActions)
      }
    }
  }
}
