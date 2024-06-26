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

  val sendTripButton1: KNode = onNode { hasTestTag("sendTripButton1") }
  val sendTripButton2: KNode = onNode { hasTestTag("sendTripButton2") }
  val sendTripButton3: KNode = onNode { hasTestTag("sendTripButton3") }

  val dialog: KNode = onNode { hasTestTag("dialog") }
  val emailDialog: KNode = onNode { hasTestTag("emailDialog") }
  val logoutDialog: KNode = onNode { hasTestTag("logoutDialog") }
  val confirmLogoutButton: KNode = onNode { hasTestTag("confirmLogoutButton") }
  val cancelLogoutButton: KNode = onNode { hasTestTag("cancelLogoutButton") }
  val profilePhoto: KNode = onNode { hasTestTag("profilePhoto") }
}
