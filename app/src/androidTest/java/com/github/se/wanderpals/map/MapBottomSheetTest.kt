package com.github.se.wanderpals.map

import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.ViewModel
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.screens.MapBottomSheetScreen
import com.github.se.wanderpals.ui.screens.trip.map.MapBottomSheet
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.Test

class FakeMapBottomSheetViewModel : ViewModel() {

  private val place =
      GeoCords(
          placeName = "placeName",
          placeBusinessStatus = "placeBusinessStatus",
          placeAddress = "placeAddress",
          placePhoneNumber = "placePhoneNumber",
          placeRating = "4.5",
          placeUserRatingsTotal = "100",
          placeWebsite = "placeWebsite")

  private val emptyPlace =
      GeoCords(
          placeName = "",
          placeBusinessStatus = "",
          placeAddress = "",
          placePhoneNumber = "",
          placeRating = "",
          placeUserRatingsTotal = "",
          placeWebsite = "")

  fun getPlace(empty: Boolean): GeoCords {
    return if (empty) emptyPlace else place
  }
}

class MapBottomSheetTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun notDisplayEmptyPlace() {
    lateinit var bottomSheetScaffoldState: BottomSheetScaffoldState
    val viewModel = FakeMapBottomSheetViewModel()
    composeTestRule.setContent {
      val scope = rememberCoroutineScope()
      bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
      MapBottomSheet(viewModel.getPlace(true), bottomSheetScaffoldState, LocalUriHandler.current)
      scope.launch { bottomSheetScaffoldState.bottomSheetState.expand() }
    }
    ComposeScreen.onComposeScreen<MapBottomSheetScreen>(composeTestRule) {
      placeName.assertIsNotDisplayed()
      placeBusinessStatus.assertIsNotDisplayed()
      placeAddress.assertIsNotDisplayed()
      placePhoneNumber.assertIsNotDisplayed()
      placeRating.assertIsNotDisplayed()
      placeUserRatingsTotal.assertIsNotDisplayed()
      placeWebsite.assertIsNotDisplayed()
    }
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun isDisplayed() {
    lateinit var bottomSheetScaffoldState: BottomSheetScaffoldState
    val viewModel = FakeMapBottomSheetViewModel()
    composeTestRule.setContent {
      val scope = rememberCoroutineScope()
      bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
      MapBottomSheet(viewModel.getPlace(false), bottomSheetScaffoldState, LocalUriHandler.current)
      scope.launch { bottomSheetScaffoldState.bottomSheetState.expand() }
    }
    ComposeScreen.onComposeScreen<MapBottomSheetScreen>(composeTestRule) {
      placeName.assertIsDisplayed()
      placeBusinessStatus.assertIsDisplayed()
      placeAddress.assertIsDisplayed()
      placePhoneNumber.assertIsDisplayed()
      placeRating.assertIsDisplayed()
      placeUserRatingsTotal.assertIsDisplayed()
      placeWebsite.assertIsDisplayed()
    }
  }
}
