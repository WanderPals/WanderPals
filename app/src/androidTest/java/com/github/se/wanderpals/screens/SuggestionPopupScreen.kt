package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SuggestionPopupScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SuggestionPopupScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("suggestionPopupScreen") }) {

  val suggestionPopupScreen: KNode = onNode { hasTestTag("suggestionPopupScreen") }
  val suggestionPopupTitle: KNode = onNode { hasTestTag("suggestionPopupTitle") }
  val suggestionPopupCommentsIcon: KNode = onNode { hasTestTag("suggestionPopupCommentsIcon") }
  val suggestionPopupCommentsNumber: KNode = onNode { hasTestTag("suggestionPopupCommentsNumber") }
  val suggestionPopupLikesIcon: KNode = onNode { hasTestTag("suggestionPopupLikesIcon") }
  val suggestionPopupLikesNumber: KNode = onNode { hasTestTag("suggestionPopupLikesNumber") }

  val suggestionPopupUserName: KNode = onNode { hasTestTag("suggestionPopupUserName") }
  val suggestionPopupDate: KNode = onNode { hasTestTag("suggestionPopupDate") }

  val suggestionPopupDescription: KNode = onNode { hasTestTag("suggestionPopupDescription") }
  val suggestionPopupDescriptionText: KNode = onNode {
    hasTestTag("suggestionPopupDescriptionText")
  }

  val suggestionPopupStartDateTimeEndDateTime: KNode = onNode {
    hasTestTag("suggestionPopupStartDateTimeEndDateTime")
  }
  //  val suggestionPopupStartTime: KNode = onNode { hasTestTag("suggestionPopupStartTime") }
  //  val suggestionPopupEndDate: KNode = onNode { hasTestTag("suggestionPopupEndDate") }
  //  val suggestionPopupEndTime: KNode = onNode { hasTestTag("suggestionPopupEndTime") }

  val suggestionPopupComments: KNode = onNode { hasTestTag("suggestionPopupComments") }
  val noSuggestionCommentList: KNode = onNode { hasTestTag("noSuggestionCommentList") }
  val suggestionComment1: KNode = onNode { hasTestTag("suggestionComment1") }
  val suggestionComment2: KNode = onNode { hasTestTag("suggestionComment2") }
  val suggestionComment3: KNode = onNode { hasTestTag("suggestionComment3") }
  val suggestionComment4: KNode = onNode { hasTestTag("suggestionComment4") }
  val suggestionPopupDivider: KNode = onNode { hasTestTag("suggestionPopupDivider") }

  //        val suggestionPopupLikeButton: KNode = onNode { hasTestTag("suggestionPopupLikeButton")
  // } //to like a suggestion

  val suggestion1: KNode = onNode {
    hasTestTag("suggestion1")
  } // to select/onClick the first suggestion

  val suggestionPopupAddrTextNotEmpty: KNode = onNode {
    hasTestTag("suggestionPopupAddrTextNotEmpty")
  }
  val suggestionPopupAddr: KNode = onNode { hasTestTag("suggestionPopupAddr") }
  val suggestionPopupAddrTextEmpty: KNode = onNode { hasTestTag("suggestionPopupAddrTextEmpty") }

  val suggestionPopupWebsiteTextNotEmpty: KNode = onNode {
    hasTestTag("suggestionPopupWebsiteTextNotEmpty")
  }
  val suggestionPopupWebsite: KNode = onNode { hasTestTag("suggestionPopupWebsite") }
  val suggestionPopupWebsiteTextEmpty: KNode = onNode {
    hasTestTag("suggestionPopupWebsiteTextEmpty")
  }
}
