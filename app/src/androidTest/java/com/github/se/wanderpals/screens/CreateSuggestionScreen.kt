package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class CreateSuggestionScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateSuggestionScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("createSuggestionScreen") }) {

  val createButton: KNode = onNode { hasTestTag("createSuggestionButton") }
  val goBackButton: KNode = onNode { hasTestTag("goBackButton") }

  val inputTitle: KNode = onNode { hasTestTag("inputSuggestionTitle") }
  val inputBudget: KNode = onNode { hasTestTag("inputSuggestionBudget") }
  val inputStartDate: KNode = onNode { hasTestTag("inputSuggestionStartDate") }
  val inputStartTime: KNode = onNode { hasTestTag("inputSuggestionStartTime") }
  val inputEndDate: KNode = onNode { hasTestTag("inputSuggestionEndDate") }
  val inputEndTime: KNode = onNode { hasTestTag("inputSuggestionEndTime") }
  val inputDescription: KNode = onNode { hasTestTag("inputSuggestionDescription") }
  val inputAddress: KNode = onNode { hasTestTag("inputSuggestionAddress") }
  val inputWebsite: KNode = onNode { hasTestTag("inputSuggestionWebsite") }
    val suggestionButtonExists: KNode = onNode { hasTestTag("suggestionButtonExists") }
}
