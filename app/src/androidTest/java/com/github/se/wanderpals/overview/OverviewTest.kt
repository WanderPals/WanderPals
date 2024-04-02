package com.github.se.wanderpals.overview

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.OverviewViewModel
import com.github.se.wanderpals.screens.OverviewScreen
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.overview.Overview
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class OverviewViewModelTest() :
    OverviewViewModel(TripsRepository("-1", dispatcher = Dispatchers.IO)) {
    private val trip1 =
        Trip(
            tripId = "1",
            title = "trip : Summer Adventure",
            startDate = LocalDate.of(2024, 7, 1),
            endDate = LocalDate.of(2024, 7, 15),
            totalBudget = 2000.0,
            description = "An adventurous trip exploring nature and wildlife.",
            imageUrl = "https://example.com/summer_adventure.jpg",
            stops = listOf("stop1", "stop2", "stop3"),
            users = listOf("user1", "user2", "user3"),
            suggestions = listOf("suggestion1", "suggestion2")
        )

    private val trip2 =
        Trip(
            tripId = "2",
            title = "Winter Ski Trip",
            startDate = LocalDate.of(2024, 12, 20),
            endDate = LocalDate.of(2024, 12, 30),
            totalBudget = 3000.0,
            description = "A ski trip to the snowy mountains.",
            imageUrl = "https://example.com/winter_ski_trip.jpg",
            stops = listOf("ski_resort1", "ski_resort2"),
            users = listOf("user4", "user5"),
            suggestions = listOf("suggestion3", "suggestion4", "suggestion5")
        )

    private val trip3 =
        Trip(
            tripId = "3",
            title = "City Exploration",
            startDate = LocalDate.of(2024, 9, 10),
            endDate = LocalDate.of(2024, 9, 15),
            totalBudget = 1500.0,
            description = "Exploring famous landmarks and enjoying city life.",
            imageUrl = "https://example.com/city_exploration.jpg",
            stops = listOf("city_stop1", "city_stop2"),
            users = listOf("user6", "user7", "user8"),
            suggestions = emptyList()
        )
    private val _state = MutableStateFlow(listOf(trip1, trip2, trip3))
    override val state: StateFlow<List<Trip>> = _state

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun deleteTrips() {
        _state.value = listOf()
    }

    override fun getAllTrips() {}
}

@RunWith(AndroidJUnit4::class)
class OverviewTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    lateinit var mockNavActions: NavigationActions

    private val overviewViewModelTest = OverviewViewModelTest()

    @Before
    fun testSetup() {
        composeTestRule.setContent {
            Overview(overviewViewModel = overviewViewModelTest, navigationActions = mockNavActions)
        }
    }

    @Test
    fun initialOverviewIsCorrectlyDisplayed() = run {
        ComposeScreen.onComposeScreen<OverviewScreen>(composeTestRule) {
            overviewScreen { assertIsDisplayed() }

            dockedSearchBar { assertIsDisplayed() }
            clearSearchButton { assertIsNotDisplayed() }

            noTripForUserText { assertIsNotDisplayed() }
            noTripFoundOnSearchText { assertIsNotDisplayed() }

            buttonTrip1 { assertIsDisplayed() }
            buttonTrip2 { assertIsDisplayed() }
            buttonTrip3 { assertIsDisplayed() }

            joinTripButton { assertIsDisplayed() }
            createTripButton { assertIsDisplayed() }
        }
    }


    @Test
    fun searchTripByTitleFindNoTrip() = run {
        ComposeScreen.onComposeScreen<OverviewScreen>(composeTestRule) {
            overviewScreen { assertIsDisplayed() }

            dockedSearchBar { assertIsDisplayed() }

            composeTestRule.onNodeWithText("Search a trip").performTextInput("abcdefg")
            clearSearchButton { assertIsDisplayed() }

            noTripForUserText { assertIsNotDisplayed() }
            noTripFoundOnSearchText { assertIsDisplayed() }

            buttonTrip1 { assertIsNotDisplayed() }
            buttonTrip2 { assertIsNotDisplayed() }
            buttonTrip3 { assertIsNotDisplayed() }

            joinTripButton { assertIsDisplayed() }
            createTripButton { assertIsDisplayed() }
        }
    }
    @Test
    fun searchTripByTitleCaseInsensitive() = run {
        ComposeScreen.onComposeScreen<OverviewScreen>(composeTestRule) {
            overviewScreen { assertIsDisplayed() }

            dockedSearchBar { assertIsDisplayed() }

            composeTestRule.onNodeWithText("Search a trip").performTextInput("cItY eXPLoRatiOn")
            clearSearchButton { assertIsDisplayed() }

            noTripForUserText { assertIsNotDisplayed() }
            noTripFoundOnSearchText { assertIsNotDisplayed() }

            buttonTrip1 { assertIsNotDisplayed() }
            buttonTrip2 { assertIsNotDisplayed() }
            buttonTrip3 { assertIsDisplayed() }

            joinTripButton { assertIsDisplayed() }
            createTripButton { assertIsDisplayed() }
        }
    }

    @Test
    fun searchTripByTitleFindTrip() = run {
        ComposeScreen.onComposeScreen<OverviewScreen>(composeTestRule) {
            overviewScreen { assertIsDisplayed() }

            dockedSearchBar { assertIsDisplayed() }
            composeTestRule.onNodeWithText("Search a trip").performTextInput("winter")


            clearSearchButton { assertIsDisplayed() }

            noTripForUserText { assertIsNotDisplayed() }
            noTripFoundOnSearchText { assertIsNotDisplayed() }

            buttonTrip1 { assertIsNotDisplayed() }
            buttonTrip2 { assertIsDisplayed() }
            buttonTrip3 { assertIsNotDisplayed() }

            joinTripButton { assertIsDisplayed() }
            createTripButton { assertIsDisplayed() }
        }
    }

    @Test
    fun searchTripByTitleFindTripsWithCommonWordsInTitle() = run {
        ComposeScreen.onComposeScreen<OverviewScreen>(composeTestRule) {
            overviewScreen { assertIsDisplayed() }

            dockedSearchBar { assertIsDisplayed() }
            composeTestRule.onNodeWithText("Search a trip").performTextInput("Trip")

            clearSearchButton { assertIsDisplayed() }

            noTripForUserText { assertIsNotDisplayed() }
            noTripFoundOnSearchText { assertIsNotDisplayed() }

            buttonTrip1 { assertIsDisplayed() }
            buttonTrip2 { assertIsDisplayed() }
            buttonTrip3 { assertIsNotDisplayed() }

            joinTripButton { assertIsDisplayed() }
            createTripButton { assertIsDisplayed() }
        }
    }

    @Test
    fun noTripForUserMessageIsDisplayed() = run {
        ComposeScreen.onComposeScreen<OverviewScreen>(composeTestRule) {
            overviewViewModelTest.deleteTrips()

            overviewScreen { assertIsDisplayed() }

            dockedSearchBar { assertIsDisplayed() }
            clearSearchButton { assertIsNotDisplayed() }

            noTripForUserText { assertIsDisplayed() }
            noTripFoundOnSearchText { assertIsNotDisplayed() }

            buttonTrip1 { assertIsNotDisplayed() }
            buttonTrip2 { assertIsNotDisplayed() }
            buttonTrip3 { assertIsNotDisplayed() }

            joinTripButton { assertIsDisplayed() }
            createTripButton { assertIsDisplayed() }
        }
    }

    @Test
    fun clearSearchButtonResetsInitialView() = run {
        ComposeScreen.onComposeScreen<OverviewScreen>(composeTestRule) {
            overviewScreen { assertIsDisplayed() }

            dockedSearchBar { assertIsDisplayed() }

            clearSearchButton { assertIsNotDisplayed() }
            composeTestRule.onNodeWithText("Search a trip").performTextInput("City Exploration")
            clearSearchButton { performClick() }
            clearSearchButton { assertIsNotDisplayed() }

            noTripForUserText { assertIsNotDisplayed() }
            noTripFoundOnSearchText { assertIsNotDisplayed() }

            buttonTrip1 { assertIsDisplayed() }
            buttonTrip2 { assertIsDisplayed() }
            buttonTrip3 { assertIsDisplayed() }

            joinTripButton { assertIsDisplayed() }
            createTripButton { assertIsDisplayed() }
        }
    }

    @Test
    fun createTripButtonNavigateToCreateTripView() = run {
        ComposeScreen.onComposeScreen<OverviewScreen>(composeTestRule) {
            createTripButton {
                assertIsDisplayed()
                performClick()
            }
            verify { mockNavActions.navigateTo(Route.CREATE_TRIP) }
            confirmVerified(mockNavActions)
        }
    }

    @Test
    fun tripButtonInTripOverViewNavigatesToTheTripView() = run {
        ComposeScreen.onComposeScreen<OverviewScreen>(composeTestRule) {
            buttonTrip1 {
                assertIsDisplayed()
                performClick()
            }
            verify { mockNavActions.navigateTo(Route.TRIP + "/1") }
            confirmVerified(mockNavActions)
        }
    }
}
