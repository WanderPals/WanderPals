package com.github.se.wanderpals.finance

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Category
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.FinanceViewModel
import com.github.se.wanderpals.screens.FinanceScreen
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.screens.trip.finance.Finance
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class FinanceViewModelTest :
    FinanceViewModel(TripsRepository("-1", dispatcher = Dispatchers.IO), "-1") {
  private val expense1 =
      Expense(
          expenseId = "1",
          title = "Groceries",
          amount = 50.0,
          category = Category.FOOD,
          userId = "user001",
          userName = "Alice",
          participantsIds = listOf("user001", "user002", "user003"),
          names = listOf("Alice", "Bob", "Charlie"),
          localDate = LocalDate.of(2024, 4, 30))

  private val expense2 =
      Expense(
          expenseId = "2",
          title = "Movie Night",
          amount = 25.0,
          category = Category.TRANSPORT,
          userId = "user002",
          userName = "Bob",
          participantsIds = listOf("user001", "user002", "user003"),
          names = listOf("Alice", "Bob", "Charlie"),
          localDate = LocalDate.of(2024, 4, 29))

  private val expense3 =
      Expense(
          expenseId = "3",
          title = "Dinner",
          amount = 100.0,
          category = Category.FOOD,
          userId = "user003",
          userName = "Charlie",
          participantsIds = listOf("user001", "user002", "user003"),
          names = listOf("Alice", "Bob", "Charlie"),
          localDate = LocalDate.of(2024, 4, 28))

  private val _expenseStateList = MutableStateFlow(listOf(expense1, expense2, expense3))
  override val expenseStateList: StateFlow<List<Expense>> = _expenseStateList

  private val _isLoading = MutableStateFlow(true)
  override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  fun removeExpenseList() {
    _expenseStateList.value = listOf()
  }

  override fun updateStateLists() {
    viewModelScope.launch {
      _isLoading.value = true

      _expenseStateList.value = listOf(expense1, expense2, expense3)

      _isLoading.value = false
    }
  }
}

@RunWith(AndroidJUnit4::class)
class FinanceTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private val financeViewModelTest = FinanceViewModelTest()

  @Before
  fun testSetup() {
    SessionManager.setUserSession()
    SessionManager.setRole(Role.OWNER)
    composeTestRule.setContent {
      Finance(financeViewModel = financeViewModelTest, navigationActions = mockNavActions)
    }
  }

  @Test
  fun expensesContentIsDisplayed() = run {
    ComposeScreen.onComposeScreen<FinanceScreen>(composeTestRule) {
      financeBackButton { assertIsDisplayed() }
      financeTopBar { assertIsDisplayed() }
      financeBottomBar { assertIsDisplayed() }
      financeFloatingActionButton { assertIsDisplayed() }
      expensesContent { assertIsDisplayed() }
      noExpensesTripText { assertIsNotDisplayed() }
    }
  }

  @Test
  fun noExpenseTextIsDisplayedIfNoExpenses() = run {
    ComposeScreen.onComposeScreen<FinanceScreen>(composeTestRule) {
      financeViewModelTest.removeExpenseList()
      noExpensesTripText { assertIsDisplayed() }
      expensesContent { assertIsNotDisplayed() }
    }
  }

  @Test
  fun expensesContentIsNotDisplayedWhenNotSelected() = run {
    ComposeScreen.onComposeScreen<FinanceScreen>(composeTestRule) {
      financeTopBar { assertIsDisplayed() }
      financeBottomBar { assertIsDisplayed() }
      financeFloatingActionButton { assertIsDisplayed() }
      expensesContent { assertIsDisplayed() }
      categoriesButton { performClick() }
      financeFloatingActionButton { assertIsNotDisplayed() }
      expensesContent { assertIsNotDisplayed() }
      debtsButton { performClick() }
      financeFloatingActionButton { assertIsNotDisplayed() }
      expensesContent { assertIsNotDisplayed() }
    }
  }

  @Test
  fun userWithViewRightCantAddExpense() = run {
    SessionManager.setRole(Role.VIEWER)
    ComposeScreen.onComposeScreen<FinanceScreen>(composeTestRule) {
      categoriesButton { performClick() }
      expensesButton { performClick() }
      financeFloatingActionButton { assertIsNotDisplayed() }
    }
  }

  @Test
  fun categoryContentDisplaysEverythingCorrectly() = run {
    composeTestRule.onNodeWithTag("CategoriesButton").performClick()
    composeTestRule.onNodeWithTag("categoryOptionPieChart").assertIsDisplayed()
    composeTestRule.onNodeWithTag("FinancePieChart").assertIsDisplayed()

    Category.values().forEach { category ->
      composeTestRule
          .onNodeWithTag("categoryOptionLazyColumn")
          .performScrollToIndex(category.ordinal + 1)
      composeTestRule.onNodeWithTag("${category.nameToDisplay}InfoItem").assertExists()
    }
  }

  @Test
  fun categoryItemsDisplayCorrectNumberOfTransactionsForEachCategory() = run {
    composeTestRule.onNodeWithTag("CategoriesButton").performClick()

    composeTestRule
        .onNodeWithTag("categoryOptionLazyColumn")
        .performScrollToIndex(Category.values().size)

    composeTestRule.onNodeWithTag("TransportNbTransactions").assertTextEquals("1 transaction")

    composeTestRule.onNodeWithTag("AccommodationNbTransactions").assertTextEquals("0 transactions")

    composeTestRule.onNodeWithTag("ActivitiesNbTransactions").assertTextEquals("0 transactions")

    composeTestRule.onNodeWithTag("FoodNbTransactions").assertTextEquals("2 transactions")

    composeTestRule.onNodeWithTag("OtherNbTransactions").assertTextEquals("0 transactions")
  }

  @Test
  fun categoryItemsDisplayCorrectTotalAmountForEachCategory() = run {
    composeTestRule.onNodeWithTag("CategoriesButton").performClick()

    composeTestRule
        .onNodeWithTag("categoryOptionLazyColumn")
        .performScrollToIndex(Category.values().size)

    composeTestRule.onNodeWithTag("TransportTotalAmount").assertTextEquals("25.00 CHF")

    composeTestRule.onNodeWithTag("AccommodationTotalAmount").assertTextEquals("0.00 CHF")

    composeTestRule.onNodeWithTag("ActivitiesTotalAmount").assertTextEquals("0.00 CHF")

    composeTestRule.onNodeWithTag("FoodTotalAmount").assertTextEquals("150.00 CHF")

    composeTestRule.onNodeWithTag("OtherTotalAmount").assertTextEquals("0.00 CHF")
  }
}
