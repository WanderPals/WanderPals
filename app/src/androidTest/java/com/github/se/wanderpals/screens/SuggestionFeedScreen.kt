package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SuggestionFeedScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SuggestionFeedScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("suggestionFeedScreen") }) {

  val suggestionFeedScreen: KNode = onNode { hasTestTag("suggestionFeedScreen") }

  val suggestionSearchBar: KNode = onNode { hasTestTag("suggestionSearchBar") }
  val clearSuggestionSearchButton: KNode = onNode { hasTestTag("clearSuggestionSearchButton") }

  val noSuggestionsForUserText: KNode = onNode { hasTestTag("noSuggestionsForUserText") }

  val suggestion1: KNode = onNode { hasTestTag("suggestion1") } // the first suggestion
  val suggestion2: KNode = onNode { hasTestTag("suggestion2") }
  val suggestion3: KNode = onNode { hasTestTag("suggestion3") }

  val suggestionButtonExists: KNode = onNode { hasTestTag("suggestionButtonExists") }

  val suggestionSortingOptions: KNode = onNode { hasTestTag("suggestionSortingOptions") }
  val suggestionSortingButton: KNode = onNode { hasTestTag("suggestionSortingButton") }
}
