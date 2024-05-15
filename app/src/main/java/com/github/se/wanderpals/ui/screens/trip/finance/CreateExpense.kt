package com.github.se.wanderpals.ui.screens.trip.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.data.Category
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.viewmodel.ExpenseViewModel
import com.github.se.wanderpals.model.viewmodel.FinanceViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.screens.DateInteractionSource
import com.github.se.wanderpals.ui.screens.MyDatePickerDialog
import com.github.se.wanderpals.ui.theme.WanderPalsTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Composable function to create an expense. It allows the user to input the title, amount, date,
 * category, user who paid, and participants. The user can select the participants from the list of
 * users and he can save the expense if all fields are filled and at least one participant is
 * selected. If not, an error message is displayed at the bottom of the screen.
 *
 * @param tripId The id of the trip.
 * @param viewModel The [ExpenseViewModel] to handle the expense creation.
 * @param navActions The navigation actions to navigate back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExpense(
    tripId: String,
    viewModel: FinanceViewModel,
    navActions: NavigationActions,
    onDone: () -> Unit = {}
) {

  LaunchedEffect(key1 = Unit) { viewModel.loadMembers(tripId) }

  var expandedMenu1 by remember { mutableStateOf(false) }
  var expandedMenu2 by remember { mutableStateOf(false) }
  var selectedMenu1: User? by remember { mutableStateOf(null) }
  var selectedMenu2 by remember { mutableStateOf("") }
  var expenseTitle by remember { mutableStateOf("") }
  var expenseAmount by remember { mutableStateOf("") }
  var expenseDate by remember { mutableStateOf("") }
  var showDatePicker: Boolean by remember { mutableStateOf(false) }

  var errorText by remember { mutableStateOf("") }

  val users by viewModel.users.collectAsState()

  var checkboxes = remember { mutableStateListOf<Boolean>() }

  if (checkboxes.size != users.size) {
    checkboxes = users.map { false }.toMutableStateList()
  }

  WanderPalsTheme {
    Scaffold(
        topBar = {
          TopAppBar(
              title = {
                Text(
                    text = "Add an expense",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.testTag("createExpenseTitle"))
              },
              navigationIcon = {
                IconButton(
                    onClick = { navActions.goBack() }, modifier = Modifier.testTag("BackButton")) {
                      Icon(
                          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                          contentDescription = "Go back",
                      )
                    }
              },
              colors =
                  TopAppBarDefaults.topAppBarColors(
                      containerColor = MaterialTheme.colorScheme.primaryContainer,
                  ),
          )
        }) { paddingValues ->
          Box(modifier = Modifier.padding(paddingValues).fillMaxSize().background(Color.White)) {
            Box(modifier = Modifier.verticalScroll(rememberScrollState(), true)) {
              Column(modifier = Modifier.testTag("createExpenseContent").background(Color.White)) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                Spacer(modifier = Modifier.padding(4.dp))

                // Title text field
                OutlinedTextField(
                    value = expenseTitle,
                    onValueChange = { if (it.length <= 50) expenseTitle = it },
                    label = { Text("Expense title") },
                    placeholder = { Text("Give a name to your expense") },
                    modifier =
                        Modifier.testTag("expenseTitle")
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                    singleLine = true,
                    suffix = {
                      Text(
                          text = "${expenseTitle.length}/50",
                          style = MaterialTheme.typography.bodyMedium)
                    },
                    isError = errorText.isNotEmpty(),
                )
                // Amount text field
                OutlinedTextField(
                    value = expenseAmount,
                    onValueChange = { if (it.length <= 20) expenseAmount = it },
                    label = { Text("Amount") },
                    placeholder = { Text("") },
                    modifier =
                        Modifier.testTag("Budget")
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    suffix = {
                      Text(
                          text = "CHF | ${expenseAmount.length}/20",
                          style = MaterialTheme.typography.bodyMedium)
                    },
                    singleLine = true, // add the currency
                    isError = errorText.isNotEmpty())

                // DatePicker
                OutlinedTextField(
                    value = expenseDate,
                    onValueChange = { expenseDate = it },
                    label = { Text("Date") },
                    placeholder = { Text(" -- / -- / ---- ") },
                    modifier =
                        Modifier.testTag("expenseDate")
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                    singleLine = true,
                    interactionSource = DateInteractionSource { showDatePicker = true },
                    isError = errorText.isNotEmpty())

                if (showDatePicker) {
                  MyDatePickerDialog(
                      onDateSelected = { expenseDate = it }, onDismiss = { showDatePicker = false })
                }
                // Dropdown menu for the user who paid
                ExposedDropdownMenuBox(
                    expanded = expandedMenu1,
                    onExpandedChange = { expandedMenu1 = !expandedMenu1 },
                    modifier =
                        Modifier.testTag("dropdownMenuPaid").padding(24.dp, 8.dp).fillMaxWidth()) {
                      // Text field for the user who paid
                      OutlinedTextField(
                          value = selectedMenu1?.name ?: "",
                          onValueChange = {},
                          label = { Text("Paid by") },
                          modifier = Modifier.testTag("paidBy").fillMaxWidth().menuAnchor(),
                          readOnly = true,
                          isError = errorText.isNotEmpty())
                      // Dropdown menu for the user who paid
                      ExposedDropdownMenu(
                          expanded = expandedMenu1,
                          onDismissRequest = { expandedMenu1 = false },
                      ) {
                        users.forEach { item ->
                          // Dropdown menu item for each user
                          DropdownMenuItem(
                              text = { Text(text = item.name) },
                              onClick = {
                                selectedMenu1 = item
                                expandedMenu1 = false
                              },
                              modifier =
                                  Modifier.padding(horizontal = 24.dp)
                                      .fillMaxWidth()
                                      .testTag(item.userId))
                        }
                      }
                    }
                // Category dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedMenu2,
                    onExpandedChange = { expandedMenu2 = !expandedMenu2 },
                    modifier =
                        Modifier.testTag("dropdownMenuCategory")
                            .padding(24.dp, top = 8.dp, bottom = 16.dp, end = 24.dp)
                            .fillMaxWidth()) {
                      // Text field for the category
                      OutlinedTextField(
                          value = selectedMenu2,
                          onValueChange = {},
                          label = { Text("Category") },
                          modifier = Modifier.fillMaxWidth().menuAnchor().testTag("category"),
                          readOnly = true,
                          isError = errorText.isNotEmpty())
                      // Dropdown menu for the category
                      ExposedDropdownMenu(
                          expanded = expandedMenu2,
                          onDismissRequest = { expandedMenu2 = false },
                      ) {
                        Category.values().forEach { item ->
                          // Dropdown menu item for each category
                          DropdownMenuItem(
                              text = { Text(text = item.name) },
                              onClick = {
                                selectedMenu2 = item.name
                                expandedMenu2 = false
                              },
                              modifier =
                                  Modifier.padding(horizontal = 24.dp)
                                      .fillMaxWidth()
                                      .testTag(item.name))
                        }
                      }
                    }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp, bottom = 16.dp)) {
                      Checkbox(
                          checked = checkboxes.all { it },
                          onCheckedChange = {
                            if (checkboxes.all { it }) checkboxes.replaceAll { false }
                            else checkboxes.replaceAll { true }
                          },
                          modifier = Modifier.testTag("checkboxAll"))
                      Text(
                          text = "FOR WHOM",
                          color = Color.Black,
                          style = MaterialTheme.typography.labelMedium,
                      )
                    }
                // List of checkboxes and username to select participants
                Surface(modifier = Modifier.fillMaxWidth(), color = Color.White) {
                  LazyColumn(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    if (users.isEmpty()) {
                      item {
                        // edge case: no users found
                        Text(
                            text = "No users found, this should not happen.",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(16.dp).testTag("noUsersFound"))
                      }
                    } else {
                      items(users.size) { index ->
                        // Simple row with a checkbox and the username
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier =
                                Modifier.background(
                                        if (checkboxes[index])
                                            MaterialTheme.colorScheme.primaryContainer
                                        else Color.White)
                                    .fillMaxWidth()
                                    .padding(16.dp, 8.dp)
                                    .testTag("userRow$index")) {
                              Checkbox(
                                  checked = checkboxes[index],
                                  onCheckedChange = { checkboxes[index] = !checkboxes[index] },
                                  modifier = Modifier.testTag("checkbox$index"))
                              Text(
                                  text = users[index].name,
                                  color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                      }
                    }
                  }
                }
              }
            }
            // Save button
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.padding().fillMaxSize()) {
                  Column(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalAlignment = Alignment.CenterHorizontally) {
                        ExtendedFloatingActionButton(
                            onClick = {
                              // Check if all fields are filled and at least one participant is
                              // selected before saving
                              if (selectedMenu1 == null ||
                                  selectedMenu2.isEmpty() ||
                                  expenseTitle.isEmpty() ||
                                  expenseAmount.toDoubleOrNull() == null ||
                                  expenseDate.isEmpty() ||
                                  checkboxes.all { !it }) {
                                errorText =
                                    "Please fill in all fields and select at least one participant."
                              } else {
                                val selectedUsers =
                                    users.filterIndexed { index, _ -> checkboxes[index] }
                                val expense =
                                    Expense(
                                        expenseId = "",
                                        title = expenseTitle,
                                        amount = expenseAmount.toDouble(),
                                        localDate =
                                            LocalDate.parse(
                                                expenseDate,
                                                DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                        category = Category.valueOf(selectedMenu2),
                                        userName = selectedMenu1!!.name,
                                        userId = selectedMenu1!!.userId,
                                        participantsIds = selectedUsers.map { it.userId },
                                        names = selectedUsers.map { it.name })
                                errorText = ""
                                viewModel.addExpense(tripId, expense)
                                onDone()
                                navActions.goBack()
                              }
                            },
                            modifier =
                                Modifier.fillMaxWidth(0.5f)
                                    .padding(bottom = if (errorText.isNotEmpty()) 4.dp else 10.dp)
                                    .testTag("saveButton"),
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            elevation = FloatingActionButtonDefaults.elevation(2.dp, 1.dp)) {
                              Text(
                                  text = "Save",
                                  color = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        // Error text
                        if (errorText.isNotEmpty()) {
                          Text(
                              text = errorText,
                              color = Color.Red,
                              style = MaterialTheme.typography.bodyMedium,
                              modifier = Modifier.padding(bottom = 10.dp).testTag("errorText"))
                        }
                      }
                }
          }
        }
  }
}
