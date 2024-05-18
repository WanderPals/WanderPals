package com.github.se.wanderpals.trip

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.OverviewViewModel
import com.github.se.wanderpals.screens.CreateTripoScreen
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.CreateTrip
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.spyk
import io.mockk.verify
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private val testTrip =
    Trip(
        "",
        "My trip",
        LocalDate.of(2024, 3, 5),
        LocalDate.of(2024, 3, 10),
        500.0,
        "My description",
        "",
        emptyList(),
        emptyList(),
        emptyList())

open class CreateTripViewModelTest(tripsRepository: TripsRepository) :
    OverviewViewModel(tripsRepository) {
  override fun createTrip(trip: Trip) {
    assert(trip == testTrip)
  }
}

@RunWith(AndroidJUnit4::class)
class CreateTripTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Test
  fun goBackButtonTriggersBackNavigation() = run {
    val vm = CreateTripViewModelTest(TripsRepository("testUser123", Dispatchers.IO))
    composeTestRule.setContent { CreateTrip(vm, mockNavActions) }
    ComposeScreen.onComposeScreen<CreateTripoScreen>(composeTestRule) {
      goBackButton {
        assertIsDisplayed()
        assertIsEnabled()
        performClick()
      }
    }

    verify { mockNavActions.navigateTo(Route.OVERVIEW) }
    confirmVerified(mockNavActions)
  }

  @Test
  fun saveTripDoesNotWorkWithEmptyTitle() = run {
    val vm = CreateTripViewModelTest(TripsRepository("testUser123", Dispatchers.IO))
    composeTestRule.setContent { CreateTrip(vm, mockNavActions) }
    ComposeScreen.onComposeScreen<CreateTripoScreen>(composeTestRule) {
      step("Open trip screen") {
        inputTitle {
          assertIsDisplayed()
          performClick()

          assertTextContains("Title")
          assertTextContains("Name the trip")

          performTextClearance()
        }

        titleLengthText {
          assertIsDisplayed()
          assertTextEquals("0/35")
        }

        inputBudget {
          assertIsDisplayed()
          performClick()

          assertTextContains("Budget")

          performTextClearance()
          performTextInput("500")
        }

        inputDescription {
          assertIsDisplayed()
          performClick()

          assertTextContains("Describe")
          assertTextContains("Describe the trip")

          performTextClearance()
          performTextInput("My description")
        }

        inputStartDate {
          assertIsDisplayed()

          assertTextContains("dd/mm/yyyy")

          performTextClearance()
          performTextInput("05/03/2024")
        }

        inputEndDate {
          assertIsDisplayed()

          assertTextContains("dd/mm/yyyy")

          performTextClearance()
          performTextInput("10/03/2024")
        }

        saveButton {
          assertIsDisplayed()
          performClick()
        }

        verify { mockNavActions wasNot Called }
        confirmVerified(mockNavActions)
      }
    }
  }

  @Test
  fun saveTripDoesNotWorkWithEmptyDescription() = run {
    val vm = CreateTripViewModelTest(TripsRepository("testUser123", Dispatchers.IO))
    composeTestRule.setContent { CreateTrip(vm, mockNavActions) }
    ComposeScreen.onComposeScreen<CreateTripoScreen>(composeTestRule) {
      step("Open trip screen") {
        inputTitle {
          assertIsDisplayed()
          performClick()

          assertTextContains("Title")
          assertTextContains("Name the trip")

          performTextClearance()
          performTextInput("My trip")
        }
        titleLengthText {
          assertIsDisplayed()
          assertTextEquals("7/35")
        }

        inputBudget {
          assertIsDisplayed()
          performClick()

          assertTextContains("Budget")

          performTextClearance()
          performTextInput("500")
        }

        inputDescription {
          assertIsDisplayed()
          performClick()

          assertTextContains("Describe")
          assertTextContains("Describe the trip")

          performTextClearance()
        }

        inputStartDate {
          assertIsDisplayed()

          assertTextContains("dd/mm/yyyy")

          performTextClearance()
          performTextInput("05/03/2024")
        }

        inputEndDate {
          assertIsDisplayed()

          assertTextContains("dd/mm/yyyy")

          performTextClearance()
          performTextInput("10/03/2024")
        }

        saveButton {
          assertIsDisplayed()
          performClick()
        }

        verify { mockNavActions wasNot Called }
        confirmVerified(mockNavActions)
      }
    }
  }

  fun saveTripDoesNotWorkWithEmptyDates() = run {
    val vm = CreateTripViewModelTest(TripsRepository("testUser123", Dispatchers.IO))
    composeTestRule.setContent { CreateTrip(vm, mockNavActions) }
    ComposeScreen.onComposeScreen<CreateTripoScreen>(composeTestRule) {
      step("Open trip screen") {
        inputTitle {
          assertIsDisplayed()
          performClick()

          assertTextContains("Title")
          assertTextContains("Name the trip")

          performTextClearance()
          performTextInput("My trip")
        }
        titleLengthText {
          assertIsDisplayed()
          assertTextEquals("7/35")
        }

        inputBudget {
          assertIsDisplayed()
          performClick()

          assertTextContains("Budget")

          performTextClearance()
          performTextInput("500")
        }

        inputDescription {
          assertIsDisplayed()
          performClick()

          assertTextContains("Describe")
          assertTextContains("Describe the trip")

          performTextClearance()
          performTextInput("My description")
        }

        inputStartDate {
          assertIsDisplayed()

          assertTextContains("dd/mm/yyyy")

          performTextClearance()
        }

        inputEndDate {
          assertIsDisplayed()

          assertTextContains("dd/mm/yyyy")

          performTextClearance()
        }

        saveButton {
          assertIsDisplayed()
          performClick()
        }

        verify { mockNavActions wasNot Called }
        confirmVerified(mockNavActions)
      }
    }
  }

  fun saveTripDoesNotWorkWithNotLogicalDates() = run {
    val vm = CreateTripViewModelTest(TripsRepository("testUser123", Dispatchers.IO))
    composeTestRule.setContent { CreateTrip(vm, mockNavActions) }
    ComposeScreen.onComposeScreen<CreateTripoScreen>(composeTestRule) {
      step("Open trip screen") {
        inputTitle {
          assertIsDisplayed()
          performClick()

          assertTextContains("Title")
          assertTextContains("Name the trip")

          performTextClearance()
          performTextInput("My trip")
        }
        titleLengthText {
          assertIsDisplayed()
          assertTextEquals("7/35")
        }

        inputBudget {
          assertIsDisplayed()
          performClick()

          assertTextContains("Budget")

          performTextClearance()
          performTextInput("500")
        }

        inputDescription {
          assertIsDisplayed()
          performClick()

          assertTextContains("Describe")
          assertTextContains("Describe the trip")

          performTextClearance()
          performTextInput("My description")
        }

        inputStartDate {
          assertIsDisplayed()

          assertTextContains("dd/mm/yyyy")

          performTextClearance()
          performTextInput("10/03/2024")
        }

        inputEndDate {
          assertIsDisplayed()

          assertTextContains("dd/mm/yyyy")

          performTextClearance()
          performTextInput("05/03/2024")
        }

        saveButton {
          assertIsDisplayed()
          performClick()
        }

        verify { mockNavActions wasNot Called }
        confirmVerified(mockNavActions)
      }
    }
  }

  @Test
  fun saveDoesNotWorkWithNegativeBudget() = run {
    ComposeScreen.onComposeScreen<CreateTripoScreen>(composeTestRule) {
      val vm = CreateTripViewModelTest(TripsRepository("testUser123", Dispatchers.IO))
      composeTestRule.setContent { CreateTrip(vm, mockNavActions) }
      step("Open trip screen") {
        inputTitle {
          assertIsDisplayed()
          performClick()

          assertTextContains("Title")
          assertTextContains("Name the trip")

          performTextClearance()
          performTextInput("My trip")
        }
        titleLengthText {
          assertIsDisplayed()
          assertTextEquals("7/35")
        }

        inputBudget {
          assertIsDisplayed()
          performClick()

          assertTextContains("Budget")

          performTextClearance()
          performTextInput("-500")
        }

        inputDescription {
          assertIsDisplayed()
          performClick()

          assertTextContains("Describe")
          assertTextContains("Describe the trip")

          performTextClearance()
          performTextInput("My description")
        }

        inputStartDate {
          assertIsDisplayed()

          assertTextContains("dd/mm/yyyy")

          performTextClearance()
          performTextInput("05/03/2024")
        }

        inputEndDate {
          assertIsDisplayed()

          assertTextContains("dd/mm/yyyy")

          performTextClearance()
          performTextInput("10/03/2024")
        }

        saveButton {
          assertIsDisplayed()
          performClick()
        }

        verify { mockNavActions wasNot Called }
        confirmVerified(mockNavActions)
      }
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun saveTripWorks() = runBlockingTest {

    // Spy on the actual ViewModel rather than completely mocking it
    val overviewViewModel =
        spyk(
            OverviewViewModel(tripsRepository = TripsRepository("-1", Dispatchers.IO)),
            recordPrivateCalls = true)

    // Mock the createTrip function to only modify certain properties
    coEvery { overviewViewModel.createTrip(any()) } coAnswers
        {
          overviewViewModel.apply {
            this.setCreateTripFinished(true)
            // this.setProperty("_createTripFinished", true)
          }
        }

    // Set the content of the test
    composeTestRule.setContent {
      CreateTrip(overviewViewModel = overviewViewModel, nav = mockNavActions)
    }

    // Access the UI controls using your custom screen class
    val screen = CreateTripoScreen(composeTestRule)

    // Simulate user inputs and interactions
    screen.inputTitle.performTextInput("My Trip")
    screen.inputBudget.performTextInput("500")
    screen.inputDescription.performTextInput("An exciting journey")
    screen.inputStartDate.performTextInput("05/03/2024")
    screen.inputEndDate.performTextInput("10/03/2024")

    // Simulate clicking the save button
    screen.saveButton.performClick()

    // Wait for Compose to process potential recompositions due to state changes
    composeTestRule.waitForIdle()

    // Verify that the navigation has been triggered as expected due to the ViewModel's state change
    verify { mockNavActions.navigateTo(Route.OVERVIEW) }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun saveTripDoesntWorkOffline() = runBlockingTest {

    // Spy on the actual ViewModel rather than completely mocking it
    val overviewViewModel =
        spyk(
            OverviewViewModel(tripsRepository = TripsRepository("-1", Dispatchers.IO)),
            recordPrivateCalls = true)

    // Mock the createTrip function to only modify certain properties
    coEvery { overviewViewModel.createTrip(any()) } coAnswers
        {
          overviewViewModel.apply {
            this.setCreateTripFinished(true)
            // this.setProperty("_createTripFinished", true)
          }
        }
    SessionManager.setIsNetworkAvailable(false)

    // Set the content of the test
    composeTestRule.setContent {
      CreateTrip(overviewViewModel = overviewViewModel, nav = mockNavActions)
    }

    // Access the UI controls using your custom screen class
    val screen = CreateTripoScreen(composeTestRule)

    // Simulate user inputs and interactions
    screen.inputTitle.performTextInput("My Trip")
    screen.inputBudget.performTextInput("500")
    screen.inputDescription.performTextInput("An exciting journey")
    screen.inputStartDate.performTextInput("05/03/2024")
    screen.inputEndDate.performTextInput("10/03/2024")

    // Check that the save button is disabled
    screen.saveButton.assertIsNotEnabled()
  }
}
