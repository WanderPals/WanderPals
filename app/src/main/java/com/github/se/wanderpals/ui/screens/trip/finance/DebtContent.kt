package com.github.se.wanderpals.ui.screens.trip.finance

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.viewmodel.FinanceViewModel
import com.github.se.wanderpals.service.SessionManager

/**
 * Composable function for displaying the list of debts of the trip. It displays the following
 * information for each debt:
 * - Amount of the debt.
 * - User to whom the debt is owed.
 * - Clicking on a debt item will display the details of the debt.
 * - Clicking on the back button will return to the list of debts.
 *
 * @param expenses List of expenses to display.
 * @param users List of users to display.
 * @param currencySymbol Symbol of the currency.
 */
@Composable
fun DebtContent(currencySymbol: String, viewmodel: FinanceViewModel) {

  val showSettleDebtDialog by viewmodel.showSettleDebtDialog.collectAsState()
  val settleDebtData by viewmodel.settleDebtData.collectAsState()
  val users by viewmodel.users.collectAsState()
  val expenses by viewmodel.expenseStateList.collectAsState()
  val isLoading by viewmodel.isLoading.collectAsState()
  var settling by remember { mutableStateOf(false) }

  // transform a list of Expense to a Map of userId to a double by taking the list of expenses and
  // summing the amount corresponding to each participants
  val debt =
      users
          .associate { it.userId to users.associate { it.userId to 0.0 }.toMutableMap() }
          .toMutableMap()
  // for each user, we calculate the difference between the amount he paid and the amount he should
  // have paid
  expenses.forEach { expense ->
    expense.participantsIds.forEach { participantId ->
      debt[expense.userId]!![participantId] =
          debt[expense.userId]!![participantId]!! + expense.amount / expense.participantsIds.size
      debt[participantId]!![expense.userId] =
          debt[participantId]!![expense.userId]!! - expense.amount / expense.participantsIds.size
    }
  }

  Box(
      modifier =
          Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp).testTag("defaultDebtContent")) {
        if (showSettleDebtDialog) {
          AlertDialog(
              onDismissRequest = { viewmodel.setShowSettleDebtDialogState(false) },
              title = { Text("Settling debt") },
              text = {
                Text(
                    when (SessionManager.getIsNetworkAvailable()) {
                      true ->
                          "Do you want to settle the debt with ${settleDebtData.first.name} for ${String.format("%.02f %s", settleDebtData.second, currencySymbol)}?"
                      false -> "You can't settle this debt because you are offline"
                    })
              },
              confirmButton = {
                TextButton(
                    onClick = {
                      if (SessionManager.getIsNetworkAvailable()) {
                        settling = true
                        viewmodel.settleDebt(
                            SessionManager.getCurrentUser()!!.userId,
                            SessionManager.getCurrentUser()!!.name)
                      } else {
                        viewmodel.setShowSettleDebtDialogState(false)
                      }
                    },
                    modifier = Modifier.testTag("confirmSettleDebtButton")) {
                      Text("Confirm", color = Color.Red)
                    }
              },
              dismissButton = {
                TextButton(
                    onClick = { viewmodel.setShowSettleDebtDialogState(false) },
                    modifier = Modifier.testTag("cancelSettleDebtButton")) {
                      Text("Cancel")
                    }
              },
              modifier = Modifier.testTag("settleDebtDialog"))
        }

        if (isLoading) {
          Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp).align(Alignment.Center).testTag("loading"))
          }
          if (settling) settling = false
        } else {
          LazyColumn(modifier = Modifier.fillMaxWidth().testTag("debtColumn")) {
            item {
              ElevatedCard(
                  modifier = Modifier.fillMaxWidth().padding(8.dp),
                  elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                      Text(
                          text = "Balance Info",
                          modifier =
                              Modifier.testTag("balanceInfo").padding(top = 12.dp, start = 8.dp),
                      )

                      Spacer(modifier = Modifier.height(8.dp))

                      Column {
                        for (key in users) {
                          DebtInfo(
                              amount =
                                  debt[key.userId]!!.values.sumOf { it } -
                                      debt[key.userId]!![key.userId]!!,
                              user = key.name,
                              currencySymbol = currencySymbol)
                        }
                      }
                    }
                  }
            }
            item {
              ElevatedCard(
                  modifier = Modifier.fillMaxWidth().padding(8.dp),
                  elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                      Text(
                          text = "Fix debt",
                          modifier = Modifier.testTag("myDebt").padding(top = 12.dp, start = 8.dp),
                      )

                      Spacer(modifier = Modifier.height(8.dp))

                      Column {
                        users
                            .filter { it.userId != SessionManager.getCurrentUser()!!.userId }
                            .forEach { key ->
                              if (debt[SessionManager.getCurrentUser()!!.userId]!![key.userId]!! !=
                                  0.0) {
                                val amount =
                                    debt[SessionManager.getCurrentUser()!!.userId]!![key.userId]!!
                                DebtItem(
                                    amount = amount,
                                    user = SessionManager.getCurrentUser()!!.name,
                                    user2 = key.name,
                                    isClickable = amount > 0 && !settling,
                                    onClick = {
                                      viewmodel.setShowSettleDebtDialogState(true)
                                      viewmodel.setSettleDebt(
                                          key,
                                          debt[SessionManager.getCurrentUser()!!.userId]!![
                                              key.userId]!!)
                                    },
                                    currencySymbol = currencySymbol)
                                HorizontalDivider()
                              }
                            }
                        if (users
                            .filter { it.userId != SessionManager.getCurrentUser()!!.userId }
                            .all {
                              debt[SessionManager.getCurrentUser()!!.userId]!![it.userId]!! == 0.0
                            }) {
                          Text(
                              text = "No debt to settle",
                              modifier =
                                  Modifier.padding(32.dp).testTag("noDebtToSettle").fillMaxWidth(),
                              color = MaterialTheme.colorScheme.tertiary,
                              textAlign = TextAlign.Center)
                        }
                      }
                    }
                  }
            }
          }
        }
      }
}

/**
 * Composable function for displaying a single debt item. It displays the amount of the debt and the
 * user to whom the debt is owed.
 *
 * @param amount Amount of the debt.
 * @param user Current user using the app
 * @param user2 User to whom the debt is owed/who owes the debt.
 * @param isClickable Boolean to determine if the item is clickable.
 * @param onClick Function to execute when the item is clicked.
 * @param currencySymbol Symbol of the currency.
 */
@SuppressLint("DefaultLocale")
@Composable
fun DebtItem(
    amount: Double,
    user: String,
    user2: String,
    isClickable: Boolean = false,
    onClick: () -> Unit = {},
    currencySymbol: String
) {

  // start and end are the text to display at the start and end of the row
  val money =
      if (amount >= 0) "${String.format("%.02f", amount)} $currencySymbol"
      else "${String.format("%.02f", -amount)} $currencySymbol"

  Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier =
          Modifier.fillMaxWidth()
              .background(Color.Transparent)
              .clickable(enabled = isClickable) { onClick() }
              .padding(horizontal = 8.dp, vertical = 4.dp)
              .testTag("debtItem$user2")
              .height(IntrinsicSize.Max)) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.padding(8.dp).fillMaxSize().weight(4f)) {
              Column {
                Text(text = user2, modifier = Modifier.padding(2.dp).testTag("nameStart$user2"))
                if (amount > 0) {
                  Text(
                      text = money,
                      modifier = Modifier.padding(2.dp).testTag("moneyStart$user2"),
                      color = MaterialTheme.colorScheme.tertiary)
                }
              }
            }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(8.dp).fillMaxSize().weight(1f)) {
              if (amount < 0)
                  Icon(
                      Icons.AutoMirrored.Filled.ArrowBack,
                      contentDescription = "arrow back",
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.testTag("arrowBack$user2"))
              else
                  Icon(
                      Icons.AutoMirrored.Filled.ArrowForward,
                      contentDescription = "arrow forward",
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.testTag("arrowForward$user2"))
            }

        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier.padding(8.dp).fillMaxSize().weight(4f)) {
              Column {
                Text(
                    text = user,
                    modifier = Modifier.padding(2.dp).fillMaxWidth().testTag("nameEnd$user$user2"),
                    textAlign = TextAlign.End)

                Text(
                    text = if (amount < 0) money else "click to settle debt",
                    modifier = Modifier.padding(2.dp).fillMaxWidth().testTag("moneyEnd$user2"),
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.tertiary)
              }
            }
      }
}

/**
 * Composable function for displaying the debt information. It displays the amount of the debt and
 * the user to whom the debt is owed.
 *
 * @param amount Amount of the debt.
 * @param user User to whom the debt is owed.
 * @param currencySymbol Symbol of the currency.
 */
@SuppressLint("DefaultLocale")
@Composable
fun DebtInfo(amount: Double, user: String, currencySymbol: String) {

  Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier =
          Modifier.fillMaxWidth()
              // red color for negative amount, green color for positive amount, transparent for 0,
              // pale color
              .background(
                  if (amount <= 0) if (amount == 0.0) Color.Transparent else Color(0x80f7a8a8)
                  else Color(0x80a8f7a8))
              .padding(horizontal = 8.dp, vertical = 4.dp)
              .testTag("debt$user")
              .height(IntrinsicSize.Max)) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.padding(8.dp).fillMaxSize().weight(4f)) {
              Column { Text(text = user, modifier = Modifier.padding(2.dp).testTag("start$user")) }
            }

        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier.padding(8.dp).fillMaxSize().weight(4f)) {
              Column {
                Text(
                    text =
                        if (amount >= 0) "+${String.format("%.02f", amount)} $currencySymbol"
                        else "${String.format("%.02f", amount)} $currencySymbol",
                    modifier = Modifier.padding(2.dp).fillMaxWidth().testTag("end$user"),
                    textAlign = TextAlign.End)
              }
            }
      }
}
