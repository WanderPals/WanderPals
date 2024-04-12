package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SuggestionFeedScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SuggestionFeedScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("suggestionFeedScreen") }) {

  val suggestionFeedScreen: KNode = onNode { hasTestTag("suggestionFeedScreen") }

    val searchPlaceholder: KNode = onNode { hasTestTag("searchPlaceholder") }
      val suggestionSearchBar: KNode = onNode { hasTestTag("suggestionSearchBar") }
    val clearSuggestionSearchButton: KNode = onNode { hasTestTag("clearSuggestionSearchButton") }

  val noSuggestionsForUserText: KNode = onNode { hasTestTag("noSuggestionsForUserText") }
      val noTripFoundOnSearchText: KNode = onNode { hasTestTag("noTripFoundOnSearchText") }
  // //todo: for sprint 3

  val suggestion1: KNode = onNode { hasTestTag("suggestion1") }
  val suggestion2: KNode = onNode { hasTestTag("suggestion2") }
  val suggestion3: KNode = onNode { hasTestTag("suggestion3") }

  // for When clicking the create suggestion button, the navigation action is triggered
  //    val createSuggestionButton: KNode = onNode { hasTestTag("createSuggestionButton") } //todo:
  // this is after William

  val suggestionButtonExists: KNode = onNode { hasTestTag("suggestionButtonExists") }

    val filterByCreationDateButton: KNode = onNode { hasTestTag("filterByCreationDateButton") }
    val filterByLikeNumberButton: KNode = onNode { hasTestTag("filterByLikeNumberButton") }
    val filterByCommentNumberButton: KNode = onNode { hasTestTag("filterByCommentNumberButton") }

    // Test tags for like counts
    // 1, 2, 3 is the position of the item in the list
    val suggestion1LikesCount: KNode = onNode { hasTestTag("suggestion1LikesCount") }
    val suggestion2LikesCount: KNode = onNode { hasTestTag("suggestion2LikesCount") }
    val suggestion3LikesCount: KNode = onNode { hasTestTag("suggestion3LikesCount") }

    val suggestionFilterOptions: KNode = onNode { hasTestTag("suggestionFilterOptions") }
    val suggestionFilterButton: KNode = onNode { hasTestTag("suggestionFilterButton") }
}
//use SuggestionFeedContent.kt
