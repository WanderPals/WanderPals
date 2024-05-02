package com.github.se.wanderpals.ui.screens.trip.finance

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.viewmodel.FinanceViewModel
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.navigation.NavigationActions

/**
 * Enum class representing different finance options available in the Finance screen.
 *
 * @param optionName The name of the finance option.
 */
enum class FinanceOption(private val optionName: String) {
  EXPENSES("Expenses"),
  CATEGORIES("Categories"),
  DEBTS("Debts");

  override fun toString(): String {
    return this.optionName
  }
}

/**
 * Composable function representing the Finance screen. The screen contains the following view for
 * the user :
 * - Expenses view which is an history of the expenses during the trip
 * - Categories view allows the user to watch the categories of expense during the trip
 * - Debts view display the balances between each member of the trip
 *
 * @param financeViewModel The ViewModel for finance-related data.
 * @param navigationActions The navigation actions for the Finance screen.
 */
@Composable
fun Finance(financeViewModel: FinanceViewModel, navigationActions: NavigationActions) {

  var currentSelectedOption by remember { mutableStateOf(FinanceOption.EXPENSES) }

  val expenseList by financeViewModel.expenseStateList.collectAsState()

  Scaffold(
      modifier = Modifier.testTag("financeScreen"),
      topBar = {
        FinanceTopBar(
            currentSelectedOption = currentSelectedOption,
            onSelectOption = { newOption -> currentSelectedOption = newOption })
      },
      bottomBar = {
        if (currentSelectedOption == FinanceOption.EXPENSES) {
          FinanceBottomBar()
        }
      },
      floatingActionButton = {
        if (currentSelectedOption == FinanceOption.EXPENSES &&
            SessionManager.getCurrentUser()!!.role != Role.VIEWER) {
          FloatingActionButton(
              modifier = Modifier.testTag("financeFloatingActionButton"),
              onClick = { /* TODO navigate to create expense screen*/},
              containerColor = MaterialTheme.colorScheme.primary,
              shape = RoundedCornerShape(50.dp)) {
                Icon(
                    imageVector = Icons.Default.Add,
                    Icons.Default.Add.name,
                    modifier = Modifier.size(35.dp),
                    tint = Color.White)
              }
        }
      }) {
          // Content
          innerPadding ->
        LaunchedEffect(Unit) { financeViewModel.updateStateLists() }
        when (currentSelectedOption) {
          FinanceOption.EXPENSES -> {
            ExpensesContent(innerPadding = innerPadding, expenseList = expenseList)
          }
          FinanceOption.CATEGORIES -> {
            Box(modifier = Modifier.fillMaxSize()) {
              Text(
                  modifier = Modifier.align(Alignment.Center),
                  text = "Not available yet. ",
              )
            }
          }
          FinanceOption.DEBTS -> {
            Box(modifier = Modifier.fillMaxSize()) {
              Text(
                  modifier = Modifier.align(Alignment.Center),
                  text = "Not available yet. ",
              )
            }
          }
        }
      }
}
