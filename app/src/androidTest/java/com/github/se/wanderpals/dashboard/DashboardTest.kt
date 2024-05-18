package com.github.se.wanderpals.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Category
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.DashboardViewModel
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.trip.Dashboard
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.Called
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private val suggestion1: Suggestion =
    Suggestion(
        suggestionId = "1",
        userName = "User",
        createdAt = LocalDate.now().minusDays(1),
        createdAtTime = LocalTime.now(),
        stop =
            Stop(
                stopId = "1",
                title = "Stop Title",
                address = "123 Street",
                date = LocalDate.now().plusDays(2),
                startTime = LocalTime.now(),
                duration = 60,
                budget = 100.0,
                description =
                    "This is a description of the stop. It should be brief and informative.",
                geoCords = GeoCords(0.0, 0.0),
                website = "https://example.com",
                imageUrl = ""),
        text = "This is a suggestion for a stop.",
        userId = "1")

private val suggestion2: Suggestion =
    Suggestion(
        suggestionId = "2",
        userName = "User",
        createdAt = LocalDate.now(),
        createdAtTime = LocalTime.now(),
        stop =
            Stop(
                stopId = "2",
                title = "Stop Title",
                address = "123 Street",
                date = LocalDate.now().plusDays(1),
                startTime = LocalTime.now(),
                duration = 60,
                budget = 100.0,
                description =
                    "This is a description of the stop. It should be brief and informative.",
                geoCords = GeoCords(0.0, 0.0),
                website = "https://example.com",
                imageUrl = ""),
        text = "This is a suggestion for a stop.",
        userId = "1")

private val suggestion3: Suggestion =
    Suggestion(
        suggestionId = "3",
        userName = "User",
        createdAt = LocalDate.now(),
        createdAtTime = LocalTime.now(),
        stop =
            Stop(
                stopId = "3",
                title = "Stop Title",
                address = "123 Street",
                date = LocalDate.now().plusDays(1),
                startTime = LocalTime.now(),
                duration = 60,
                budget = 100.0,
                description =
                    "This is a description of the stop. It should be brief and informative.",
                geoCords = GeoCords(0.0, 0.0),
                website = "https://example.com",
                imageUrl = ""),
        text = "This is a suggestion for a stop.",
        userId = "1")

private val suggestion4: Suggestion =
    Suggestion(
        suggestionId = "4",
        userName = "User",
        createdAt = LocalDate.now(),
        createdAtTime = LocalTime.now(),
        stop =
            Stop(
                stopId = "4",
                title = "Stop Title",
                address = "123 Street",
                date = LocalDate.now(),
                startTime = LocalTime.now(),
                duration = 60,
                budget = 100.0,
                description =
                    "This is a description of the stop. It should be brief and informative.",
                geoCords = GeoCords(0.0, 0.0),
                website = "https://example.com",
                imageUrl = ""),
        text = "This is a suggestion for a stop.",
        userId = "1")

private val expense1 =
    Expense(
        expenseId = "1",
        title = "Groceries",
        amount = 50.0,
        category = Category.FOOD,
        userId = "1",
        userName = "Alice",
        participantsIds = listOf("user001", "user002", "user003"),
        names = listOf("Alice", "Bob", "Charlie"),
        localDate = LocalDate.of(2024, 4, 28))

private val expense2 =
    Expense(
        expenseId = "2",
        title = "Movie Night",
        amount = 25.0,
        category = Category.OTHER,
        userId = "2",
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
        userId = "3",
        userName = "Charlie",
        participantsIds = listOf("user001", "user002", "user003"),
        names = listOf("Alice", "Bob", "Charlie"),
        localDate = LocalDate.of(2024, 4, 30))

class DashboardViewModelTest(list: List<Suggestion>) :
    DashboardViewModel(tripId = "", tripsRepository = TripsRepository("", Dispatchers.IO)) {
  private val _isLoading = MutableStateFlow(false)
  override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _state = MutableStateFlow(list)
  override val state: StateFlow<List<Suggestion>> = _state.asStateFlow()

  private val _expenses = MutableStateFlow(emptyList<Expense>())
  override val expenses: StateFlow<List<Expense>> = _expenses

  private val _stops = MutableStateFlow(emptyList<Stop>())
  override val stops: StateFlow<List<Stop>> = _stops.asStateFlow()

  override fun loadSuggestion(tripId: String) {}

  override fun loadExpenses(tripId: String) {}

  override fun loadStops(tripId: String) {}

  fun setStops(stops: List<Stop>) {
    _stops.value = stops
  }

  fun setExpenses(expenses: List<Expense>) {
    _expenses.value = expenses
  }

  fun setLoading(isLoading: Boolean) {
    _isLoading.value = isLoading
  }

  override fun confirmDeleteTrip() {
    // Do nothing
  }
}

@RunWith(AndroidJUnit4::class)
class DashboardTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Before
  fun setUp() {
    SessionManager.setUserSession(role = Role.OWNER)
  }

  @Test
  fun testDashboardLoading() {
    val viewModel = DashboardViewModelTest(listOf())
    viewModel.setLoading(true)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }
    // Check that the loading indicator is displayed
    composeTestRule.onNodeWithTag("loading").assertIsDisplayed()

    viewModel.setLoading(false)
    composeTestRule.onNodeWithTag("loading").assertDoesNotExist()
  }

  @Test
  fun testDashboardDisplaysEssentials() {
    val viewModel = DashboardViewModelTest(listOf())
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }

    // Check that the top bar is displayed
    composeTestRule.onNodeWithTag("dashboardTopBar", useUnmergedTree = true).assertIsDisplayed()
    // Check that the suggestion widget is displayed
    composeTestRule.onNodeWithTag("suggestionCard", useUnmergedTree = true).assertIsDisplayed()
    // Check that the suggestion widget title is displayed
    composeTestRule.onNodeWithTag("suggestionTitle", useUnmergedTree = true).assertIsDisplayed()
    // Check that the suggestion widget is displayed
    composeTestRule.onNodeWithTag("noSuggestions", useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun testDashboardOneSuggestions() {
    val viewModel = DashboardViewModelTest(listOf(suggestion1))
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }

    // Check that the suggestion widget doesn't display "No suggestions yet."
    composeTestRule.onNodeWithTag("noSuggestions", useUnmergedTree = true).assertDoesNotExist()

    // Check that the first suggestion exist and is displayed
    composeTestRule
        .onNodeWithTag("suggestionItem1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
  }

  @Test
  fun testDashboardTwoSuggestions() {
    val viewModel = DashboardViewModelTest(listOf(suggestion1, suggestion2))
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }

    // Check that the suggestion widget doesn't display "No suggestions yet."
    composeTestRule.onNodeWithTag("noSuggestions", useUnmergedTree = true).assertDoesNotExist()

    // Check that the first suggestion exist and is displayed
    composeTestRule
        .onNodeWithTag("suggestionItem1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the second suggestion exist and is displayed
    composeTestRule
        .onNodeWithTag("suggestionItem2", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
  }

  @Test
  fun testDashboardThreeSuggestions() {
    val viewModel = DashboardViewModelTest(listOf(suggestion1, suggestion2, suggestion3))
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }

    // Check that the suggestion widget doesn't display "No suggestions yet."
    composeTestRule.onNodeWithTag("noSuggestions", useUnmergedTree = true).assertDoesNotExist()

    // Check that the first suggestion exist and is displayed
    composeTestRule
        .onNodeWithTag("suggestionItem1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the second suggestion exist and is displayed
    composeTestRule
        .onNodeWithTag("suggestionItem2", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the third suggestion exist and is displayed
    composeTestRule
        .onNodeWithTag("suggestionItem3", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
  }

  @Test
  fun testDashboardFourSuggestions() {
    val viewModel =
        DashboardViewModelTest(listOf(suggestion1, suggestion2, suggestion3, suggestion4))
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }

    // Check that the suggestion widget doesn't display "No suggestions yet."
    composeTestRule.onNodeWithTag("noSuggestions", useUnmergedTree = true).assertDoesNotExist()

    // Check that the first suggestion doesn't exist
    composeTestRule.onNodeWithTag("suggestionItem1", useUnmergedTree = true).assertDoesNotExist()
    // Check that the second suggestion exist and is displayed
    composeTestRule
        .onNodeWithTag("suggestionItem2", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the third suggestion exist and is displayed
    composeTestRule
        .onNodeWithTag("suggestionItem3", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the fourth suggestion doesn't exist
    composeTestRule
        .onNodeWithTag("suggestionItem4", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
  }

  @Test
  fun testDashboardWidgetContent() {
    val viewModel = DashboardViewModelTest(listOf(suggestion1))
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }

    // Check that the stop title is displayed
    composeTestRule
        .onNodeWithTag("suggestionTitle1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the user name is displayed
    composeTestRule
        .onNodeWithTag("suggestionUser1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the start time is displayed
    composeTestRule
        .onNodeWithTag("suggestionStart1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the end time is displayed
    composeTestRule
        .onNodeWithTag("suggestionEnd1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
  }

  @Test
  fun clickMemberList() = run {
    val viewModel = DashboardViewModelTest(listOf(suggestion1))
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }
    composeTestRule.onNodeWithTag("menuButton").performClick()
    composeTestRule.onNodeWithTag("AdminButtonTest").performClick()
    composeTestRule.onNodeWithTag("menuNav").assertIsNotDisplayed()

    verify { mockNavActions.navigateTo(Route.ADMIN_PAGE) }
    confirmVerified(mockNavActions)
  }

  @Test
  fun financeWidgetDisplaysProperly() = run {
    val viewModel = DashboardViewModelTest(emptyList())
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }

    composeTestRule.onNodeWithTag("financeCard", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("financeTitle", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("totalAmount", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("noExpenses", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("noExpensesBox", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("financeIcon", useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun financeWidgetDisplaysProperlyOne() = run {
    val viewModel = DashboardViewModelTest(listOf(suggestion1))
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }

    viewModel.setExpenses(listOf(expense1))

    composeTestRule.onNodeWithTag("noExpenses", useUnmergedTree = true).assertDoesNotExist()
    composeTestRule.onNodeWithTag("noExpensesBox", useUnmergedTree = true).assertDoesNotExist()
    composeTestRule.onNodeWithTag("pieChartBox", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("totalAmount", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("Total: 50.00 CHF")
    composeTestRule.onNodeWithTag("expenseItem1", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("expenseItem2", useUnmergedTree = true).assertDoesNotExist()
    composeTestRule
        .onNodeWithTag("expenseTitle1", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("Groceries")
    composeTestRule
        .onNodeWithTag("expenseAmount1", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("50.00 CHF")
    composeTestRule
        .onNodeWithTag("expenseUser1", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("Paid by Alice")
  }

  @Test
  fun financeWidgetDisplaysProperlyTwo() = run {
    val viewModel = DashboardViewModelTest(emptyList())
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }
    viewModel.setExpenses(listOf(expense1, expense2))

    composeTestRule.onNodeWithTag("noExpenses", useUnmergedTree = true).assertDoesNotExist()
    composeTestRule.onNodeWithTag("noExpensesBox", useUnmergedTree = true).assertDoesNotExist()
    composeTestRule.onNodeWithTag("pieChartBox", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("totalAmount", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("Total: 75.00 CHF")
    composeTestRule.onNodeWithTag("expenseItem1", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("expenseItem2", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("expenseItem3", useUnmergedTree = true).assertDoesNotExist()
    composeTestRule
        .onNodeWithTag("expenseTitle1", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("Groceries")
    composeTestRule
        .onNodeWithTag("expenseAmount1", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("50.00 CHF")
    composeTestRule
        .onNodeWithTag("expenseUser1", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("Paid by Alice")
    composeTestRule
        .onNodeWithTag("expenseTitle2", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("Movie Night")
    composeTestRule
        .onNodeWithTag("expenseAmount2", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("25.00 CHF")
    composeTestRule
        .onNodeWithTag("expenseUser2", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("Paid by Bob")
  }

  @Test
  fun financeWidgetDisplaysProperlyThree() = run {
    val viewModel = DashboardViewModelTest(emptyList())
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }

    viewModel.setExpenses(listOf(expense1, expense2, expense3))

    composeTestRule.onNodeWithTag("noExpenses", useUnmergedTree = true).assertDoesNotExist()
    composeTestRule.onNodeWithTag("noExpensesBox", useUnmergedTree = true).assertDoesNotExist()
    composeTestRule.onNodeWithTag("pieChartBox", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("totalAmount", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("Total: 175.00 CHF")
    composeTestRule.onNodeWithTag("expenseItem1", useUnmergedTree = true).assertDoesNotExist()
    composeTestRule.onNodeWithTag("expenseItem2", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("expenseItem3", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("expenseTitle2", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("Movie Night")
    composeTestRule
        .onNodeWithTag("expenseAmount2", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("25.00 CHF")
    composeTestRule
        .onNodeWithTag("expenseUser2", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("Paid by Bob")
    composeTestRule
        .onNodeWithTag("expenseTitle3", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("Dinner")
    composeTestRule
        .onNodeWithTag("expenseAmount3", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("100.00 CHF")
    composeTestRule
        .onNodeWithTag("expenseUser3", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("Paid by Charlie")
  }

  @Test
  fun financeWidgetNavigation() = run {
    val viewModel = DashboardViewModelTest(emptyList())
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }

    composeTestRule.onNodeWithTag("financeCard").performClick()
    verify { mockNavActions.navigateTo(Route.FINANCE) }
    confirmVerified(mockNavActions)
  }

  @Test
  fun StopWidgetNavigation() = run {
    val viewModel = DashboardViewModelTest(emptyList())
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }

    composeTestRule.onNodeWithTag("stopCard").performClick()
    verify { mockNavActions.navigateTo(Route.AGENDA) }
    confirmVerified(mockNavActions)
  }

  @Test
  fun StopWidgetDisplaysProperly() = run {
    val viewModel = DashboardViewModelTest(emptyList())
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }

    composeTestRule.onNodeWithTag("stopCard", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("stopTitle", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("totalStops", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("noStops", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("stopIcon", useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun StopWidgetDisplaysNothing() = run {
    val viewModel = DashboardViewModelTest(emptyList())
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }

    viewModel.setStops(
        listOf(
            Stop(date = LocalDate.now().minusDays(1), startTime = LocalTime.now()),
            Stop(date = LocalDate.now(), startTime = LocalTime.now().minusSeconds(1))))

    composeTestRule.onNodeWithTag("noStops", useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun stopWidgetDisplaysProperlyOne() = run {
    val viewModel = DashboardViewModelTest(listOf(suggestion1))
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }

    viewModel.setStops(listOf(suggestion1.stop))

    composeTestRule.onNodeWithTag("noStops", useUnmergedTree = true).assertDoesNotExist()
    composeTestRule
        .onNodeWithTag("totalStops", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("Total: 1 stop")
    composeTestRule.onNodeWithTag("stopItem1", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("stopItem2", useUnmergedTree = true).assertDoesNotExist()
    composeTestRule
        .onNodeWithTag("stopTitle1", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("Stop Title")
    composeTestRule
        .onNodeWithTag("stopAddress1", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("123 Street")
    composeTestRule
        .onNodeWithTag("stopStart1", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains(
            LocalDateTime.of(suggestion1.stop.date, suggestion1.stop.startTime)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
    composeTestRule
        .onNodeWithTag("stopEnd1", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains(
            LocalDateTime.of(suggestion1.stop.date, suggestion1.stop.startTime)
                .plusMinutes(suggestion1.stop.duration.toLong())
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
  }

  @Test
  fun stopWidgetDisplaysProperlyTwo() = run {
    val viewModel = DashboardViewModelTest(listOf(suggestion1))
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }

    viewModel.setStops(listOf(suggestion1.stop, suggestion2.stop))

    composeTestRule.onNodeWithTag("noStops", useUnmergedTree = true).assertDoesNotExist()
    composeTestRule
        .onNodeWithTag("totalStops", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("Total: 2 stops")
    composeTestRule.onNodeWithTag("stopItem1", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("stopItem2", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("stopItem3", useUnmergedTree = true).assertDoesNotExist()
    composeTestRule
        .onNodeWithTag("stopTitle2", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("Stop Title")
    composeTestRule
        .onNodeWithTag("stopAddress2", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("123 Street")
    composeTestRule
        .onNodeWithTag("stopStart2", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains(
            LocalDateTime.of(suggestion2.stop.date, suggestion2.stop.startTime)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
    composeTestRule
        .onNodeWithTag("stopEnd2", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains(
            LocalDateTime.of(suggestion2.stop.date, suggestion2.stop.startTime)
                .plusMinutes(suggestion2.stop.duration.toLong())
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
  }

  @Test
  fun stopWidgetDisplaysProperlyThree() = run {
    val viewModel = DashboardViewModelTest(listOf(suggestion1))
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }

    viewModel.setStops(listOf(suggestion1.stop, suggestion2.stop, suggestion3.stop))

    composeTestRule.onNodeWithTag("noStops", useUnmergedTree = true).assertDoesNotExist()
    composeTestRule
        .onNodeWithTag("totalStops", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("Total: 3 stops")
    composeTestRule.onNodeWithTag("stopItem3", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("stopItem2", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("stopItem1", useUnmergedTree = true).assertDoesNotExist()
    composeTestRule
        .onNodeWithTag("stopTitle3", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("Stop Title")
    composeTestRule
        .onNodeWithTag("stopAddress3", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains("123 Street")
    composeTestRule
        .onNodeWithTag("stopStart3", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains(
            LocalDateTime.of(suggestion3.stop.date, suggestion3.stop.startTime)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
    composeTestRule
        .onNodeWithTag("stopEnd3", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextContains(
            LocalDateTime.of(suggestion3.stop.date, suggestion3.stop.startTime)
                .plusMinutes(suggestion3.stop.duration.toLong())
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
  }

  @Test
  fun deleteTripWorks() = run {
    val viewModel = DashboardViewModelTest(emptyList())
    viewModel.setLoading(false)
    SessionManager.setUserSession(role = Role.OWNER)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }

    composeTestRule.onNodeWithTag("menuButton", useUnmergedTree = true).performClick()
    composeTestRule.onNodeWithTag("deleteTripButton", useUnmergedTree = true).performClick()
    composeTestRule.onNodeWithTag("deleteTripDialog", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmDeleteTripButton", useUnmergedTree = true).performClick()

    verify { mockNavActions.navigateTo(Route.OVERVIEW) }
    confirmVerified(mockNavActions)
  }

  @Test
  fun deleteTripDoesntWorkOffline() = run {
    val viewModel = DashboardViewModelTest(emptyList())
    viewModel.setLoading(false)
    SessionManager.setIsNetworkAvailable(false)
    SessionManager.setUserSession(role = Role.OWNER)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }

    composeTestRule.onNodeWithTag("menuButton", useUnmergedTree = true).performClick()
    composeTestRule.onNodeWithTag("deleteTripButton", useUnmergedTree = true).assertIsNotDisplayed()

    verify { mockNavActions wasNot Called }
    confirmVerified(mockNavActions)
    SessionManager.setIsNetworkAvailable(true)
  }

  @Test
  fun deleteTripCancel() = run {
    val viewModel = DashboardViewModelTest(emptyList())
    viewModel.setLoading(false)
    SessionManager.setUserSession(role = Role.OWNER)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }

    composeTestRule.onNodeWithTag("menuButton", useUnmergedTree = true).performClick()
    composeTestRule.onNodeWithTag("deleteTripButton", useUnmergedTree = true).performClick()
    composeTestRule.onNodeWithTag("deleteTripDialog", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancelDeleteTripButton", useUnmergedTree = true).performClick()
    composeTestRule.onNodeWithTag("deleteTripDialog", useUnmergedTree = true).assertDoesNotExist()
  }

  @Test
  fun deleteTripNotOwner() = run {
    val viewModel = DashboardViewModelTest(emptyList())
    viewModel.setLoading(false)
    SessionManager.setUserSession(role = Role.ADMIN)
    composeTestRule.setContent {
      Dashboard(tripId = "", dashboardViewModel = viewModel, navActions = mockNavActions)
    }

    composeTestRule.onNodeWithTag("menuButton", useUnmergedTree = true).performClick()
    composeTestRule.onNodeWithTag("deleteTripButton", useUnmergedTree = true).assertDoesNotExist()
  }
}
