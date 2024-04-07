package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class CreateTripoScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateTripoScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("createTripScreen") }) {

  val screenTitle: KNode = onNode { hasTestTag("createTripTitle") }

  val goBackButton: KNode = onNode { hasTestTag("goBackButton") }
  val saveButton: KNode = onNode { hasTestTag("tripSave") }

  val inputTitle: KNode = onNode { hasTestTag("inputTripTitle") }
  val inputBudget: KNode = onNode { hasTestTag("inputTripBudget") }
  val inputStartDate: KNode = onNode { hasTestTag("inputTripStartDate") }
  val inputEndDate: KNode = onNode { hasTestTag("inputTripEndDate") }
  val inputDescription: KNode = onNode { hasTestTag("inputTripDescription") }

  val titleLengthText : KNode = onNode { hasTestTag("titleLengthText") }
}
