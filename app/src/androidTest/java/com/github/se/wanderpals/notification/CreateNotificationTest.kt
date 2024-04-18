package com.github.se.wanderpals.notification

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.CreateNotificationViewModel
import com.github.se.wanderpals.screens.CreateNotificationScreen
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.notification.CreateNotification
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

private val testNotification1: TripNotification =
    TripNotification("", "", "Title1", "testUser001", "This is Title1.", LocalDateTime.now())

private val testNotification2: TripNotification =
    TripNotification(
        "", "", "Title2", "testUser002", "This is Title2.", LocalDateTime.now().minusDays(2))

open class CreateNotificationViewModelTest(tripsRepository: TripsRepository) :
    CreateNotificationViewModel(tripsRepository) {
  override fun addNotification(tripId: String, tripNotification: TripNotification): Boolean {
    // Check only relevant fields, i.e. title and description, as the rest are not user inputs of
    // the create notification screen
    assert(
        tripNotification.title == testNotification1.title ||
            tripNotification.title == testNotification2.title)
    assert(
        tripNotification.description == testNotification1.description ||
            tripNotification.description == testNotification2.description)

    return true
  }
}

@RunWith(AndroidJUnit4::class)
class CreateNotificationTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Test
  fun createTripNotificationReturnToNotificationOnCancel() = run {
    ComposeScreen.onComposeScreen<CreateNotificationScreen>(composeTestRule) {
      val vm = CreateNotificationViewModelTest(TripsRepository("testUser001", Dispatchers.IO))
      composeTestRule.setContent {
        CreateNotification(
            "tripId001",
            vm,
            onSuccess = { mockNavActions.navigateTo(Route.NOTIFICATION) },
            onCancel = { mockNavActions.navigateTo(Route.DASHBOARD) })
      }

      tripNotifGoBackButton {
        assertIsDisplayed()
        assertIsEnabled()
        performClick()
      }

      verify { mockNavActions.navigateTo(Route.DASHBOARD) }
      confirmVerified(mockNavActions)
    }
  }

  @Test
  fun createTripNotificationReturnToNotificationWhenSuccessfulAndWorksWithOnlyMandatoryField() =
      run {
        ComposeScreen.onComposeScreen<CreateNotificationScreen>(composeTestRule) {
          val vm = CreateNotificationViewModelTest(TripsRepository("testUser001", Dispatchers.IO))
          composeTestRule.setContent {
            CreateNotification(
                "tripId1",
                vm,
                onSuccess = { mockNavActions.navigateTo(Route.NOTIFICATION) },
                onCancel = { mockNavActions.navigateTo(Route.DASHBOARD) })
          }

          step("Open create tripNotifiation screen") {
            inputNotificationTitle {
              assertIsDisplayed()
              performClick()

              assertTextContains("Trip Notification Title*")

              performTextClearance()
              performTextInput("Title1")
            }

            notifTitleLengthText {
              assertIsDisplayed()
              assertTextEquals("6/55")
            }

            inputNotificationDescription {
              assertIsDisplayed()
              performClick()

              assertTextContains("Trip Notification Description*")

              performTextClearance()
              performTextInput("This is Title1.")
            }

            createNotificationButton {
              assertIsDisplayed()
              performClick()
            }

            verify { mockNavActions.navigateTo(Route.NOTIFICATION) }
            confirmVerified(mockNavActions)
          }
        }
      }

  @Test
  fun createTripNotificationFailsWithMissingTitleAndDoesNotWorkWithEmptyTitle() = run {
    ComposeScreen.onComposeScreen<CreateNotificationScreen>(composeTestRule) {
      val vm = CreateNotificationViewModelTest(TripsRepository("testUser001", Dispatchers.IO))
      composeTestRule.setContent {
        CreateNotification(
            "tripId1",
            vm,
            onSuccess = { mockNavActions.navigateTo(Route.NOTIFICATION) },
            onCancel = { mockNavActions.navigateTo(Route.DASHBOARD) })
      }

      step("Open create tripNotification screen") {
        inputNotificationTitle {
          assertIsDisplayed()
          performClick()

          assertTextContains("Trip Notification Title*")

          performTextClearance()
          // no text input
        }

        notifTitleLengthText {
          assertIsDisplayed()
          assertTextEquals("0/55") // because no text input
        }

        inputNotificationDescription {
          assertIsDisplayed()
          performClick()

          assertTextContains("Trip Notification Description*")

          performTextClearance()
          performTextInput("This is Title1.")
        }

        createNotificationButton {
          assertIsDisplayed()
          performClick()
        }

        verify { mockNavActions wasNot Called }
        confirmVerified(mockNavActions)
      }
    }
  }

  @Test
  fun createTripNotificationFailsWithMissingDescription() = run {
    ComposeScreen.onComposeScreen<CreateNotificationScreen>(composeTestRule) {
      val vm = CreateNotificationViewModelTest(TripsRepository("testUser001", Dispatchers.IO))
      composeTestRule.setContent {
        CreateNotification(
            "tripId1",
            vm,
            onSuccess = { mockNavActions.navigateTo(Route.NOTIFICATION) },
            onCancel = { mockNavActions.navigateTo(Route.DASHBOARD) })
      }

      step("Open create tripNotification screen") {
        inputNotificationTitle {
          assertIsDisplayed()
          performClick()

          assertTextContains("Trip Notification Title*")

          performTextClearance()
          performTextInput("Title1")
        }

        notifTitleLengthText {
          assertIsDisplayed()
          assertTextEquals("6/55")
        }

        inputNotificationDescription {
          assertIsDisplayed()
          performClick()

          assertTextContains("Trip Notification Description*")

          performTextClearance()
          // no description input
        }

        createNotificationButton {
          assertIsDisplayed()
          performClick()
        }

        verify { mockNavActions wasNot Called }
        confirmVerified(mockNavActions)
      }
    }
  }
}
