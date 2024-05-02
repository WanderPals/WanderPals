package com.github.se.wanderpals.finance

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Category
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.ui.screens.trip.finance.FinancePieChart
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.junit4.MockKRule
import java.time.LocalDate
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PieChartTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @Test
  fun testPieChart() {
    composeTestRule.setContent {
      FinancePieChart(
          expenses =
              listOf(
                  Expense(
                      "",
                      "",
                      10.0,
                      Category.FOOD,
                      "",
                      "",
                      emptyList(),
                      emptyList(),
                      LocalDate.now())))
    }

    composeTestRule.onNodeWithTag("canvasPieChart").assertIsDisplayed()
    composeTestRule.onNodeWithTag("FinancePieChart").assertIsDisplayed()
  }
}
