package com.github.se.wanderpals.ui.screens.trip.finance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Expense
import java.time.format.DateTimeFormatter

/**
 * Composable function for displaying the list of expenses of the trip.
 * It displays the following informations : the title,amount,date, and category of the expense and
 * also which user paid it.
 *
 * @param innerPadding Padding values for the inner content.
 * @param expenseList List of expenses to display.
 */
@Composable
fun ExpensesContent(innerPadding : PaddingValues,expenseList : List<Expense>){
    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxHeight()
    ) {
        items(expenseList) { expense ->
            ExpenseItem(expense = expense) {
            }
        }
    }
}

/**
 * Composable function for displaying an individual expense item.
 * @param expense The expense to display.
 * @param onExpenseItemClick Callback function for when an expense item is clicked.
 */
@Composable
fun ExpenseItem(expense: Expense, onExpenseItemClick: (String) -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(90.dp)) {
        Button(
            onClick = { },
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent),
            enabled = false // not implemented for this sprint
        ) {
            Row(modifier = Modifier
                .fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start
                )
                {
                    // Expense Title
                    Text(
                        text = expense.title,
                        style = TextStyle(fontSize = 18.sp),
                        color = Color.Black,
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )

                    // User that paid the expense
                    Text(
                        text =buildAnnotatedString {
                            append("Payed by ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(expense.userName)
                            }
                        },
                        style = TextStyle(
                            fontSize = 14.sp, color = Color.Gray,
                            textAlign = TextAlign.Start
                        ),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    // Expense category
                    Text(
                        text = expense.category.name,
                        style = TextStyle(
                            fontSize = 14.sp, color = Color.Gray,
                            textAlign = TextAlign.Start
                        )
                    )

                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.End
                ) {
                    // Expense amount
                    Text(
                        text = "%.2f CHF".format(expense.amount),
                        style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                    )

                    // Expense date
                    Text(
                        text = expense.localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                    )

                }
            }
        }
    }
}