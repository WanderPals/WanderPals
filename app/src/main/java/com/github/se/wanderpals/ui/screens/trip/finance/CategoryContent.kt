package com.github.se.wanderpals.ui.screens.trip.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.github.se.wanderpals.model.data.Expense
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.data.Category
import com.github.se.wanderpals.ui.PullToRefreshLazyColumn
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
    CategoryContent(innerPadding = PaddingValues(16.dp), expenseList = expenses,onRefresh = {})
}

@Composable
fun CategoryContent(innerPadding: PaddingValues, expenseList: List<Expense>,onRefresh : () -> Unit) {

    val categoryTransactionMap = expenseList.groupBy { it.category }
        .mapValues { (_, expenses) ->
            val nbPayments = expenses.size
            val totalAmount = expenses.sumOf { it.amount }
            Pair(nbPayments, totalAmount)
        }
        .withDefault { _ -> Pair(0, 0.0) }

    val lazyColumn =
        @Composable {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(horizontal = 25.dp, vertical = 10.dp),
            ) {
                // Pie chart
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            FinancePieChart(
                                expenses = expenseList,
                                radiusOuter = 80.dp,
                                totalValueDisplayIsEnabled = true
                            )

                            Spacer(modifier = Modifier.height(20.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {

                                PieChartCategoryText(category = Category.TRANSPORT)

                                PieChartCategoryText(category = Category.ACCOMMODATION)

                                PieChartCategoryText(category = Category.ACTIVITIES)

                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {

                                PieChartCategoryText(category = Category.FOOD)

                                PieChartCategoryText(category = Category.OTHER)
                            }
                        }

                    }
                }

                items(Category.values()) { category ->
                    val categoryInfo = categoryTransactionMap.getValue(category)
                    Spacer(modifier = Modifier.height(20.dp))
                    CategoryInfoItem(
                        category = category,
                        nbTransaction = categoryInfo.first,
                        totalCategoryAmount = categoryInfo.second
                    )
                }

            }
        }
        PullToRefreshLazyColumn(inputLazyColumn = lazyColumn, onRefresh = onRefresh)


}

@Composable
fun PieChartCategoryText(category: Category){
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(category.color, shape = RoundedCornerShape(size = 2.dp))
        )
        Text(
            text = category.nameToDisplay,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun CategoryInfoItem(category : Category,nbTransaction : Int,totalCategoryAmount: Double) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Row(
            modifier =
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(35.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(category.color)

                )
                Column {
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = category.nameToDisplay,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = "$nbTransaction transactions" ,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                    )
                }
            }

            Text(
                modifier = Modifier.padding(end = 15.dp),
                text = "%.2f CHF".format(totalCategoryAmount),
                style =  MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.End,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}