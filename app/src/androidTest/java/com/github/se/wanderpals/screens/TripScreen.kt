package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class TripScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<TripScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("tripScreen") }) {

  val tripScreen: KNode = onNode { hasTestTag("tripScreen") }
  val suggestionScreen: KNode = onNode { hasTestTag("suggestionFeedScreen") }
  val agendaScreen: KNode = onNode { hasTestTag("agendaScreen") }
  val dashboardScreen: KNode = onNode { hasTestTag("dashboardScreen") }
  val mapScreen: KNode = onNode { hasTestTag("mapScreen") }
  val notificationScreen: KNode = onNode { hasTestTag("notificationScreen") }

  val bottomNav: KNode = onNode { hasTestTag("bottomNav") }
  val menuButton: KNode = onNode { hasTestTag("menuButton") }
  val menuNav: KNode = onNode { hasTestTag("menuNav") }
  val overviewButtonMenuItem: KNode = menuNav.child { hasTestTag("backToOverview") }

  val suggestionItem: KNode = bottomNav.child { hasTestTag("Suggestion") }
  val agendaItem: KNode = bottomNav.child { hasTestTag("Agenda") }
  val dashboardItem: KNode = bottomNav.child { hasTestTag("Dashboard") }
  val mapItem: KNode = bottomNav.child { hasTestTag("Map") }
  val notificationItem: KNode = bottomNav.child { hasTestTag("Notification") }
}
