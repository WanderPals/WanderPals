package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SignIn(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SignIn>(semanticsProvider = semanticsProvider) {

  val loginButton: KNode = child { hasTestTag("LoginButton") }
}
