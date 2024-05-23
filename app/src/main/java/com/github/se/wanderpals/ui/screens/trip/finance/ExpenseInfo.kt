package com.github.se.wanderpals.ui.screens.trip.finance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.viewmodel.FinanceViewModel
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.navigation.Route
import java.time.format.DateTimeFormatter

/**
 * Composable function for displaying detailed information about an expense, including : the
 * title,cost,date and participants related to this expense. If user has permission higher than
 * viewer, the user can delete the expense.
 *
 * This composable takes a [FinanceViewModel] as a parameter to interact with expense data.
 *
 * @param financeViewModel The view model containing the expense data and related actions.
 */
@Composable
fun ExpenseInfo(financeViewModel: FinanceViewModel) {
  val selectedExpense by financeViewModel.selectedExpense.collectAsState()
  val expense = selectedExpense!!

  val showDeleteDialog by financeViewModel.showDeleteDialog.collectAsState()

  val tripCurrency by financeViewModel.tripCurrency.collectAsState()

  LaunchedEffect(Unit) { financeViewModel.updateStateLists() }
  // Dialog for deleting expense
  if (showDeleteDialog) {
    AlertDialog(
        onDismissRequest = { financeViewModel.setShowDeleteDialogState(false) },
        title = { Text("Confirm Deletion") },
        text = {
          Text(
              when (SessionManager.getIsNetworkAvailable()) {
                true -> "Are you sure you want to delete this expense?"
                false -> "You can't delete this expense because you are offline"
              })
        },
        confirmButton = {
          TextButton(
              onClick = {
                if (SessionManager.getIsNetworkAvailable()) {
                  financeViewModel.deleteExpense(expense)
                  navigationActions.navigateTo(Route.FINANCE)
                } else {
                  financeViewModel.setShowDeleteDialogState(false)
                }
              },
              modifier = Modifier.testTag("confirmDeleteExpenseButton")) {
                Text("Confirm", color = MaterialTheme.colorScheme.error)
              }
        },
        dismissButton = {
          TextButton(
              onClick = { financeViewModel.setShowDeleteDialogState(false) },
              modifier = Modifier.testTag("cancelDeleteExpenseButton")) {
                Text("Cancel")
              }
        },
        modifier = Modifier.testTag("deleteExpenseDialog"))
  }

  Column(
      modifier = Modifier.fillMaxWidth().testTag("expenseInfo"),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        // Top information of the expenses
        ExpenseTopInfo(expense = expense, currencySymbol = tripCurrency.symbol) {
          financeViewModel.setShowDeleteDialogState(true)
        }

        // Display of the list of participant related to the expense
        ExpenseParticipantsInfo(expense = expense, currencySmybol = tripCurrency.symbol)
      }
}

/**
 * Composable function for displaying top information about an expense.
 *
 * @param expense The expense object containing information to be displayed.
 * @param onDeleteExpenseClick Callback function invoked when the delete expense action is
 *   triggered.
 */
@Composable
fun ExpenseTopInfo(expense: Expense, currencySymbol: String, onDeleteExpenseClick: () -> Unit) {
  val userIsViewer = SessionManager.getCurrentUser()!!.role == Role.VIEWER

  Surface(
      color = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Row(
                  modifier = Modifier.fillMaxWidth().padding(top = 5.dp, end = 10.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically) {
                    // Go-back button
                    IconButton(
                        modifier = Modifier.align(Alignment.Top).testTag("expenseInfoBackButton"),
                        onClick = { navigationActions.goBack() }) {
                          Icon(
                              modifier = Modifier.size(35.dp),
                              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                              contentDescription = "Back",
                              tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    // Delete text (clickable)
                    ClickableText(
                        modifier = Modifier.testTag("deleteTextButton"),
                        onClick = {
                          if (!userIsViewer) {
                            onDeleteExpenseClick()
                          }
                        },
                        text =
                            AnnotatedString(
                                text = "DELETE",
                                spanStyle =
                                    SpanStyle(
                                        fontSize = 16.sp,
                                        color =
                                            if (userIsViewer) Color.LightGray
                                            else MaterialTheme.colorScheme.onPrimary)),
                    )
                  }

              // Expense title
              Text(
                  text = expense.title,
                  style =
                      MaterialTheme.typography.bodyLarge.copy(
                          color = MaterialTheme.colorScheme.onPrimary,
                          fontSize = 25.sp,
                          fontWeight = FontWeight.Bold),
                  overflow = TextOverflow.Ellipsis,
                  maxLines = 1)

              // Expense amount
              Text(
                  modifier =
                      Modifier.padding(top = 10.dp).testTag("expenseAmount" + expense.expenseId),
                  text = String.format("%.2f $currencySymbol", expense.amount),
                  style =
                      MaterialTheme.typography.bodyLarge.copy(
                          color = MaterialTheme.colorScheme.onPrimary,
                          fontSize = 20.sp,
                          fontWeight = FontWeight.Bold))
              // Username and local date
              Row(
                  modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "Paid by ${expense.userName}",
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onPrimary,
                            ),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1)
                    Text(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        text = expense.localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onPrimary,
                            ),
                        maxLines = 1)
                  }
              Text(
                  modifier = Modifier.padding(bottom = 10.dp).align(Alignment.Start),
                  text = "For ${expense.participantsIds.size} participant(s) :",
                  style =
                      MaterialTheme.typography.bodyLarge.copy(
                          color = MaterialTheme.colorScheme.onPrimary,
                          fontWeight = FontWeight.Bold),
                  textAlign = TextAlign.Start)
            }
      }
}

/**
 * Composable function for displaying information about participants involved in an expense. Show
 * their name and the amount they have been paid.
 *
 * @param expense The expense object containing participant information.
 */
@Composable
fun ExpenseParticipantsInfo(expense: Expense, currencySmybol: String) {
  LazyColumn(modifier = Modifier.fillMaxSize()) {
    items(expense.names) { userName ->
      Box(modifier = Modifier.fillMaxWidth().height(80.dp).padding(horizontal = 15.dp)) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
          Text(
              modifier = Modifier.weight(1f).testTag(userName + expense.expenseId),
              text = userName,
              style = MaterialTheme.typography.bodyLarge,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis)
          Text(
              text =
                  String.format(
                      "%.2f $currencySmybol", expense.amount / expense.participantsIds.size),
              style = MaterialTheme.typography.bodyLarge)
        }
      }
      HorizontalDivider(
          color = MaterialTheme.colorScheme.surfaceVariant,
          thickness = 1.dp,
          modifier = Modifier.fillMaxWidth())
    }
  }
}
