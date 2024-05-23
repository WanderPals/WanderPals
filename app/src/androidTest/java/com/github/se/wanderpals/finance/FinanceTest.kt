package com.github.se.wanderpals.finance

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Category
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.FinanceViewModel
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.screens.FinanceScreen
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.trip.finance.ExpenseInfo
import com.github.se.wanderpals.ui.screens.trip.finance.Finance
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.Called
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

  private val _users = MutableStateFlow(listOf<User>())
  override val users: StateFlow<List<User>> = _users.asStateFlow()

  private val _isLoading = MutableStateFlow(true)
  override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _selectedExpense = MutableStateFlow(expense1)
  override val selectedExpense: StateFlow<Expense?> = _selectedExpense.asStateFlow()

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

  override fun loadMembers(tripId: String) {
    viewModelScope.launch {
      _users.value =
          listOf(
              User("user001", "Alice", ""),
              User("user002", "Bob", ""),
              User("user003", "Charlie", ""))
    }
  }

  override fun deleteExpense(expense: Expense) {
    _expenseStateList.value = _expenseStateList.value.filter { it.expenseId != expense.expenseId }
    setShowDeleteDialogState(false)
  }

  // Override the updateCurrencyFunction to avoid call from the tripsRepository in tests
  override fun updateCurrency(currencyCode: String) {}
}

@RunWith(AndroidJUnit4::class)
class FinanceTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private val financeViewModelTest = FinanceViewModelTest()

  private fun setUpFinanceTest(role: Role = Role.OWNER) {
    SessionManager.setUserSession("user001", "Alice")
    SessionManager.setRole(role)
    navigationActions = mockNavActions
    composeTestRule.setContent {
      Finance(financeViewModel = financeViewModelTest, navigationActions = mockNavActions)
    }
  }

  private fun setUpExpenseInfoTest(role: Role = Role.OWNER) {
    SessionManager.setUserSession()
    SessionManager.setRole(role)
    navigationActions = mockNavActions
    composeTestRule.setContent { ExpenseInfo(financeViewModel = financeViewModelTest) }
  }

  @Test
  fun expensesContentIsDisplayed() = run {
    setUpFinanceTest()
    ComposeScreen.onComposeScreen<FinanceScreen>(composeTestRule) {
      financeBackButton { assertIsDisplayed() }
      composeTestRule.onNodeWithTag("currencyButton").assertIsDisplayed()
      financeTopBar { assertIsDisplayed() }
      financeBottomBar { assertIsDisplayed() }
      financeFloatingActionButton { assertIsDisplayed() }
      expensesContent { assertIsDisplayed() }
      noExpensesTripText { assertIsNotDisplayed() }
    }
  }

  @Test
  fun noExpenseTextIsDisplayedIfNoExpenses() = run {
    setUpFinanceTest()
    ComposeScreen.onComposeScreen<FinanceScreen>(composeTestRule) {
      financeViewModelTest.removeExpenseList()
      noExpensesTripText { assertIsDisplayed() }
      expensesContent { assertIsNotDisplayed() }
    }
  }

  @Test
  fun expensesContentIsNotDisplayedWhenNotSelected() = run {
    setUpFinanceTest()
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
    setUpFinanceTest(Role.VIEWER)
    ComposeScreen.onComposeScreen<FinanceScreen>(composeTestRule) {
      financeFloatingActionButton { assertIsNotDisplayed() }
    }
  }

  @Test
  fun offlineCantAddExpense() = run {
    SessionManager.setIsNetworkAvailable(false)
    setUpFinanceTest()
    ComposeScreen.onComposeScreen<FinanceScreen>(composeTestRule) {
      financeFloatingActionButton { assertIsNotDisplayed() }
    }
    SessionManager.setIsNetworkAvailable(true)
  }

  @Test
  fun debtScreenDisplaysProperly() = run {
    SessionManager.setUserSession("user001", "Alice")
    navigationActions = mockNavActions
    composeTestRule.setContent {
      Finance(financeViewModel = financeViewModelTest, navigationActions = mockNavActions)
    }
    ComposeScreen.onComposeScreen<FinanceScreen>(composeTestRule) {

      // Testing debtContent

      debtsButton { performClick() }

      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag("debtsContent").assertIsDisplayed()
      composeTestRule.onNodeWithTag("defaultDebtContent").assertIsDisplayed()
      composeTestRule.onNodeWithTag("debtColumn").assertIsDisplayed()
      composeTestRule.onNodeWithTag("debtAlice").assertIsDisplayed()
      composeTestRule.onNodeWithTag("debtBob").assertIsDisplayed()
      composeTestRule.onNodeWithTag("debtCharlie").assertIsDisplayed()
      composeTestRule.onNodeWithTag("debtItemBob").assertExists()
      composeTestRule.onNodeWithTag("debtItemCharlie").assertExists()
      composeTestRule.onNodeWithTag("myDebt").assertExists()
      composeTestRule.onNodeWithTag("balanceInfo").assertExists()

      // Testing debtInfo

      composeTestRule
          .onNodeWithTag("startAlice", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Alice")
      composeTestRule
          .onNodeWithTag("endAlice", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("-8.33 CHF")

      composeTestRule
          .onNodeWithTag("startBob", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Bob")
      composeTestRule
          .onNodeWithTag("endBob", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("-33.33 CHF")

      composeTestRule
          .onNodeWithTag("startCharlie", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Charlie")
      composeTestRule
          .onNodeWithTag("endCharlie", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("+41.67 CHF")

      composeTestRule.onNodeWithTag("debtColumn", useUnmergedTree = true).performScrollToIndex(1)

      // Testing DebtItem

      composeTestRule.onNodeWithTag("nameStartBob", useUnmergedTree = true).assertTextEquals("Bob")
      composeTestRule
          .onNodeWithTag("moneyStartBob", useUnmergedTree = true)
          .assertTextEquals("8.33 CHF")
      composeTestRule
          .onNodeWithTag("nameEndAliceBob", useUnmergedTree = true)
          .assertTextEquals("Alice")
      composeTestRule
          .onNodeWithTag("nameEndAliceCharlie", useUnmergedTree = true)
          .assertTextEquals("Alice")
      composeTestRule.onNodeWithTag("moneyEndBob", useUnmergedTree = true).assertDoesNotExist()
      composeTestRule
          .onNodeWithTag("nameStartCharlie", useUnmergedTree = true)
          .assertTextEquals("Charlie")
      composeTestRule
          .onNodeWithTag("moneyEndCharlie", useUnmergedTree = true)
          .assertTextEquals("16.67 CHF")
      composeTestRule
          .onNodeWithTag("moneyStartCharlie", useUnmergedTree = true)
          .assertDoesNotExist()

      composeTestRule.onNodeWithTag("arrowForwardBob", useUnmergedTree = true).assertExists()
      composeTestRule.onNodeWithTag("arrowBackCharlie", useUnmergedTree = true).assertExists()
    }
  }

  @Test
  fun categoryContentDisplaysEverythingCorrectly() = run {
    setUpFinanceTest()
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
    setUpFinanceTest()
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
    setUpFinanceTest()
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

  @Test
  fun expenseInfoDisplaysCorrectly() = run {
    setUpExpenseInfoTest()
    composeTestRule.onNodeWithTag("expenseInfo").assertIsDisplayed()
    composeTestRule.onNodeWithTag("expenseAmount1").assertIsDisplayed()
    financeViewModelTest.expenseStateList.value.first().names.forEach { name ->
      composeTestRule.onNodeWithTag(name + "1").assertTextEquals(name)
    }
  }

  @Test
  fun expenseInfoGoBackToFinanceRouteWhenClickingOnBackButton() = run {
    setUpExpenseInfoTest()
    composeTestRule.onNodeWithTag("expenseInfoBackButton").performClick()
    verify { mockNavActions.goBack() }
    confirmVerified(mockNavActions)
  }

  @Test
  fun expensesDeletesCorrectly() = run {
    setUpExpenseInfoTest()
    ComposeScreen.onComposeScreen<FinanceScreen>(composeTestRule) {
      composeTestRule.onNodeWithTag("deleteTextButton").performClick()
      composeTestRule.onNodeWithTag("deleteExpenseDialog").assertIsDisplayed()
      composeTestRule.onNodeWithTag("confirmDeleteExpenseButton").performClick()
      verify { mockNavActions.navigateTo(Route.FINANCE) }
      confirmVerified(mockNavActions)
      composeTestRule.onNodeWithTag("deleteExpenseDialog").assertIsNotDisplayed()
      assert(financeViewModelTest.expenseStateList.value.size == 2)
    }
  }

  @Test
  fun expensesDeletesNotWorkingOffline() = run {
    SessionManager.setIsNetworkAvailable(false)
    setUpExpenseInfoTest()
    ComposeScreen.onComposeScreen<FinanceScreen>(composeTestRule) {
      composeTestRule.onNodeWithTag("deleteTextButton").performClick()
      composeTestRule.onNodeWithTag("deleteExpenseDialog").assertIsDisplayed()
      composeTestRule.onNodeWithTag("confirmDeleteExpenseButton").performClick()
      verify { mockNavActions wasNot Called }
      confirmVerified(mockNavActions)
      composeTestRule.onNodeWithTag("deleteExpenseDialog").assertIsNotDisplayed()
      assert(financeViewModelTest.expenseStateList.value.size == 3)
    }
    SessionManager.setIsNetworkAvailable(true)
  }

  @Test
  fun expensesDeletesCancel() = run {
    setUpExpenseInfoTest()
    ComposeScreen.onComposeScreen<FinanceScreen>(composeTestRule) {
      composeTestRule.onNodeWithTag("deleteTextButton").performClick()
      composeTestRule.onNodeWithTag("deleteExpenseDialog").assertIsDisplayed()
      composeTestRule.onNodeWithTag("cancelDeleteExpenseButton").performClick()
      composeTestRule.onNodeWithTag("deleteExpenseDialog").assertIsNotDisplayed()
      composeTestRule.onNodeWithTag("expenseInfo").assertIsDisplayed()
    }
  }

  @Test
  fun viewerCantDeleteExpense() = run {
    setUpExpenseInfoTest(Role.VIEWER)
    composeTestRule.onNodeWithTag("deleteTextButton").performClick()
    composeTestRule.onNodeWithTag("deleteExpenseDialog").assertIsNotDisplayed()
  }

  @Test
  fun currencyChangeOnValidInput() = run {
    setUpFinanceTest()
    composeTestRule.onNodeWithTag("currencyButton").performClick()
    composeTestRule.onNodeWithTag("currencySearchText").performTextInput("Euro")
    composeTestRule.onNodeWithTag("currencyValidationButton").performClick()
    composeTestRule.onNodeWithTag("currencyDialog").assertIsNotDisplayed()
  }

  @Test
  fun currencyChangeFailsOnWrongInput() = run {
    setUpFinanceTest()
    composeTestRule.onNodeWithTag("currencyButton").performClick()
    composeTestRule.onNodeWithTag("currencySearchText").performTextInput("r")
    composeTestRule.onNodeWithTag("currencyValidationButton").performClick()
    composeTestRule.onNodeWithTag("currencyDialog").assertIsDisplayed()
  }

  @Test
  fun viewerCantChangeCurrency() = run {
    setUpFinanceTest(Role.VIEWER)
    composeTestRule.onNodeWithTag("currencyButton").assertIsNotEnabled()
    composeTestRule.onNodeWithTag("currencyButton").performClick()
    composeTestRule.onNodeWithTag("currencyDialog").assertIsNotDisplayed()
  }

  @Test
  fun currencySearchSucceedsOnCurrencyCodeSearch() = run {
    setUpFinanceTest()
    composeTestRule.onNodeWithTag("currencyButton").performClick()
    composeTestRule.onNodeWithTag("currencySearchText").performTextInput("USD")
    composeTestRule.onAllNodesWithTag("currencyItem").assertCountEquals(1)
  }

  @Test
  fun currencySearchIsCaseInsensitive() = run {
    setUpFinanceTest()
    composeTestRule.onNodeWithTag("currencyButton").performClick()
    composeTestRule.onNodeWithTag("currencySearchText").performTextInput("uS dOlLAr")
    composeTestRule.onAllNodesWithTag("currencyItem").assertCountEquals(1)
  }
}
