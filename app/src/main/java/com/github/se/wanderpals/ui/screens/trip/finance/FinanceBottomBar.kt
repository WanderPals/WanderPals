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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.service.SessionManager


/**
 * Composable function for displaying the bottom bar in the Finance screen. Provides information
 * about total expenses for the user and total expenses for the trip.
 */
@Composable
fun FinanceBottomBar(expenses: List<Expense>,currencySymbol : String) {
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
                  color = Color.White,
                  modifier = Modifier.align(Alignment.Start))
              Text(
                  text =
                      "${expenses
                          .filter { it.userId == SessionManager.getCurrentUser()?.userId }
                          .sumOf { it.amount }} $currencySymbol",
                  color = Color.White,
                  modifier = Modifier.align(Alignment.Start),
              )
            }
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(end = 18.dp)) {
              Text(
                  text = "Total trip expenses",
                  color = Color.White,
                  modifier = Modifier.align(Alignment.End))
              Text(
                  text = "${expenses.sumOf { it.amount }} $currencySymbol" ,
                  color = Color.White,
                  modifier = Modifier.align(Alignment.End))
            }
      }
}
