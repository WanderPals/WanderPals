package com.github.se.wanderpals.ui.screens.trip.finance

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import com.github.se.wanderpals.model.viewmodel.FinanceViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions


enum class FinanceOption(private val optionName: String) {
    EXPENSES("Expenses"),
    CATEGORIES("Categories"),
    DEBTS("Debts");

    override fun toString(): String {
        return this.optionName
    }
}


/** The Finance screen. */
@Composable
fun Finance(financeViewModel: FinanceViewModel, navigationActions: NavigationActions) {

    var currentSelectedOption by remember { mutableStateOf(FinanceOption.EXPENSES) }

    val expenseList by financeViewModel.expenseStateList.collectAsState()


    Scaffold(
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
            if (currentSelectedOption == FinanceOption.EXPENSES) {
                FloatingActionButton(
                    onClick = { /* TODO navigate to create expense screen*/ },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        Icons.Default.Add.name,
                        modifier = Modifier.size(35.dp),
                        tint = Color.White
                    )
                }
            }
        }

    ) { innerPadding ->
        LaunchedEffect(Unit) {
            financeViewModel.updateStateLists()
        }
        when (currentSelectedOption) {
            FinanceOption.EXPENSES -> {
                ExpensesContent(
                    innerPadding = innerPadding,
                    expenseList = expenseList
                )
            }

            FinanceOption.CATEGORIES -> {
                // to be implemented
            }

            FinanceOption.DEBTS -> {
                // to be implemented
            }
        }
    }
}


