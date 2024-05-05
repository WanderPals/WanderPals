package com.github.se.wanderpals.ui.screens.trip.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.data.Category
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.User
import java.time.LocalDate

/**
 * Composable function for displaying the list of debts of the trip. It displays the following
 * informations : the title,amount,date, and category of the expense and also which user paid it.
 *
 * @param expenses List of expenses to display.
 */
@Composable
fun DebtContent(expenses : List<Expense>, users : List<User>) {

    val userExpense = expenses.groupBy { it.userId }
    // transform a list of Expense to a Map of userId to a double by taking the list of expenses and summing the amount corresponding to each participants
    val debt = users.associate { it.userId to users.associate { it.userId to 0.0 }.toMutableMap() }.toMutableMap()
// for each user, we calculate the difference between the amount he paid and the amount he should have paid
    expenses.forEach { expense ->
        expense.participantsIds.forEach { participantId ->
            debt[expense.userId]!![participantId] = debt[expense.userId]!![participantId]!! + expense.amount / expense.participantsIds.size
            debt[participantId]!![expense.userId] = debt[participantId]!![expense.userId]!! - expense.amount / expense.participantsIds.size
        }
    }

    var showDetails by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf("") }

    if(showDetails)
    {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max)) {
                IconButton(onClick = { showDetails = false }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Text(text = "Details for ${users.filter { it.userId == selectedUser }[0].name}")
                }
            }
            debt[selectedUser]!!.forEach {
                if(it.key != selectedUser) {
                    DebtItem(
                        amount = it.value,
                        user = users.filter { it2 -> it2.userId == it.key }[0].name
                    )
                }
            }
        }
    }
    else {
        Column {
            debt.forEach { entry ->
                    DebtItem(
                        amount = entry.value.values.sum() - entry.value[entry.key]!!,
                        user = users.filter { it2 -> it2.userId == entry.key }[0].name,
                        isClickable = true,
                        onClick = {
                            showDetails = true
                            selectedUser = entry.key
                        }
                    )
                }
            }
        }
    }


@Composable
fun DebtItem(amount : Double, user: String, isClickable: Boolean = false, onClick: () -> Unit = {}) {
    
    val start = if(amount >= 0) user else "${String.format("%.02f", amount)} CHF"
    val end  = if(amount >= 0) "+${String.format("%.02f", amount)} CHF" else user

    // fix background when username is too long
    //


    ElevatedCard(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 4.dp)
        .background(Color.Transparent)
        .clickable(enabled = isClickable) { onClick() },
        elevation = CardDefaults.elevatedCardElevation(2.dp)
    )
    {
        Row (modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
        ) {
            Row (modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(if (amount >= 0) Color.Transparent else Color(0xFFf7a8a8))
                .padding(4.dp),
                horizontalArrangement = Arrangement.End) {
                Text(text = start)
            }
            Row (modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(if (amount > 0) Color(0xFFa8f7a8) else Color.Transparent)
                .padding(4.dp)){
                Text(text = end)
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun DebtItemPreview() {
    Column {
        DebtItem(amount = 50.0, user = "Charles", true)
        DebtItem(amount = -50.0, user = "David")
    }

}

@Preview(showBackground = true)
@Composable
fun DebtContentPreview() {
    val expenses = listOf(
        Expense("1", "title1", 10.0, Category.FOOD, "1", "user1", listOf("1", "2", "4"), listOf("user1"), LocalDate.now()),
        Expense("2", "title2", 30.0, Category.ACTIVITIES, "4", "user2", listOf("2", "3"), listOf("user1", "user2"), LocalDate.now()),
        Expense("3", "title3", 30.0, Category.TRANSPORT, "1", "user1", listOf("2", "4"), listOf("user2"), LocalDate.now()),
        Expense("4", "title4", 40.0, Category.FOOD, "2", "user2", listOf("2"), listOf("user1", "user2"), LocalDate.now())
    )
    val users = listOf(
        User("1", "user1", ""),
        User("2", "user2", ""),
        User("3", "user3", ""),
        User("4", "user4", "")
    )
    DebtContent(expenses = expenses, users = users)
}