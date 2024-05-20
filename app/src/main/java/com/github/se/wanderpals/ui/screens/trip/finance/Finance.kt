package com.github.se.wanderpals.ui.screens.trip.finance

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text2.input.TextFieldCharSequence
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.viewmodel.FinanceViewModel
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import android.icu.util.Currency
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow


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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Finance(financeViewModel: FinanceViewModel, navigationActions: NavigationActions) {

    var currentSelectedOption by remember { mutableStateOf(FinanceOption.EXPENSES) }

    val expenseList by financeViewModel.expenseStateList.collectAsState()
    val users by financeViewModel.users.collectAsState()

    val currencyDialogIsOpen by financeViewModel.showCurrencyDialog.collectAsState()
    var selectedCurrency by remember { mutableStateOf("CHF") }
    var expanded by remember { mutableStateOf(false) }

    val currencies = Currency.getAvailableCurrencies().filterNot{it.displayName.contains("(")}

    LaunchedEffect(Unit) {
        financeViewModel.updateStateLists()
        financeViewModel.loadMembers(navigationActions.variables.currentTrip)
    }

    Scaffold(
        modifier = Modifier.testTag("financeScreen"),
        topBar = {
            FinanceTopBar(
                currentSelectedOption = currentSelectedOption,
                onSelectOption = { newOption -> currentSelectedOption = newOption },
                onCurrencyClick = { financeViewModel.setShowCurrencyDialogState(true) })
        },
        bottomBar = {
            if (currentSelectedOption == FinanceOption.EXPENSES) {
                FinanceBottomBar(expenseList)
            }
        },
        floatingActionButton = {
            if (currentSelectedOption == FinanceOption.EXPENSES &&
                SessionManager.getCurrentUser()!!.role != Role.VIEWER &&
                SessionManager.getIsNetworkAvailable()
            ) {
                FloatingActionButton(
                    modifier = Modifier.testTag("financeFloatingActionButton"),
                    onClick = { navigationActions.navigateTo(Route.CREATE_EXPENSE) },
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
        }) {
        // Content
            innerPadding ->
        if (currencyDialogIsOpen) {
            Dialog(
                onDismissRequest = { financeViewModel.setShowCurrencyDialogState(false) }) {
                Surface(
                    modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp, bottom = 100.dp),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        TextField(
                            value = selectedCurrency,
                            onValueChange = { value -> selectedCurrency = value },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .border(
                                    1.dp, Color.DarkGray, RoundedCornerShape(5.dp),
                                ),
                            placeholder = { Text(text = "Search a currency ") },
                            colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    Icons.Default.CheckCircle.name,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            maxLines = 1,
                        )
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                           val filteredCurrencies =
                               currencies
                                   .toList()
                                   .filter {
                                       it.displayName.contains(selectedCurrency,ignoreCase = true) ||
                                       it.currencyCode.contains(selectedCurrency,ignoreCase = true)
                                        }
                                   .sortedBy { it.displayName }
                           items(filteredCurrencies){ currency ->
                               Box( modifier = Modifier
                                   .fillMaxWidth()
                                   .padding(horizontal = 20.dp)
                                   .clip(RoundedCornerShape(5.dp))
                                   .border(
                                       1.dp, Color.Gray, RoundedCornerShape(5.dp),
                                   ),
                                   contentAlignment = Alignment.CenterStart){
                                   Button(
                                       modifier = Modifier.fillMaxWidth(),
                                       colors =
                                       ButtonDefaults.buttonColors(
                                           containerColor = Color.Transparent),
                                       onClick = { selectedCurrency = currency.displayName }) {

                                   }
                                   Text(
                                       modifier = Modifier.padding(start = 5. dp),
                                       text = currency.displayName,
                                       textAlign = TextAlign.Start,
                                       maxLines = 1,
                                       overflow = TextOverflow.Ellipsis)
                               }
                               Spacer(modifier = Modifier.height(7.dp))
                            }
                        }
                    }
                }
            }
        }
        when (currentSelectedOption) {
            FinanceOption.EXPENSES -> {
                ExpensesContent(
                    innerPadding = innerPadding,
                    expenseList = expenseList,
                    onRefresh = { financeViewModel.updateStateLists() },
                    onExpenseItemClick = {
                        navigationActions.setVariablesExpense(it)
                        navigationActions.navigateTo(Route.EXPENSE_INFO)
                    })
            }

            FinanceOption.CATEGORIES -> {
                CategoryContent(
                    innerPadding = innerPadding,
                    expenseList = expenseList,
                    onRefresh = { financeViewModel.updateStateLists() })
            }

            FinanceOption.DEBTS -> {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .testTag("debtsContent")
                ) {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))
                    DebtContent(expenses = expenseList, users = users)
                }
            }
        }
    }
}

