package com.github.se.wanderpals.ui.screens.trip.finance
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.se.wanderpals.model.viewmodel.FinanceViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions

@Composable
fun ExpenseInfo(financeViewModel: FinanceViewModel,navigationActions : NavigationActions){
    val selectedExpense by financeViewModel.selectedExpense.collectAsState()
    val expense = selectedExpense!!
    Text(expense.expenseId)
}


