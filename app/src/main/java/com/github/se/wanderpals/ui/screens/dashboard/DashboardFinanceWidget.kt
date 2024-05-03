package com.github.se.wanderpals.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.viewmodel.DashboardViewModel
import com.github.se.wanderpals.ui.screens.trip.finance.FinancePieChart

/**
 * Composable function for displaying the finance widget in the Dashboard screen. The finance widget
 * displays the total amount of expenses and the latest two expenses. The widget also displays a pie
 * chart of the expenses. The user can click on the widget to navigate to the Finance screen.
 *
 * @param viewModel The ViewModel for the Dashboard screen.
 * @param onClick The callback function for when the widget is clicked.
 */
@Composable
fun DashboardFinanceWidget(viewModel: DashboardViewModel, onClick: () -> Unit = {}) {
  val expenses by viewModel.expenses.collectAsState()
  val sortedExpenses = expenses.sortedByDescending { it.localDate }

  Card(
      modifier =
          Modifier.padding(16.dp)
              .fillMaxWidth()
              .clickable(onClick = onClick)
              .testTag("financeCard"),
      colors =
          CardDefaults.cardColors(
              containerColor =
                  MaterialTheme.colorScheme
                      .surfaceVariant // This sets the background color of the Card
              ),
      shape = RoundedCornerShape(6.dp)) {
        // Finance Widget
        Row(
            modifier = Modifier.height(IntrinsicSize.Max).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
              // Finance Details, Left part of the widget
              Column(
                  modifier =
                      Modifier.padding(start = 8.dp, top = 8.dp, end = 4.dp, bottom = 8.dp)
                          .width(IntrinsicSize.Max)) {
                    // Top part of the texts
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()) {
                          Row(
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.Start,
                              modifier =
                                  Modifier.clip(RoundedCornerShape(4.dp))
                                      .background(MaterialTheme.colorScheme.primaryContainer)
                                      .padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = "Finance Icon",
                                    modifier = Modifier.size(16.dp).testTag("financeIcon"),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text(
                                    text = "Finance",
                                    modifier = Modifier.testTag("financeTitle"),
                                    style =
                                        TextStyle(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Bold))
                              }

                          Spacer(modifier = Modifier.padding(4.dp))

                          Text(
                              text =
                                  "Total: ${String.format("%.02f", expenses.sumOf { it.amount })} CHF",
                              modifier =
                                  Modifier.testTag("totalAmount")
                                      .clip(RoundedCornerShape(4.dp))
                                      .background(MaterialTheme.colorScheme.surface)
                                      .padding(horizontal = 8.dp, vertical = 4.dp),
                              style =
                                  TextStyle(
                                      color = MaterialTheme.colorScheme.primary,
                                      fontWeight = FontWeight.Bold))
                        }

                    Spacer(modifier = Modifier.padding(4.dp))

                    // Latest expenses
                    Surface(
                        modifier =
                            Modifier.clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surface)) {
                          if (expenses.isEmpty()) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier =
                                    Modifier.padding(top = 16.dp, bottom = 40.dp).fillMaxSize()) {
                                  Text(
                                      text = "No expenses yet.",
                                      modifier = Modifier
                                          .testTag("noExpenses"),
                                      style = TextStyle(color = MaterialTheme.colorScheme.primary),
                                  )
                                }
                          } else {
                            Column {
                              ExpenseItem(expense = sortedExpenses[0])
                              HorizontalDivider(
                                  color = MaterialTheme.colorScheme.surfaceVariant,
                                  thickness = 1.dp,
                                  modifier = Modifier.padding(horizontal = 8.dp))
                              if (expenses.size > 1) {
                                ExpenseItem(expense = sortedExpenses[1])
                              } else {
                                Box(modifier = Modifier.fillMaxSize())
                              }
                            }
                          }
                        }
                  }

              // Finance Pie Chart
              Box(
                  modifier =
                      Modifier.padding(top = 8.dp, start = 4.dp, bottom = 8.dp, end = 8.dp)
                          .clip(RoundedCornerShape(4.dp))
                          .background(MaterialTheme.colorScheme.surface)
                          .fillMaxSize(),
                  contentAlignment = Alignment.Center) {
                    if (expenses.isEmpty()) {
                      Text(
                          text = "No expenses yet.",
                          modifier = Modifier.testTag("noExpensesBox"),
                          style = TextStyle(color = MaterialTheme.colorScheme.primary))
                    } else {
                      Box(modifier = Modifier.padding(4.dp).testTag("pieChartBox")) {
                        FinancePieChart(
                            expenses = expenses, radiusOuter = 50.dp, chartBandWidth = 10.dp)
                      }
                    }
                  }
            }
      }
}

@Composable
fun ExpenseItem(expense: Expense) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth().testTag("expenseItem" + expense.expenseId)) {
        Column(modifier = Modifier.padding(8.dp)) {
          Text(
              text =
                  if (expense.title.length > 20) expense.title.subSequence(0, 18).toString() + "..."
                  else expense.title,
              style =
                  TextStyle(
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary,
                      fontSize = 15.sp),
              modifier = Modifier.testTag("expenseTitle" + expense.expenseId))
          Text(
              text =
                  "Paid by ${if(expense.userName.length > 12) expense.userName.subSequence(0, 10).toString()+"..." else expense.userName}",
              style = TextStyle(color = MaterialTheme.colorScheme.tertiary, fontSize = 10.sp),
              modifier = Modifier.testTag("expenseUser" + expense.userId))
        }

        Text(
            text = "${String.format("%.02f", expense.amount)} CHF",
            style =
                TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 15.sp),
            modifier = Modifier.padding(8.dp).testTag("expenseAmount" + expense.expenseId))
      }
}
