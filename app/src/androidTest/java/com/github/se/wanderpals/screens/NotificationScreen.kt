package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class NotificationScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<NotificationScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("notificationScreen") }) {

  val notificationButton: KNode = onNode { hasTestTag("notificationButton") }

  val announcementButton: KNode = onNode { hasTestTag("announcementButton") }

  val createAnnouncementButton: KNode = onNode { hasTestTag("createAnnouncementButton") }
}
