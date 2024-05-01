package com.github.se.wanderpals.finance

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.se.wanderpals.model.data.Category
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.ExpenseViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.screens.finance.CreateExpense
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

private class FakeExpenseViewModel : ExpenseViewModel(TripsRepository("", Dispatchers.IO), "") {

  private val _users = MutableStateFlow(listOf(mockUser1, mockUser2, mockUser3, mockUser4))
  override val users = _users

  override fun addExpense(tripId: String, expense: Expense) {
    assert(expense.copy(expenseId = "expense0") == mockExpense)
  }

  override fun loadMembers(tripId: String) {
    // Do nothing
  }
}

private class FakeTripRepository : TripsRepository("", Dispatchers.IO) {
  override suspend fun addExpenseToTrip(tripId: String, expense: Expense): String {
    assert(expense.copy(expenseId = "expense0") == mockExpense)
    return ""
  }

  override suspend fun getAllUsersFromTrip(tripId: String): List<User> {
    return listOf(mockUser1, mockUser2, mockUser3, mockUser4)
  }
}

val mockExpense =
    Expense(
        "expense0",
        "ExpenseTitle",
        10.0,
        Category.FOOD,
        "1",
        "John doe",
        localDate = LocalDate.of(2021, 1, 1),
        names = listOf("John Doe"),
        participantsIds = listOf("1"))

val mockUser1 = User("1", "John Doe")
val mockUser2 = User("2", "Jane Doe")
val mockUser3 = User("3", "A1")
val mockUser4 = User("4", "A2")

private class ExpenseScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ExpenseScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("mapScreen") }) {

  val createExpenseTitle: KNode = onNode { hasTestTag("createExpenseTitle") }
  val backButton: KNode = onNode { hasTestTag("BackButton") }
  val category: KNode = onNode { hasTestTag("category") }
  val paidBy: KNode = onNode { hasTestTag("paidBy") }
  val checkboxAll: KNode = onNode { hasTestTag("checkboxAll") }
  val expenseTitle: KNode = onNode { hasTestTag("expenseTitle") }
  val budget: KNode = onNode { hasTestTag("Budget") }
  val dropdownMenuCategory: KNode = onNode { hasTestTag("dropdownMenuCategory") }
  val expenseDate: KNode = onNode { hasTestTag("expenseDate") }
  val dropdownMenuPaid: KNode = onNode { hasTestTag("dropdownMenuPaid") }
  val userRow1: KNode = onNode { hasTestTag("userRow0") }
  val userRow2: KNode = onNode { hasTestTag("userRow1") }
  val userRow3: KNode = onNode { hasTestTag("userRow2") }
  val userRow4: KNode = onNode { hasTestTag("userRow3") }

  val checkbox1: KNode = onNode { hasTestTag("checkbox0") }
  val checkbox2: KNode = onNode { hasTestTag("checkbox1") }
  val checkbox3: KNode = onNode { hasTestTag("checkbox2") }
  val checkbox4: KNode = onNode { hasTestTag("checkbox3") }

  val saveButton: KNode = onNode { hasTestTag("saveButton") }
  val noUsersFound: KNode = onNode { hasTestTag("noUsersFound") }
  val errorText: KNode = onNode { hasTestTag("errorText") }
  val categoryFood: KNode = onNode { hasTestTag(Category.FOOD.name) }
  val categoryTransport: KNode = onNode { hasTestTag(Category.TRANSPORT.name) }
  val categoryEntertainment: KNode = onNode { hasTestTag(Category.ENTERTAINMENT.name) }
  val categoryOther: KNode = onNode { hasTestTag(Category.OTHER.name) }
  val categoryUtilities: KNode = onNode { hasTestTag(Category.UTILITIES.name) }
  val user1: KNode = onNode { hasTestTag(mockUser1.userId) }
  val user2: KNode = onNode { hasTestTag(mockUser2.userId) }
  val user3: KNode = onNode { hasTestTag(mockUser3.userId) }
  val user4: KNode = onNode { hasTestTag(mockUser4.userId) }
}

class CreateExpenseTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  // test case
  @Test
  fun createExpenseDisplaysProperly() {
    val viewModel = FakeExpenseViewModel()

    composeTestRule.setContent { CreateExpense("", viewModel, mockNavActions) }

    ComposeScreen.onComposeScreen<ExpenseScreen>(composeTestRule) {
      createExpenseTitle { assertIsDisplayed() }
      backButton { assertIsDisplayed() }
      category { assertIsDisplayed() }
      paidBy { assertIsDisplayed() }
      checkboxAll { assertIsDisplayed() }
      expenseTitle { assertIsDisplayed() }
      budget { assertIsDisplayed() }
      dropdownMenuCategory { assertIsDisplayed() }
      expenseDate { assertIsDisplayed() }
      dropdownMenuPaid { assertIsDisplayed() }
      saveButton { assertIsDisplayed() }
      userRow1 { assertExists() }
      userRow2 { assertExists() }
      userRow3 { assertExists() }
      userRow4 { assertExists() }
      checkbox1 { assertIsOff() }
      checkbox2 { assertIsOff() }
      checkbox3 { assertIsOff() }
      checkbox4 { assertIsOff() }

      noUsersFound { assertDoesNotExist() }
      errorText { assertDoesNotExist() }
      categoryFood { assertDoesNotExist() }
      categoryTransport { assertDoesNotExist() }
      categoryEntertainment { assertDoesNotExist() }
      categoryOther { assertDoesNotExist() }
      categoryUtilities { assertDoesNotExist() }
      user1 { assertDoesNotExist() }
      user2 { assertDoesNotExist() }
      user3 { assertDoesNotExist() }
      user4 { assertDoesNotExist() }
    }
  }

  @Test
  fun createExpenseDisplaysCategory() {
    val viewModel = FakeExpenseViewModel()

    composeTestRule.setContent { CreateExpense("", viewModel, mockNavActions) }

    ComposeScreen.onComposeScreen<ExpenseScreen>(composeTestRule) {
      dropdownMenuCategory { performClick() }
      categoryFood { assertIsDisplayed() }
      categoryTransport { assertIsDisplayed() }
      categoryEntertainment { assertIsDisplayed() }
      categoryOther { assertIsDisplayed() }
      categoryUtilities { assertIsDisplayed() }
    }
  }

  @Test
  fun createExpenseDisplaysUsers() {
    val viewModel = FakeExpenseViewModel()

    composeTestRule.setContent { CreateExpense("", viewModel, mockNavActions) }

    ComposeScreen.onComposeScreen<ExpenseScreen>(composeTestRule) {
      dropdownMenuPaid { performClick() }
      user1 { assertIsDisplayed() }
      user2 { assertIsDisplayed() }
      user3 { assertIsDisplayed() }
      user4 { assertIsDisplayed() }
    }
  }

  @Test
  fun createExpenseSavesExpense() {
    val viewModel = FakeExpenseViewModel()

    composeTestRule.setContent { CreateExpense("", viewModel, mockNavActions) }

    ComposeScreen.onComposeScreen<ExpenseScreen>(composeTestRule) {
      expenseTitle { performTextInput("ExpenseTitle") }
      budget { performTextInput("10.0") }
      expenseDate {
        performTextClearance()
        performTextInput("01/01/2021")
      }

      dropdownMenuCategory { performClick() }
      categoryFood { performClick() }
      dropdownMenuPaid { performClick() }
      user1 { performClick() }
      checkbox1 { performClick() }
      saveButton { performClick() }
      errorText { assertDoesNotExist() }
    }
  }

  @Test
  fun createExpenseMissingTitle() {
    val viewModel = FakeExpenseViewModel()

    composeTestRule.setContent { CreateExpense("", viewModel, mockNavActions) }

    ComposeScreen.onComposeScreen<ExpenseScreen>(composeTestRule) {
      budget { performTextInput("10.0") }
      expenseDate {
        performTextClearance()
        performTextInput("01/01/2021")
      }

      dropdownMenuCategory { performClick() }
      categoryFood { performClick() }
      dropdownMenuPaid { performClick() }
      user1 { performClick() }
      checkbox1 { performClick() }
      saveButton { performClick() }
      errorText { assertIsDisplayed() }
    }
  }

  @Test
  fun createExpenseMissingBudget() {
    val viewModel = FakeExpenseViewModel()

    composeTestRule.setContent { CreateExpense("", viewModel, mockNavActions) }

    ComposeScreen.onComposeScreen<ExpenseScreen>(composeTestRule) {
      expenseTitle { performTextInput("ExpenseTitle") }
      expenseDate {
        performTextClearance()
        performTextInput("01/01/2021")
      }

      dropdownMenuCategory { performClick() }
      categoryFood { performClick() }
      dropdownMenuPaid { performClick() }
      user1 { performClick() }
      checkbox1 { performClick() }
      saveButton { performClick() }
      errorText { assertIsDisplayed() }
    }
  }

  @Test
  fun createExpenseMissingDate() {
    val viewModel = FakeExpenseViewModel()

    composeTestRule.setContent { CreateExpense("", viewModel, mockNavActions) }

    ComposeScreen.onComposeScreen<ExpenseScreen>(composeTestRule) {
      expenseTitle { performTextInput("ExpenseTitle") }
      budget { performTextInput("10.0") }
      dropdownMenuCategory { performClick() }
      categoryFood { performClick() }
      dropdownMenuPaid { performClick() }
      user1 { performClick() }
      checkbox1 { performClick() }
      saveButton { performClick() }
      errorText { assertIsDisplayed() }
    }
  }

  @Test
  fun createExpenseMissingCategory() {
    val viewModel = FakeExpenseViewModel()

    composeTestRule.setContent { CreateExpense("", viewModel, mockNavActions) }

    ComposeScreen.onComposeScreen<ExpenseScreen>(composeTestRule) {
      expenseTitle { performTextInput("ExpenseTitle") }
      budget { performTextInput("10.0") }
      expenseDate {
        performTextClearance()
        performTextInput("01/01/2021")
      }
      dropdownMenuPaid { performClick() }
      user1 { performClick() }
      checkbox1 { performClick() }
      saveButton { performClick() }
      errorText { assertIsDisplayed() }
    }
  }

  @Test
  fun createExpenseMissingPaidBy() {
    val viewModel = FakeExpenseViewModel()

    composeTestRule.setContent { CreateExpense("", viewModel, mockNavActions) }

    ComposeScreen.onComposeScreen<ExpenseScreen>(composeTestRule) {
      expenseTitle { performTextInput("ExpenseTitle") }
      budget { performTextInput("10.0") }
      expenseDate {
        performTextClearance()
        performTextInput("01/01/2021")
      }

      dropdownMenuCategory { performClick() }
      categoryFood { performClick() }
      checkbox1 { performClick() }
      saveButton { performClick() }
      errorText { assertIsDisplayed() }
    }
  }

  @Test
  fun createExpenseMissingParticipants() {
    val viewModel = FakeExpenseViewModel()

    composeTestRule.setContent { CreateExpense("", viewModel, mockNavActions) }

    ComposeScreen.onComposeScreen<ExpenseScreen>(composeTestRule) {
      expenseTitle { performTextInput("ExpenseTitle") }
      budget { performTextInput("10.0") }
      expenseDate {
        performTextClearance()
        performTextInput("01/01/2021")
      }

      dropdownMenuCategory { performClick() }
      categoryFood { performClick() }
      dropdownMenuPaid { performClick() }
      user1 { performClick() }
      saveButton { performClick() }
      errorText { assertIsDisplayed() }
    }
  }

  @Test
  fun createExpenseSelectOne() {
    val viewModel = FakeExpenseViewModel()

    composeTestRule.setContent { CreateExpense("", viewModel, mockNavActions) }

    ComposeScreen.onComposeScreen<ExpenseScreen>(composeTestRule) {
      checkbox1 { assertIsOff() }
      checkbox1 { performClick() }
      checkbox1 { assertIsOn() }
    }
  }

  @Test
  fun createExpenseSelectAll() {
    val viewModel = FakeExpenseViewModel()

    composeTestRule.setContent { CreateExpense("", viewModel, mockNavActions) }

    ComposeScreen.onComposeScreen<ExpenseScreen>(composeTestRule) {
      // check everything is off
      checkbox1 { assertIsOff() }
      checkbox2 { assertIsOff() }
      checkbox3 { assertIsOff() }
      checkbox4 { assertIsOff() }

      // select all
      checkboxAll { performClick() }
      checkbox1 { assertIsOn() }
      checkbox2 { assertIsOn() }
      checkbox3 { assertIsOn() }
      checkbox4 { assertIsOn() }
      checkboxAll { assertIsOn() }

      // deselect all
      checkboxAll { performClick() }
      checkbox1 { assertIsOff() }
      checkbox2 { assertIsOff() }
      checkbox3 { assertIsOff() }
      checkbox4 { assertIsOff() }

      // select one
      checkboxAll { assertIsOff() }
      checkbox1 { performClick() }
      checkbox1 { assertIsOn() }
      checkbox2 { assertIsOff() }
      checkboxAll { assertIsOff() }

      // select all but one
      checkboxAll { performClick() }
      checkbox1 { performClick() }
      checkbox1 { assertIsOff() }
      checkbox2 { assertIsOn() }
      checkboxAll { assertIsOff() }
    }
  }

  @Test
  fun createExpenseSelectCategory() {
    val viewModel = FakeExpenseViewModel()

    composeTestRule.setContent { CreateExpense("", viewModel, mockNavActions) }

    ComposeScreen.onComposeScreen<ExpenseScreen>(composeTestRule) {
      category { performClick() }
      categoryFood { performClick() }
      category { assertTextContains("FOOD") }

      category { performClick() }
      categoryEntertainment { performClick() }
      category { assertTextContains("ENTERTAINMENT") }

      category { performClick() }
      categoryTransport { performClick() }
      category { assertTextContains("TRANSPORT") }

      category { performClick() }
      categoryUtilities { performClick() }
      category { assertTextContains("UTILITIES") }

      category { performClick() }
      categoryOther { performClick() }
      category { assertTextContains("OTHER") }
    }
  }

  @Test
  fun viewModelTest() // This test might pose problem later, concurrency issues. If this breaks,
    // remove it.
  {
    val viewModel = ExpenseViewModel(FakeTripRepository(), "")
    viewModel.loadMembers("")
    viewModel.addExpense("", mockExpense)
    composeTestRule.setContent { CreateExpense("", viewModel, mockNavActions) }
    composeTestRule.waitForIdle()

    ComposeScreen.onComposeScreen<ExpenseScreen>(composeTestRule) {
      userRow1 { assertIsDisplayed() }
      userRow2 { assertIsDisplayed() }
      userRow3 { assertIsDisplayed() }
      userRow4 { assertIsDisplayed() }
    }

    val users = viewModel.users.value
    assert(users == listOf(mockUser1, mockUser2, mockUser3, mockUser4))
  }
}
