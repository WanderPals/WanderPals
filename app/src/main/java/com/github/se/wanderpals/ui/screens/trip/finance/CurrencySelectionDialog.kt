package com.github.se.wanderpals.ui.screens.trip.finance

import android.icu.util.Currency
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.se.wanderpals.model.viewmodel.FinanceViewModel

/**
 * A composable function that displays a dialog for currency selection. The user can search the
 * currency among a list to update the currency used for the trip
 *
 * @param financeViewModel The [FinanceViewModel] used to manage the currency selection state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelectionDialog(financeViewModel: FinanceViewModel) {

  var searchedCurrency by remember { mutableStateOf("") }
  var isError by remember { mutableStateOf(false) }
  val currencies = Currency.getAvailableCurrencies().filterNot { it.displayName.contains("(") }

  Dialog(
      onDismissRequest = {
        isError = false
        financeViewModel.setShowCurrencyDialogState(false)
      }) {
        Surface(
            modifier =
                Modifier.fillMaxSize()
                    .padding(top = 100.dp, bottom = 100.dp)
                    .testTag("currencyDialog"),
            color = MaterialTheme.colorScheme.background,
        ) {
          Column(
              modifier = Modifier.fillMaxSize(),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Top) {
                // Text allowing user to input text and perform search
                OutlinedTextField(
                    value = searchedCurrency,
                    onValueChange = { value -> searchedCurrency = value },
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(20.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .testTag("currencySearchText"),
                    label = {
                      Text(
                          text = if (isError) "Invalid currency" else "Select a currency",
                          color =
                              if (isError) MaterialTheme.colorScheme.error
                              else MaterialTheme.colorScheme.onSurface)
                    },
                    colors =
                        TextFieldDefaults.textFieldColors(
                            containerColor = Color.White, errorContainerColor = Color.White),
                    isError = isError,

                    // Button for validation checking of the selected currency
                    trailingIcon = {
                      IconButton(
                          modifier = Modifier.testTag("currencyValidationButton"),
                          onClick = {
                            val newCurrency =
                                currencies.find {
                                  it.displayName.equals(searchedCurrency, ignoreCase = true) ||
                                      it.currencyCode.equals(searchedCurrency, ignoreCase = true)
                                }
                            if (newCurrency != null) {
                              financeViewModel.updateCurrency(newCurrency.currencyCode)
                              financeViewModel.setShowCurrencyDialogState(false)
                              isError = false
                            } else {
                              isError = true
                            }
                          }) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = Icons.Default.CheckCircle.name,
                                tint = MaterialTheme.colorScheme.primary)
                          }
                    },
                    maxLines = 1,
                )

                // Currency list to display, based on user search input
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top) {
                      // currencies filtered based on text input search
                      val filteredCurrencies =
                          currencies
                              .toList()
                              .filter {
                                it.displayName.contains(searchedCurrency, ignoreCase = true) ||
                                    it.currencyCode.contains(searchedCurrency, ignoreCase = true)
                              }
                              .sortedBy { it.displayName }
                      items(filteredCurrencies) { currency ->
                        Box(
                            modifier =
                                Modifier.fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .border(
                                        1.dp,
                                        Color.Gray,
                                        RoundedCornerShape(5.dp),
                                    )
                                    .testTag("currencyItem"),
                            contentAlignment = Alignment.CenterStart) {
                              Button(
                                  modifier = Modifier.fillMaxWidth(),
                                  colors =
                                      ButtonDefaults.buttonColors(
                                          containerColor = Color.Transparent),
                                  onClick = { searchedCurrency = currency.displayName }) {}

                              Text(
                                  modifier = Modifier.padding(start = 5.dp),
                                  text = currency.displayName,
                                  textAlign = TextAlign.Start,
                                  maxLines = 1,
                                  overflow = TextOverflow.Ellipsis)
                            }
                        Spacer(modifier = Modifier.height(7.dp))
                      }
                    }
              }
        }
      }
}
