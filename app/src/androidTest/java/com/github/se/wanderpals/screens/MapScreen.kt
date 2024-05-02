package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class MapScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<MapScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("mapScreen") }) {

  val switchButton: KNode = onNode { hasTestTag("switchButton") }
  val searchBar: KNode = onNode { hasTestTag("searchBar") }
  val googleMap: KNode = onNode { hasTestTag("googleMap") }
  val menuButtonIcon: KNode = onNode { hasTestTag("menuButtonIcon") }
  val clearSearchButton: KNode = onNode { hasTestTag("clearSearchButton") }
  val searchButtonIcon: KNode = onNode { hasTestTag("searchButtonIcon") }
  val clearSearchButtonIcon: KNode = onNode { hasTestTag("clearSearchButtonIcon") }
  val listOfPropositions: KNode = onNode { hasTestTag("listOfPropositions") }
  val clearMarkersButton = onNode { hasTestTag("clearMarkersButton") }
}
