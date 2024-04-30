package com.github.se.wanderpals.ui.screens.trip.finance



import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun FinancePreview() {
    Finance()
}

enum class FinanceOption(val optionName: String) {
    EXPENSES("Expenses"),
    CATEGORIES("Categories"),
    DEBTS("Debts");

    override fun toString(): String {
        return this.optionName
    }
}


/** The Finance screen. */
@Composable
fun Finance() {
    var currentSelectedOption by remember { mutableStateOf(FinanceOption.EXPENSES) }

    Scaffold(
        topBar = {
            FinanceTopBar(
                currentSelectedOption = currentSelectedOption,
                onSelectOption = { newOption -> currentSelectedOption = newOption })
        },
        bottomBar = {
            if (currentSelectedOption == FinanceOption.EXPENSES) {
                FinanceBottomBar()
            }
        },
        floatingActionButton = {
            if (currentSelectedOption == FinanceOption.EXPENSES) {
                FloatingActionButton(
                    onClick = { /* TODO navigate to create expense screen*/ },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        Icons.Default.Add.name,
                        modifier = Modifier.size(35.dp),
                        tint = Color.White
                    )
                }
            }
        }

    ) { innerPadding ->
        when (currentSelectedOption) {
            FinanceOption.EXPENSES -> {
                LazyColumn(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxHeight()) {

                }
                // Contenu pour l'option Expenses
            }

            FinanceOption.CATEGORIES -> {
                // to be implemented
            }

            FinanceOption.DEBTS -> {
                // to be implemented
            }
        }
    }
}


