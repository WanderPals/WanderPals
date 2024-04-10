package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class DashboardScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<DashboardScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("dashboardScreen") }) {

  val overviewScreen: KNode = onNode { hasTestTag("iyiy") }
}
