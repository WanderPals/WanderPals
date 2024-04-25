package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class MapBottomSheetScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<MapBottomSheetScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("mapBottomSheet") }) {

  val placeName: KNode = onNode { hasTestTag("placeName") }
  val placeBusinessStatus: KNode = onNode { hasTestTag("placeBusinessStatus") }
  val placeAddress: KNode = onNode { hasTestTag("placeAddress") }
  val placePhoneNumber: KNode = onNode { hasTestTag("placePhoneNumber") }
  val placeRating: KNode = onNode { hasTestTag("placeRating") }
  val placeUserRatingsTotal: KNode = onNode { hasTestTag("placeUserRatingsTotal") }
  val placeWebsite: KNode = onNode { hasTestTag("placeWebsite") }
}
