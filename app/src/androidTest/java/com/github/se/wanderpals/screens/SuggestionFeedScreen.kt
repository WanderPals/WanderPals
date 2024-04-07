package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SuggestionFeedScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SuggestionFeedScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("suggestionFeedScreen") }) {

  val suggestionFeedScreen: KNode = onNode { hasTestTag("suggestionFeedScreen") }

  //    val dockedSearchBar: KNode = onNode { hasTestTag("dockedSearchBar") } //todo: for sprint 3
  //    val clearSearchButton: KNode = onNode { hasTestTag("clearSearchButton") } //todo: for sprint
  // 3

  val noSuggestionsForUserText: KNode = onNode { hasTestTag("noSuggestionsForUserText") }
  //    val noTripFoundOnSearchText: KNode = onNode { hasTestTag("noTripFoundOnSearchText") }
  // //todo: for sprint 3

  val suggestion1: KNode = onNode { hasTestTag("suggestion1") }
  val suggestion2: KNode = onNode { hasTestTag("suggestion2") }
  val suggestion3: KNode = onNode { hasTestTag("suggestion3") }

  // for When clicking the create suggestion button, the navigation action is triggered
  //    val createSuggestionButton: KNode = onNode { hasTestTag("createSuggestionButton") } //todo:
  // this is after William

  val suggestionButtonExists: KNode = onNode { hasTestTag("suggestionButtonExists") }
}
