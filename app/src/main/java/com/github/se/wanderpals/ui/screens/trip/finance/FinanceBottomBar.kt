package com.github.se.wanderpals.ui.screens.trip.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.service.SessionManager

/**
 * Composable function for displaying the bottom bar in the Finance screen. Provides information
 * about total expenses for the user and total expenses for the trip.
 *
 * @param expenses The list of [Expense] objects representing all the expenses related to the trip.
 * @param currencySymbol Symbol of the currency.
 */
@Composable
fun FinanceBottomBar(expenses: List<Expense>, currencySymbol: String) {
  val totalAmount =
      expenses.filter { it.userId == SessionManager.getCurrentUser()?.userId }.sumOf { it.amount }
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .height(60.dp)
              .background(MaterialTheme.colorScheme.primary)
              .testTag("financeBottomBar"),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 18.dp)) {
              Text(
                  text = "My total expenses",
                  color = MaterialTheme.colorScheme.onPrimary,
                  modifier = Modifier.align(Alignment.Start))
              Text(
                  text = "%.2f %s".format(totalAmount, currencySymbol),
                  color = MaterialTheme.colorScheme.onPrimary,
                  modifier = Modifier.align(Alignment.Start),
              )
            }
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(end = 18.dp)) {
              Text(
                  text = "Total trip expenses",
                  color = MaterialTheme.colorScheme.onPrimary,
                  modifier = Modifier.align(Alignment.End))
              Text(
                  text = "%.2f %s".format(expenses.sumOf { it.amount }, currencySymbol),
                  color = MaterialTheme.colorScheme.onPrimary,
                  modifier = Modifier.align(Alignment.End))
            }
      }
}
