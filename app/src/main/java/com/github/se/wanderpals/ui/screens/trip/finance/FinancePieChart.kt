package com.github.se.wanderpals.ui.screens.trip.finance

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.data.Expense

/**
 * A pie chart that displays the expenses in a trip.
 *
 * @param expenses The list of expenses to display in the pie chart.
 * @param radiusOuter The outer radius of the pie chart.
 * @param chartBandWidth The width of the pie chart bands.
 */
@Composable
fun FinancePieChart(
    expenses: List<Expense>,
    radiusOuter: Dp = 100.dp,
    chartBandWidth: Dp = 20.dp,
) {
  val pieChartData =
      expenses
          .groupBy { it.category }
          .map { (category, expenses) -> category to expenses.sumOf { it.amount } }
          .toMap()

  val totalExpense = pieChartData.values.sum()
  val pieChartValues =
      pieChartData.values.toList().map { 360f * it.toFloat() / totalExpense.toFloat() }

  val colors =
      listOf(
          Color(0xFFFFC09F),
          Color(0xFF2E5EAA),
          Color(0xFFCF4D6F),
          Color(0xFFA5F8D3),
          Color(
              0xFF30BCED)) // Need to change this to prettier color but i don't know if hard coding
  // is the best way to do it

  var lastValue = -90f

  Box(
      modifier =
          Modifier.size(radiusOuter * 2f + chartBandWidth)
              .testTag("FinancePieChart")
              .background(Color.DarkGray),
      contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(radiusOuter * 2f)) {
          pieChartValues.forEachIndexed { index, fl ->
            drawArc(
                color = colors[index],
                startAngle = lastValue,
                sweepAngle = fl - 2,
                useCenter = false,
                style = Stroke(width = chartBandWidth.toPx(), cap = StrokeCap.Butt))
            lastValue += fl
          }
        }
      }
}
