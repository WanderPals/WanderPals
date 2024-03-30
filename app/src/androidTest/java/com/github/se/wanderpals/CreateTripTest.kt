package com.github.se.wanderpals

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.CreateTripViewModel
import com.github.se.wanderpals.screens.CreateTripoScreen
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.CreateTrip
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.Called
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import org.junit.Before
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
    CreateTripViewModel(tripsRepository) {
  override fun createTrip(trip: Trip) {
    assert(trip == testTrip)
  }
}

@RunWith(AndroidJUnit4::class)
class CreateTripTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Before
  fun testSetup() {
    val vm = CreateTripViewModelTest(TripsRepository("testUser123", Dispatchers.IO))
    composeTestRule.setContent { CreateTrip(vm, mockNavActions) }
  }

  @Test
  fun goBackButtonTriggersBackNavigation() = run {
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
    ComposeScreen.onComposeScreen<CreateTripoScreen>(composeTestRule) {
      step("Open trip screen") {
        inputTitle {
          assertIsDisplayed()
          performClick()

          assertTextContains("Title")
          assertTextContains("Name the trip")

          performTextClearance()
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
      step("Open trip screen") {
        inputTitle {
          assertIsDisplayed()
          performClick()

          assertTextContains("Title")
          assertTextContains("Name the trip")

          performTextClearance()
          performTextInput("My trip")
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

  @Test
  fun saveTripWorks() = run {
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

        verify { mockNavActions.navigateTo(Route.OVERVIEW) }
        confirmVerified(mockNavActions)
      }
    }
  }
}
