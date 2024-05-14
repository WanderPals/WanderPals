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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.data.Category
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.ui.PullToRefreshLazyColumn

/**
 * Composable function to display information about expenses that have been made during the trip
 *
 * A pie-chart is displayed to compare the amount of expenses in each category relative to the
 * others. The number of transaction and the total amount for each category is also displayed as
 * items below the pie-chart.
 *
 * @param innerPadding Padding values for the inner content
 * @param expenseList List of expenses
 * @param onRefresh Callback function for handling refresh action
 */
@Composable
fun CategoryContent(
    innerPadding: PaddingValues,
    expenseList: List<Expense>,
    onRefresh: () -> Unit
) {

  /*Grouping expenses by category and calculating total number of transactions
  and total amount for each category which are by default 0 if the category
  has no associated expenses */
  val categoryTransactionMap =
      expenseList
          .groupBy { it.category }
          .mapValues { (_, expenses) ->
            val nbPayments = expenses.size
            val totalAmount = expenses.sumOf { it.amount }
            Pair(nbPayments, totalAmount)
          }
          .withDefault { _ -> Pair(0, 0.0) }

  val lazyColumn =
      @Composable {
        LazyColumn(
            modifier =
                Modifier.fillMaxWidth().padding(innerPadding).testTag("categoryOptionLazyColumn"),
            contentPadding = PaddingValues(horizontal = 25.dp, vertical = 10.dp)) {

              // Pie chart
              item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.testTag("categoryOptionPieChart"),
                ) {
                  Column(
                      modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                      verticalArrangement = Arrangement.SpaceBetween,
                      horizontalAlignment = Alignment.CenterHorizontally) {
                        FinancePieChart(
                            expenses = expenseList,
                            radiusOuter = 80.dp,
                            totalValueDisplayIsEnabled = true)

                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly) {
                              PieChartCategoryText(category = Category.TRANSPORT)

                              PieChartCategoryText(category = Category.ACCOMMODATION)

                              PieChartCategoryText(category = Category.ACTIVITIES)
                            }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly) {
                              PieChartCategoryText(category = Category.FOOD)

                              PieChartCategoryText(category = Category.OTHER)
                            }
                      }
                }
              }

              // Category information items
              items(Category.values()) { category ->
                val categoryInfo = categoryTransactionMap.getValue(category)
                Spacer(modifier = Modifier.height(20.dp))
                CategoryInfoItem(
                    category = category,
                    nbTransaction = categoryInfo.first,
                    totalCategoryAmount = categoryInfo.second)
              }
            }
      }
  PullToRefreshLazyColumn(inputLazyColumn = lazyColumn, onRefresh = onRefresh)
}
/**
 * Composable function to display a row containing a colored box representing the category and the
 * name of the category.
 *
 * @param category The category to be displayed.
 */
@Composable
fun PieChartCategoryText(category: Category) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
        modifier =
            Modifier.size(10.dp)
                .background(category.color, shape = RoundedCornerShape(size = 2.dp)))
    Text(
        text = category.nameToDisplay,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(start = 8.dp))
  }
}

/**
 * Composable function to display information about a specific category, including the number of
 * transactions and the total amount spent in that category.
 *
 * @param category The category for which the information is displayed.
 * @param nbTransaction The number of transactions in the category.
 * @param totalCategoryAmount The total amount spent in the category.
 */
@Composable
fun CategoryInfoItem(category: Category, nbTransaction: Int, totalCategoryAmount: Double) {

  Surface(
      modifier = Modifier.fillMaxWidth().height(60.dp).testTag(category.nameToDisplay + "InfoItem"),
      shape = RoundedCornerShape(10.dp),
      color = MaterialTheme.colorScheme.secondaryContainer,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
          Row(
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier =
                        Modifier.padding(start = 10.dp)
                            .size(35.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(category.color))

                Column {
                  Text(
                      modifier = Modifier.padding(start = 10.dp),
                      text = category.nameToDisplay,
                      style =
                          MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.primary,
                  )
                  Text(
                      modifier =
                          Modifier.padding(start = 10.dp)
                              .testTag(category.nameToDisplay + "NbTransactions"),
                      text =
                          if (nbTransaction == 1) "$nbTransaction transaction"
                          else "$nbTransaction transactions",
                      style = MaterialTheme.typography.bodyMedium,
                      color = Color.Gray,
                  )
                }
              }

          Text(
              modifier =
                  Modifier.padding(end = 15.dp).testTag(category.nameToDisplay + "TotalAmount"),
              text = "%.2f CHF".format(totalCategoryAmount),
              style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary,
              textAlign = TextAlign.End,
              overflow = TextOverflow.Ellipsis,
              maxLines = 1)
        }
  }
}
