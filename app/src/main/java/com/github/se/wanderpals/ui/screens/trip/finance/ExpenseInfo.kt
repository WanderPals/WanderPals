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


@Composable
fun ExpenseInfo(financeViewModel: FinanceViewModel) {
    val selectedExpense by financeViewModel.selectedExpense.collectAsState()
    val expense = selectedExpense!!

    val showDeleteDialog by financeViewModel.showDeleteDialog.collectAsState()

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { financeViewModel.setShowDeleteDialogState(false) },
            title = { Text("Confirm Deletion") },
            text = {
                Text("Are you sure you want to delete this expense?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        financeViewModel.deleteExpense(expense)
                        navigationActions.navigateTo(Route.FINANCE)
                              },
                    modifier = Modifier.testTag("confirmDeleteExpenseButton")) {
                    Text("Confirm", color = Color.Red)
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ExpenseTopInfo(expense) {
            financeViewModel.setShowDeleteDialogState(true)
        }

        ExpenseParticipantsInfo(expense = expense)
    }
}

@Composable
fun ExpenseTopInfo(expense: Expense,onDeleteExpenseClick : () -> Unit) {
    val userIsViewer = SessionManager.getCurrentUser()!!.role == Role.VIEWER

    Surface(
        color = MaterialTheme.colorScheme.primary,
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier.align(Alignment.Top).testTag("expenseInfoBackButton"),
                    onClick = { navigationActions.goBack()}
                ) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                    )
                }
                ClickableText(
                    modifier = Modifier.testTag("deleteTextButton"),
                    onClick = { if(!userIsViewer){
                        onDeleteExpenseClick()
                    } },

                    text = AnnotatedString(
                        text = "DELETE",
                        spanStyle = SpanStyle(
                            fontSize = 16.sp,
                            color = if (userIsViewer) Color.LightGray else Color.White)
                    ),
                )
            }
            Text(

                text = expense.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                ),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            Text(
                modifier = Modifier.padding(top = 10.dp).testTag("expenseAmount"+expense.expenseId),
                text = String.format("%.2f CHF", expense.amount),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Paid by ${expense.userName}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    text = expense.localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                    ),
                    maxLines = 1
                )
            }
            Text(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .align(Alignment.Start),
                text = "For ${expense.participantsIds.size} participant(s) :",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Start
            )

        }
    }
}

@Composable
fun ExpenseParticipantsInfo(expense: Expense) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(expense.names) { userName ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 15.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f).testTag(userName+expense.expenseId),
                        text = userName,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = String.format(
                            "%.2f CHF",
                            expense.amount / expense.participantsIds.size
                        ),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            HorizontalDivider(
                color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxWidth()
            )

        }
    }
}


