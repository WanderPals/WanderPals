package com.github.se.wanderpals.ui.screens.trip.finance

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.data.Expense

/**
 * A pie chart that displays the expenses by categories in a trip.
 *
 * @param expenses The list of expenses to display in the pie chart.
 * @param radiusOuter The outer radius of the pie chart.
 * @param chartBandWidth The width of the pie chart bands.
 * @param totalValueDisplayIsEnabled boolean value to indicate if the total value of the expenses
 *   has to be displayed at the center of the pie-chart.
 * @param currencySymbol Symbol of the currency.
 */
@Composable
fun FinancePieChart(
    expenses: List<Expense>,
    radiusOuter: Dp = 100.dp,
    chartBandWidth: Dp = 20.dp,
    totalValueDisplayIsEnabled: Boolean = false,
    currencySymbol : String = "CHF"
) {

  val totalExpense = expenses.sumOf { it.amount }
  val pieChartData =
      expenses
          .groupBy { it.category }
          .mapValues { (_, expenses) ->
            expenses.sumOf { it.amount / totalExpense * 360 }.toFloat()
          }

  var lastValue = -90f

  Box(
      modifier = Modifier.size(radiusOuter * 2f + chartBandWidth).testTag("FinancePieChart"),
      contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(radiusOuter * 2f).testTag("canvasPieChart")) {
          pieChartData.forEach { (category, relativeCost) ->
            drawArc(
                color = category.color,
                startAngle = lastValue,
                sweepAngle = relativeCost - 2,
                useCenter = false,
                style = Stroke(width = chartBandWidth.toPx(), cap = StrokeCap.Butt))
            lastValue += relativeCost
          }
        }
        if (totalValueDisplayIsEnabled) {
          Column(
              verticalArrangement = Arrangement.Center,
              horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Total Value",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "$totalExpense $currencySymbol",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                )
              }
        }
      }
}
