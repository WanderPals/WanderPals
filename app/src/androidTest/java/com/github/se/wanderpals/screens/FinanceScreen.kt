package com.github.se.wanderpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class FinanceScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<FinanceScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("financeScreen") }) {

  val financeBackButton: KNode = onNode { hasTestTag("financeBackButton") }
  val financeTopBar: KNode = onNode { hasTestTag("financeTopBar") }
  val expensesButton: KNode = onNode { hasTestTag("ExpensesButton") }
  val categoriesButton: KNode = onNode { hasTestTag("CategoriesButton") }
  val debtsButton: KNode = onNode { hasTestTag("DebtsButton") }
  val financeBottomBar: KNode = onNode { hasTestTag("financeBottomBar") }
  val financeFloatingActionButton: KNode = onNode { hasTestTag("financeFloatingActionButton") }
  val expensesContent: KNode = onNode { hasTestTag("expensesContent") }
  val noExpensesTripText: KNode = onNode { hasTestTag("noExpensesTripText") }
}
