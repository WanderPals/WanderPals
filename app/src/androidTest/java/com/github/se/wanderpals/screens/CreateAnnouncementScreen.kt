package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class CreateAnnouncementScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateAnnouncementScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("createAnnouncementScreen") }) {

  val createAnnouncementButton: KNode = onNode { hasTestTag("createAnnouncementButton") }
  val tripAnnouncementGoBackButton: KNode = onNode { hasTestTag("tripAnnouncementGoBackButton") }

  val inputAnnouncementTitle: KNode = onNode { hasTestTag("inputAnnouncementTitle") }
  val inputAnnouncementDescription: KNode = onNode { hasTestTag("inputAnnouncementDescription") }

  val announcementTitleLengthText: KNode = onNode { hasTestTag("announcementTitleLengthText") }
}
