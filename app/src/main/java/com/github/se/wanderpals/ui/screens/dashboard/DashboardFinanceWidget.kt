package com.github.se.wanderpals.ui.screens.dashboard

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import java.util.Locale
import kotlin.math.ceil

/**
 * Composable function for displaying the finance widget in the Dashboard screen. The finance widget
 * displays the total amount of expenses and the latest two expenses. The widget also displays a pie
 * chart of the expenses. The user can click on the widget to navigate to the Finance screen.
 *
 * @param viewModel The ViewModel for the Dashboard screen.
 * @param onClick The callback function for when the widget is clicked.
 */
@SuppressLint("DefaultLocale")
@Composable
fun DashboardFinanceWidget(viewModel: DashboardViewModel, onClick: () -> Unit = {}) {
  val expenses by viewModel.expenses.collectAsState()
  val sortedExpenses = expenses.sortedByDescending { it.localDate }

  val currencyCode by viewModel.currencyCode.collectAsState()

  ElevatedCard(
      modifier =
          Modifier.padding(horizontal = 16.dp)
              .fillMaxWidth()
              .clickable(onClick = onClick)
              .testTag("financeCard"),
      colors =
          CardDefaults.cardColors(
              containerColor =
                  MaterialTheme.colorScheme
                      .surfaceVariant // This sets the background color of the Card
              ),
      shape = RoundedCornerShape(6.dp),
      elevation = CardDefaults.cardElevation(10.dp)) {
        // Finance Widget
        Row(
            modifier = Modifier.height(IntrinsicSize.Max).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
              // Finance Details, Left part of the widget
              Column(modifier = Modifier.padding(8.dp).width(IntrinsicSize.Max)) {
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
                              "Total: ${formatExpense(expenses.sumOf { it.amount })} " +
                                  currencyCode,
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
                Box(
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
                                  modifier = Modifier.testTag("noExpenses"),
                                  style = TextStyle(color = MaterialTheme.colorScheme.primary),
                              )
                            }
                      } else {
                        Column {
                          ExpenseItem(expense = sortedExpenses[0], currencyCode = currencyCode)
                          HorizontalDivider(
                              color = MaterialTheme.colorScheme.surfaceVariant,
                              thickness = 1.dp,
                              modifier = Modifier.padding(horizontal = 8.dp))
                          if (expenses.size > 1) {
                            ExpenseItem(expense = sortedExpenses[1], currencyCode = currencyCode)
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
fun ExpenseItem(expense: Expense, currencyCode: String) {
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
            text = "${formatExpense(expense.amount)} $currencyCode",
            style =
                TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 15.sp),
            modifier = Modifier.padding(8.dp).testTag("expenseAmount" + expense.expenseId))
      }
}

/**
 * Function to format the total expense amount to a more readable format. The function takes the
 * total expense amount as a Double and returns a formatted String. The function formats the amount
 * to a maximum of 2 decimal places and adds a suffix of M, B, or T based on the number of digits in
 * the amount.
 *
 * @param totalexpense The total expense amount as a Double.
 */
@SuppressLint("SuspiciousIndentation")
private fun formatExpense(totalexpense: Double): String {
  var formattedString = String.format(Locale.US, "%.02f", totalexpense).reversed()

  val numString = formattedString.substring(3)
  val numberOfDigits = ceil(numString.length.toDouble() / 3.0).toInt()
  if (numberOfDigits > 2) {
    val formattedReversedString = numString.chunked(3).joinToString(".")

    // take only the digits after the last point and the two digits before the point
    val expenseToString = formattedReversedString.substringAfterLast(".").substringBeforeLast(".")

    formattedString =
        when (numberOfDigits) {
          3 -> (expenseToString.reversed() + "M")
          4 -> (expenseToString.reversed() + "B")
          5 -> (expenseToString.reversed() + "T")
          else -> {
            "Error"
          }
        }
  } else {
    formattedString = formattedString.reversed()
    Log.d("formattedString", formattedString)
  }

  return formattedString
}
