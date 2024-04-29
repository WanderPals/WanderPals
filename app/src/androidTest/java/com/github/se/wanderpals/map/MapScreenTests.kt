package com.github.se.wanderpals.map

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.MapViewModel
import com.github.se.wanderpals.screens.MapScreen
import com.github.se.wanderpals.service.MapManager
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.screens.trip.map.Map
import com.github.se.wanderpals.ui.screens.trip.map.PlaceData
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// create test Map View Model
/**
 * Fake MapViewModel for testing the Map screen. This class is used to create a fake MapViewModel
 * for testing the Map screen.
 */
object FakeMapViewModel : MapViewModel(TripsRepository("-1", dispatcher = Dispatchers.IO), "id") {

  // create 2 fake stops
  private val stop1 =
      Stop(
          "1",
          "stop1",
          address = "Paris",
          date = LocalDate.now(),
          startTime = LocalTime.now(),
          duration = 2,
          budget = 100.0,
          description = "stop1 description",
          geoCords = GeoCords(48.8566, 2.3522))
  private val stop2 =
      Stop(
          "2",
          "stop2",
          address = "London",
          date = LocalDate.now(),
          startTime = LocalTime.now(),
          duration = 2,
          budget = 100.0,
          description = "stop2 description",
          geoCords = GeoCords(51.5074, 0.1278))

  override var stops = MutableStateFlow(listOf(stop1, stop2))

  override var listOfTempPlaceData = MutableStateFlow(emptyList<PlaceData>())

  override fun getAllSuggestions() {}

  override fun getAllStops() {}

  override fun getAllUsersPositions() {}

  override fun getAllPlaceData() {}

  override fun savePlaceDataState(placeData: PlaceData) {}

  override fun clearAllSharedPreferences() {}
}
/** Test class for the Map screen. This class contains the tests for the Map screen. */
class MapScreenTests : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  // test case
  @Before
  fun testSetup() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val mapManager = MapManager(context)
    mapManager.initClients()
    composeTestRule.setContent { Map(mockNavActions, mapViewModel = FakeMapViewModel, mapManager) }
  }

  // test case 1: switch button is displayed
  @Test
  fun switchButtonIsDisplayed() {
    ComposeScreen.onComposeScreen<MapScreen>(composeTestRule) {
      switchButton { assertIsDisplayed() }
    }
  }

  // test case 2: when we switch the search bar is not displayed
  @Test
  fun searchBarIsNotDisplayedWhenSwitchButtonIsClicked() {
    ComposeScreen.onComposeScreen<MapScreen>(composeTestRule) {
      switchButton { performClick() }
      searchBar { assertIsNotDisplayed() }
    }
  }
  // test case 3: when we switch the search bar is displayed
  @Test
  fun searchBarIsDisplayedWhenSwitchButtonIsClicked() {
    ComposeScreen.onComposeScreen<MapScreen>(composeTestRule) {
      switchButton {
        performClick()
        performClick()
      }
      searchBar { assertIsDisplayed() }
    }
  }
  // test case 4: display map
  @Test
  fun mapIsDisplayed() {
    ComposeScreen.onComposeScreen<MapScreen>(composeTestRule) { googleMap { assertIsDisplayed() } }
  }

  // test case 6: clear search button is displayed
  @Test
  fun clearSearchButtonIsDisplayed() {
    ComposeScreen.onComposeScreen<MapScreen>(composeTestRule) {
      composeTestRule.onNodeWithText("Search a location").performTextInput("Paris")
      clearSearchButton { assertIsDisplayed() }
    }
  }
  // test7: when we write on the searchbar the clear button clear the search
  @Test
  fun clearSearchButtonClearsSearch() {
    ComposeScreen.onComposeScreen<MapScreen>(composeTestRule) {
      composeTestRule.onNodeWithText("Search a location").performTextInput("Paris")
      clearSearchButton { performClick() }
      composeTestRule.onNodeWithText("Search a location").assertExists()
    }
  }
  // test8: when we write on the searchbar the search location is on the list of propositions
  @Test
  fun searchLocationIsOnListOfPropositions() {
    ComposeScreen.onComposeScreen<MapScreen>(composeTestRule) {
      composeTestRule.onNodeWithText("Search a location").performTextInput("Pari")
      listOfPropositions { assertIsDisplayed() }
    }
  }
  // test 9: when we search we have the proposition of the location, we can click on the proposition
  // Paris
  @Test
  fun checkThePropositionIsDisplayed() {
    ComposeScreen.onComposeScreen<MapScreen>(composeTestRule) {
      composeTestRule.onNodeWithText("Search a location").performTextInput("Pari")
      listOfPropositions { assertIsDisplayed() }
      composeTestRule.onNodeWithText("ParisZurich Traiteur").isDisplayed()
    }
  }

  // test 10: when we have an empty list of listOfTempMarkerStates the button clear is not displayed
  @Test
  fun clearMarkersButtonIsNotDisplayedWhenEmpty() {
    ComposeScreen.onComposeScreen<MapScreen>(composeTestRule) {
      clearMarkersButton { assertIsNotDisplayed() }
    }
  }

  // test 11: when we have a list of listOfTempMarkerStates the button clear is displayed
  @Test
  fun clearMarkersButtonIsDisplayedWhenNotEmpty() {
    ComposeScreen.onComposeScreen<MapScreen>(composeTestRule) {
      FakeMapViewModel.listOfTempPlaceData.value = listOf(PlaceData(placeName = "Paris"))
      clearMarkersButton { assertIsDisplayed() }
    }
  }
}
