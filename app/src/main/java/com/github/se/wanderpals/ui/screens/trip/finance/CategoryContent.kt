package com.github.se.wanderpals.ui.screens.trip.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.github.se.wanderpals.model.data.Expense
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Category
import java.time.LocalDate

fun createExpenses(): List<Expense> {
    val currentDate = LocalDate.now()

    val expense1 = Expense(
        expenseId = "1",
        title = "Lunch",
        amount = 15.0,
        category = Category.TRANSPORT,
        userId = "user1",
        userName = "John",
        participantsIds = listOf("user1", "user2"),
        names = listOf("John", "Jane"),
        localDate = currentDate.minusDays(2)
    )

    val expense2 = Expense(
        expenseId = "2",
        title = "Taxi",
        amount = 20.0,
        category = Category.FOOD,
        userId = "user2",
        userName = "Jane",
        participantsIds = listOf("user2", "user3"),
        names = listOf("Jane", "Bob"),
        localDate = currentDate.minusDays(1)
    )

    val expense3 = Expense(
        expenseId = "3",
        title = "Museum tickets",
        amount = 25.0,
        category = Category.ACTIVITIES,
        userId = "user3",
        userName = "Bob",
        participantsIds = listOf("user1", "user3"),
        names = listOf("John", "Bob"),
        localDate = currentDate
    )
    return listOf(expense1, expense2, expense3)
}

@Preview(showBackground = true)
@Composable
fun CategoryContentPreview() {
    val expenses = createExpenses()
    CategoryContent(innerPadding = PaddingValues(16.dp), expenseList = expenses)
}

@Composable
fun CategoryContent(innerPadding: PaddingValues, expenseList: List<Expense>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(innerPadding),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFD1E4FF),

    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally) {
            FinancePieChart(expenses = expenseList, radiusOuter = 100.dp)

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){

                CategoryText(
                    categoryName = Category.TRANSPORT.nameToDisplay,
                    categoryColor = Category.TRANSPORT.color
                )
                CategoryText(
                    categoryName = Category.ACCOMMODATION.nameToDisplay,
                    categoryColor = Category.ACCOMMODATION.color
                )
                CategoryText(
                    categoryName = Category.ACTIVITIES.nameToDisplay,
                    categoryColor = Category.ACTIVITIES.color
                )

            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                CategoryText(
                    categoryName = Category.FOOD.nameToDisplay,
                    categoryColor = Category.FOOD.color
                )
                CategoryText(
                    categoryName = Category.OTHER.nameToDisplay,
                    categoryColor = Category.OTHER.color
                )


            }
        }

    }
}

@Composable
fun CategoryText(categoryName : String,categoryColor : Color){
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(categoryColor, shape = RoundedCornerShape(size = 2.dp))
        )
        Text(
            text = categoryName,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
