package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class CreateNotificationScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateNotificationScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("createNotificationScreen") }) {

  val createNotificationButton: KNode = onNode { hasTestTag("createNotificationButton") }
  val tripNotifGoBackButton: KNode = onNode { hasTestTag("tripNotifGoBackButton") }

  val inputNotificationTitle: KNode = onNode { hasTestTag("inputNotificationTitle") }
  val inputNotificationDescription: KNode = onNode { hasTestTag("inputNotificationDescription") }

  val notifTitleLengthText: KNode = onNode { hasTestTag("notifTitleLengthText") }
}
