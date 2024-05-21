package com.github.se.wanderpals.notifications

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.NotificationsViewModel
import com.github.se.wanderpals.screens.CreateAnnouncementScreen
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.trip.notifications.CreateAnnouncement
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateAnnouncementTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Test
  fun createAnnouncementReturnToAnnouncementBack() = run {
    ComposeScreen.onComposeScreen<CreateAnnouncementScreen>(composeTestRule) {
      val vm = NotificationsViewModelTest()
      composeTestRule.setContent {
        CreateAnnouncement(
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

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun createTripAnnouncementReturnToAnnouncementWhenSuccessfulAndWorksWithOnlyMandatoryFields() =
      runBlockingTest {
        ComposeScreen.onComposeScreen<CreateAnnouncementScreen>(composeTestRule) {

          // Set up the ViewModel
          val vm = spyk(NotificationsViewModelTest(), recordPrivateCalls = true)

          SessionManager.setUserSession()
          SessionManager.setRole(Role.OWNER)

          // Mock the ViewModel's function if needed
          coEvery { vm.addAnnouncement(any()) } coAnswers
              {
                vm.apply { this.setCreateAnnouncementFinished(true) }
              }
          composeTestRule.setContent {
            CreateAnnouncement(
                viewModel = vm,
                onNavigationBack = { mockNavActions.navigateTo(Route.NOTIFICATION) },
            )
          }

          // Access the UI controls using your custom screen class
          val screen = CreateAnnouncementScreen(composeTestRule)

          // Simulate user inputs and interactions
          screen.inputAnnouncementTitle.performTextInput("Title1")
          screen.inputAnnouncementDescription.performTextInput("This is Title1.")

          // Simulate clicking the create button
          screen.createAnnouncementButton.performClick()

          // Wait for Compose to process potential recompositions due to state changes
          composeTestRule.waitForIdle()

          // Verify that the navigation has been triggered as expected due to the ViewModel's state
          // change
          verify { mockNavActions.navigateTo(Route.NOTIFICATION) }
          confirmVerified(mockNavActions)
        }
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun multipleCallsToAddAnnouncementDoNotCreateMultipleObjects() =
      runBlockingTest {
        val tripsRepository = mockk<TripsRepository>()
        val tripId = "test_trip_id"

        // Create a spy of the NotificationsViewModel
        val vm = spyk(NotificationsViewModel(tripsRepository, tripId), recordPrivateCalls = true)

        // Set user session and role
        SessionManager.setUserSession()
        SessionManager.setRole(Role.OWNER)

        // Mock the repository functions as needed
        coEvery { tripsRepository.addAnnouncementToTrip(any(), any()) } returns true
        coEvery { tripsRepository.getTrip(any()) } coAnswers
            {
              // Simulate a delay to mimic the coroutine execution time
              delay(500)
              null
            }

        // Set the content of the test
        composeTestRule.setContent {
          CreateAnnouncement(
              viewModel = vm,
              onNavigationBack = { mockNavActions.navigateTo(Route.NOTIFICATION) },
          )
        }

        // Access the UI controls using your custom screen class
        val screen = CreateAnnouncementScreen(composeTestRule)

        // Simulate user inputs and interactions
        screen.inputAnnouncementTitle.performTextInput("Title1")
        screen.inputAnnouncementDescription.performTextInput("This is Title1.")

        // Simulate clicking the create button twice concurrently
        launch { screen.createAnnouncementButton.performClick() }
        launch { screen.createAnnouncementButton.performClick() }

        // Wait for Compose to process potential recompositions due to state changes
        composeTestRule.waitForIdle()

        // Verify that the addAnnouncement method was only called once
        coVerify(exactly = 1) { tripsRepository.addAnnouncementToTrip(any(), any()) }
      }

  @Test
  fun createAnnouncementFailsWithMissingTitleAndDoesNotWorkWithEmptyTitle() = run {
    ComposeScreen.onComposeScreen<CreateAnnouncementScreen>(composeTestRule) {
      val vm = NotificationsViewModelTest()
      composeTestRule.setContent {
        CreateAnnouncement(
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
      val vm = NotificationsViewModelTest()
      composeTestRule.setContent {
        CreateAnnouncement(
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

  @Test
  fun createAnnouncementOffline() = run {
    ComposeScreen.onComposeScreen<CreateAnnouncementScreen>(composeTestRule) {
      val vm = NotificationsViewModelTest()
      SessionManager.setIsNetworkAvailable(false)
      composeTestRule.setContent {
        CreateAnnouncement(
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

        createAnnouncementButton { assertIsNotEnabled() }

        verify { mockNavActions wasNot Called }
        confirmVerified(mockNavActions)
      }
    }
    SessionManager.setIsNetworkAvailable(true)
  }
}
