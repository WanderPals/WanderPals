package com.github.se.wanderpals.ui.screens.finance

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.data.Category
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.ExpenseViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExpense(viewModel: ExpenseViewModel) {

    val context = LocalContext.current
    var expandedMenu1 by remember { mutableStateOf(false) }
    var expandedMenu2 by remember { mutableStateOf(false) }
    var selectedMenu1 by remember { mutableStateOf("") }
    var selectedMenu2 by remember { mutableStateOf("") }
    var expenseTitle by remember { mutableStateOf("") }

    val users by viewModel.users.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add an expense",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("createExpenseTitle")
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { }, modifier = Modifier.testTag("BackButton")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                        )
                    }
                },
                colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                ),
            )
        }) { paddingValues ->
            // Content
        Column(modifier = Modifier
            .testTag("createExpenseContent")
            .padding(paddingValues)
            .background(Color.White)) {

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            Spacer(modifier = Modifier.padding(4.dp))

            // Content
            OutlinedTextField(
                value = expenseTitle,
                onValueChange = { expenseTitle = it},
                label = { Text("Expense title") },
                placeholder = { Text("Give a name to your expense") },
                modifier = Modifier
                    .testTag("expenseTitle")
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .fillMaxWidth())

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Budget") },
                placeholder = { Text("Assign a budget") },
                modifier = Modifier
                    .testTag("Budget")
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .fillMaxWidth())

            //make it take only numbers

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Date") },
                placeholder = { Text(" -- / -- / ---- ") },
                modifier = Modifier
                    .testTag("expenseTitle")
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .fillMaxWidth())

            //add datePicker

            ExposedDropdownMenuBox(
                expanded = expandedMenu1,
                onExpandedChange = { expandedMenu1 = !expandedMenu1 },
                modifier = Modifier
                    .testTag("dropdownMenu")
                    .padding(24.dp, 8.dp)
                    .fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedMenu1,
                    onValueChange = {},
                    label = { Text("Paid by") },
                    modifier = Modifier
                        .testTag("paidBy")
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true)
                ExposedDropdownMenu(
                    expanded = expandedMenu1,
                    onDismissRequest = { expandedMenu1 = false },
                ) {
                    users.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item.name) },
                            onClick = {
                                selectedMenu1 = item.name
                                expandedMenu1 = false
                            },
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }

            // Add the viewModel with the list of users

            ExposedDropdownMenuBox(
                expanded = expandedMenu2,
                onExpandedChange = { expandedMenu2 = !expandedMenu2 },
                modifier = Modifier
                    .testTag("dropdownMenu")
                    .padding(24.dp, top = 8.dp, bottom = 16.dp, end = 24.dp)
                    .fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedMenu2,
                    onValueChange = {},
                    label = { Text("Category") },
                    modifier = Modifier
                        .testTag("expenseTitle")
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true)
                ExposedDropdownMenu(
                    expanded = expandedMenu2,
                    onDismissRequest = { expandedMenu2 = false },
                ) {
                    Category.values().forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item.name) },
                            onClick = {
                                selectedMenu2 = item.name
                                expandedMenu2 = false
                            },
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(24.dp, bottom = 16.dp)) {
                Checkbox(checked = false, onCheckedChange = {})
                Text(text = "FOR WHOM", color = Color.Black, style = MaterialTheme.typography.labelMedium)
            }

            Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primaryContainer) {
                LazyColumn(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)) {
                    items(users.size) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(24.dp, 8.dp)) {
                            Checkbox(checked = false, onCheckedChange = {})
                            Text(text = users[it].name, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }

                    // Add the viewModel with the list of users
                }
            }

            Spacer(modifier = Modifier.padding(4.dp))

            // fix this for other screen

            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                ExtendedFloatingActionButton(onClick = { }, modifier = Modifier.fillMaxWidth(0.7f), containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                    Text(text = "Save", color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateExpensePreview() {
    CreateExpense(ExpenseViewModel(TripsRepository("", Dispatchers.IO), ""))
}