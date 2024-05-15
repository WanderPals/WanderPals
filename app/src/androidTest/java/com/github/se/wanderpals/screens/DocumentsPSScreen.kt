package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class DocumentsPSScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<DocumentsPSScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("documentsScreen") }) {
  val tabPrivate = onNode { hasTestTag("tabPrivate") }
  val tabShared = onNode { hasTestTag("tabShared") }
  val floatingActionButton = onNode { hasTestTag("addDocumentButton") }
  val document0 = onNode { hasTestTag("document0") }
  val document1 = onNode { hasTestTag("document1") }
  val document2 = onNode { hasTestTag("document2") }
  val documentUser0 = onNode { hasTestTag("documentUser0") }
  val documentUser1 = onNode { hasTestTag("documentUser1") }
  val documentImage = onNode { hasTestTag("documentImage") }
  val documentImageBox = onNode { hasTestTag("documentImageBox") }
}