package com.github.se.wanderpals.ui.screens.trip.finance

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.User

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
 */
@Composable
fun DebtContent(expenses: List<Expense>, users: List<User>) {

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

  var showDetails by remember { mutableStateOf(false) }
  var selectedUser by remember { mutableStateOf("") }

  if (showDetails) {
    Column(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max).testTag("debtDetails")) {
      IconButton(onClick = { showDetails = false }, modifier = Modifier.testTag("detailsBack")) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
      }
      Box(
          contentAlignment = Alignment.TopCenter,
          modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text(
                text = "Details for ${users.filter { it.userId == selectedUser }[0].name}",
                modifier =
                    Modifier.testTag(
                        "details${users.filter { it.userId == selectedUser }[0].name}"))
          }

      Spacer(modifier = Modifier.height(8.dp))

      Box(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Column {
          debt[selectedUser]!!.forEach {
            if (it.key != selectedUser) {
              DebtItem(
                  amount = it.value, user = users.filter { it2 -> it2.userId == it.key }[0].name)
            }
          }
        }
      }
    }
  } else {
    Box(
        modifier =
            Modifier.padding(top = 32.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .testTag("defaultDebtContent")) {
          Column {
            debt.forEach { entry ->
              DebtItem(
                  amount = entry.value.values.sum() - entry.value[entry.key]!!,
                  user = users.filter { it2 -> it2.userId == entry.key }[0].name,
                  isClickable = true,
                  onClick = {
                    showDetails = true
                    selectedUser = entry.key
                  })
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
 * @param user User to whom the debt is owed.
 * @param isClickable Boolean to determine if the item is clickable.
 * @param onClick Function to execute when the item is clicked.
 */
@Composable
fun DebtItem(amount: Double, user: String, isClickable: Boolean = false, onClick: () -> Unit = {}) {

  // start and end are the text to display at the start and end of the row
  val start = if (amount >= 0) user else "${String.format("%.02f", amount)} CHF"
  val end = if (amount >= 0) "+${String.format("%.02f", amount)} CHF" else user

  ElevatedCard(
      modifier =
          Modifier.fillMaxWidth()
              .padding(horizontal = 8.dp, vertical = 8.dp)
              .background(Color.Transparent)
              .clickable(enabled = isClickable) { onClick() }
              .testTag("debt$user"),
      elevation = CardDefaults.elevatedCardElevation(2.dp)) {
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .height(IntrinsicSize.Max)) {
              Row(
                  modifier =
                      Modifier.fillMaxSize()
                          .weight(1f)
                          .background(
                              if (amount >= 0) Color.Transparent
                              else Color(0xFFf7a8a8)) // red if amount < 0, transparent otherwise
                          .padding(4.dp),
                  horizontalArrangement = Arrangement.End,
                  verticalAlignment = Alignment.CenterVertically) {
                    Text(text = start, modifier = Modifier.testTag("start$user"))
                  }
              Row(
                  modifier =
                      Modifier.fillMaxSize()
                          .weight(1f)
                          .background(
                              if (amount > 0) Color(0xFFa8f7a8)
                              else Color.Transparent) // green if amount > 0, transparent otherwise
                          .padding(4.dp),
                  verticalAlignment = Alignment.CenterVertically) {
                    Text(text = end, modifier = Modifier.testTag("end$user"))
                  }
            }
      }
}
