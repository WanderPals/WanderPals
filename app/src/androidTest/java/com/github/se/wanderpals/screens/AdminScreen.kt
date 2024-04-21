package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class AdminScreen( semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<AdminScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("adminScreen") }){
val adminScreen: KNode = onNode { hasTestTag("adminScreen") }
val userList: KNode = onNode { hasTestTag("userList") }
val user1: KNode = onNode { hasTestTag("user1") }


}