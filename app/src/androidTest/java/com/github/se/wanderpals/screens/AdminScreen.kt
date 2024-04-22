package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class AdminScreen( semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<AdminScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = {hasTestTag("adminScreen")}
    ){
    val adminScreenCard: KNode = onNode { hasTestTag("adminScreenCard") }
    val IconAdminScreen: KNode = onNode { hasTestTag("IconAdminScreen") }
    val AdminTitle: KNode = onNode { hasTestTag("AdminTitle") }
    val AdminDivider: KNode = onNode { hasTestTag("AdminDivider") }
    val userName = onNode { hasTestTag("userName") }
    val editRoleButton = onNode { hasTestTag("editRoleButton") }
    val deleteUserButton = onNode { hasTestTag("deleteUserButton") }
    val confirmDeleteUserButton = onNode { hasTestTag("confirmDeleteUserButton") }
    val cancelDeleteCommentButton = onNode { hasTestTag("cancelDeleteCommentButton") }
    val changeRoleTitle = onNode { hasTestTag("changeRoleTitle") }
    val radioButton = onNode { hasTestTag("radioButton") }
    val stringRole = onNode { hasTestTag("stringRole") }
    val ConfirmRoleChangeButton = onNode { hasTestTag("ConfirmRoleChangeButton") }


}