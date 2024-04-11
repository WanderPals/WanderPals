package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class OverviewScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<OverviewScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("overviewScreen") }) {

  val overviewScreen: KNode = onNode { hasTestTag("overviewScreen") }

  val dockedSearchBar: KNode = onNode { hasTestTag("dockedSearchBar") }
  val clearSearchButton: KNode = onNode { hasTestTag("clearSearchButton") }

  val noTripForUserText: KNode = onNode { hasTestTag("noTripForUserText") }
  val noTripFoundOnSearchText: KNode = onNode { hasTestTag("noTripFoundOnSearchText") }

  val buttonTrip1: KNode = onNode { hasTestTag("buttonTrip1") }
  val buttonTrip2: KNode = onNode { hasTestTag("buttonTrip2") }
  val buttonTrip3: KNode = onNode { hasTestTag("buttonTrip3") }

  val joinTripButton: KNode = onNode { hasTestTag("joinTripButton") }
  val createTripButton: KNode = onNode { hasTestTag("createTripButton") }

  val shareTripButton1: KNode = onNode { hasTestTag("shareTripButton1") }
  val shareTripButton2: KNode = onNode { hasTestTag("shareTripButton2") }
  val shareTripButton3: KNode = onNode { hasTestTag("shareTripButton3") }

  val dialog: KNode = onNode { hasTestTag("dialog") }
}
