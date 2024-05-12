package com.github.se.wanderpals.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.viewmodel.OverviewViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * Custom interaction source for date picker.
 *
 * Needed because the OutlinedTextField does not have a click functionality that works while being
 * enabled (so testable)
 *
 * @param onClick action to be executed when the date picker is clicked
 */
class DateInteractionSource(val onClick: () -> Unit) : MutableInteractionSource {

  override val interactions =
      MutableSharedFlow<Interaction>(
          extraBufferCapacity = 16,
          onBufferOverflow = BufferOverflow.DROP_OLDEST,
      )

  override suspend fun emit(interaction: Interaction) {
    if (interaction is PressInteraction.Release) {
      onClick()
    }

    interactions.emit(interaction)
  }

  override fun tryEmit(interaction: Interaction): Boolean {
    return interactions.tryEmit(interaction)
  }
}

/**
 * Screen for creating a new trip.
 *
 * The user can input the title, budget, description, start date, and end date of the trip. The
 * dates are selected using a date picker dialog present in another function
 *
 * @param tripViewModel view model for creating a trip
 * @param nav navigation actions
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateTrip(overviewViewModel: OverviewViewModel, nav: NavigationActions) {

  val createTripFinished by overviewViewModel.createTripFinished.collectAsState()

  // Effect to react to the createTripFinished state change
  LaunchedEffect(createTripFinished) {
    if (createTripFinished) {
      nav.navigateTo(Route.OVERVIEW) // navigate to overview, after add trip is done
      overviewViewModel.resetCreateTripFinished() // Reset the flag after handling it
    }
  }

  val MAX_TITLE_LENGTH = 35

  var name by remember { mutableStateOf("") }
  var budget by remember { mutableStateOf("0") }
  var startDate by remember { mutableStateOf("") }
  var endDate by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }

  var errorText by remember { mutableStateOf("") }
  var showDatePickerStart by remember { mutableStateOf(false) }
  var showDatePickerEnd by remember { mutableStateOf(false) }

  Scaffold(
      modifier = Modifier.testTag("createTripScreen"),
      topBar = {
        Row {
          OutlinedButton(
              modifier = Modifier.testTag("goBackButton").padding(16.dp),
              onClick = { nav.navigateTo(Route.OVERVIEW) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                )
              }

          Text(
              text = "Create a new Trip",
              modifier = Modifier.testTag("createTripTitle").padding(28.dp))
        }
      },
      content = {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              OutlinedTextField(
                  value = name,
                  onValueChange = { newTitle ->
                    if (newTitle.length <= MAX_TITLE_LENGTH) {
                      name = newTitle
                    }
                  },
                  label = { Text("Title") },
                  modifier =
                      Modifier.testTag("inputTripTitle").fillMaxWidth().padding(bottom = 16.dp),
                  isError = errorText.isNotEmpty(),
                  singleLine = true,
                  placeholder = { Text("Name the trip") })

              Text(
                  text = "${name.length}/$MAX_TITLE_LENGTH",
                  modifier = Modifier.align(Alignment.End).testTag("titleLengthText"),
                  style = TextStyle(fontSize = 12.sp, color = Color.Gray),
              )
              OutlinedTextField(
                  value = budget,
                  onValueChange = { budget = it },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  label = { Text("Budget") },
                  modifier =
                      Modifier.testTag("inputTripBudget").fillMaxWidth().padding(bottom = 16.dp),
                  isError = errorText.isNotEmpty(),
                  singleLine = true,
                  placeholder = { Text("Enter the total budget") })

              OutlinedTextField(
                  value = description,
                  onValueChange = { description = it },
                  label = { Text("Describe") },
                  modifier =
                      Modifier.testTag("inputTripDescription")
                          .fillMaxWidth()
                          .height(200.dp)
                          .padding(bottom = 16.dp),
                  isError = errorText.isNotEmpty(),
                  placeholder = { Text("Describe the trip") })

              Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    placeholder = { Text("dd/mm/yyyy") },
                    modifier =
                        Modifier.size(150.dp, 50.dp)
                            .padding(end = 16.dp)
                            .testTag("inputTripStartDate")
                            .clickable { showDatePickerStart = true },
                    isError = errorText.isNotEmpty(),
                    singleLine = true,
                    interactionSource = DateInteractionSource { showDatePickerStart = true })
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    placeholder = { Text("dd/mm/yyyy") },
                    modifier = Modifier.size(150.dp, 50.dp).testTag("inputTripEndDate"),
                    isError = errorText.isNotEmpty(),
                    singleLine = true,
                    interactionSource = DateInteractionSource { showDatePickerEnd = true })
              }
              if (showDatePickerStart) {
                MyDatePickerDialog(
                    onDateSelected = { startDate = it },
                    onDismiss = { showDatePickerStart = false })
              }

              if (showDatePickerEnd) {
                MyDatePickerDialog(
                    onDateSelected = { endDate = it }, onDismiss = { showDatePickerEnd = false })
              }

              Text(
                  text = errorText,
                  color = Color.Red,
              )

              Button(
                  modifier = Modifier.testTag("tripSave").fillMaxWidth().padding(16.dp),
                  onClick = {
                    val error = validateInputs(name, budget, description, startDate, endDate)
                    if (error.isNotEmpty()) {
                      errorText = error
                    } else {

                      val startDateTrip =
                          LocalDate.of(
                              startDate.split("/")[2].toInt(),
                              startDate.split("/")[1].toInt(),
                              startDate.split("/")[0].toInt())
                      val endDateTrip =
                          LocalDate.of(
                              endDate.split("/")[2].toInt(),
                              endDate.split("/")[1].toInt(),
                              endDate.split("/")[0].toInt())

                      val trip =
                          Trip(
                              tripId = "",
                              title = name,
                              startDate = startDateTrip,
                              endDate = endDateTrip,
                              totalBudget = budget.toDouble(),
                              description = description,
                              imageUrl = "",
                              stops = emptyList(),
                              users = emptyList(),
                              suggestions = emptyList())
                      overviewViewModel.createTrip(trip)
                      // navigation handled by the launched effects
                    }
                  },
              ) {
                Text("Save")
              }
            }
      })
}

/**
 * Date picker dialog for selecting a date.
 *
 * @param onDateSelected action to be executed when a date is selected
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(onDateSelected: (String) -> Unit, onDismiss: () -> Unit) {
  val datePickerState = rememberDatePickerState()

  val selectedDate = datePickerState.selectedDateMillis?.let { convertMillisToDate(it) } ?: ""

  DatePickerDialog(
      onDismissRequest = { onDismiss() },
      confirmButton = {
        Button(
            onClick = {
              onDateSelected(selectedDate)
              onDismiss()
            }) {
              Text(text = "OK")
            }
      },
      dismissButton = { Button(onClick = { onDismiss() }) { Text(text = "Cancel") } }) {
        DatePicker(state = datePickerState, modifier = Modifier.testTag("datePicker"))
      }
}

/**
 * Converts milliseconds to a date string.
 *
 * @param millis milliseconds to convert
 * @return date string in the format "dd/MM/yyyy"
 */
@SuppressLint("SimpleDateFormat")
private fun convertMillisToDate(millis: Long): String {
  val formatter = SimpleDateFormat("dd/MM/yyyy")
  return formatter.format(Date(millis))
}

/**
 * Validates the input fields of the trip creation screen and returns an error message if any of
 * them are empty or the start date is after the end date.
 *
 * @param name title of the trip
 * @param description description of the trip
 */
private fun validateInputs(
    name: String,
    budget: String,
    description: String,
    startDate: String,
    endDate: String
): String {
  val errors = mutableListOf<String>()

  if (name.isBlank()) errors.add("Title")
  if (description.isBlank()) errors.add("Description")
  if (startDate.isBlank()) errors.add("Start date")
  if (endDate.isBlank()) errors.add("End date")

  val startDateParsed =
      if (startDate.isNotBlank())
          LocalDate.of(
              startDate.split("/")[2].toInt(),
              startDate.split("/")[1].toInt(),
              startDate.split("/")[0].toInt())
      else null

  val endDateParsed =
      if (endDate.isNotBlank())
          LocalDate.of(
              endDate.split("/")[2].toInt(),
              endDate.split("/")[1].toInt(),
              endDate.split("/")[0].toInt())
      else null

  var errorText =
      when {
        errors.isEmpty() -> ""
        errors.size == 1 -> "${errors.first()} cannot be empty!"
        else -> errors.joinToString(separator = ", ", postfix = " cannot be empty!")
      }

  if (startDateParsed != null && endDateParsed != null && startDateParsed.isAfter(endDateParsed)) {
    errorText =
        if (errorText.isNotEmpty()) {
          "$errorText, start date must be before end date!"
        } else {
          "Start date must be before end date!"
        }
  }

  // Check that budget can be converted to a double
  try {
    budget.toDouble()
    if (budget.toDouble() < 0) {
      errorText =
          if (errorText.isNotEmpty()) {
            "$errorText and budget must be a positive number!"
          } else {
            "Budget must be a positive number!"
          }
    }
  } catch (e: NumberFormatException) {
    errorText =
        if (errorText.isNotEmpty()) {
          "$errorText, budget must be a number!"
        } else {
          "Budget must be a number!"
        }
  }

  return errorText
}
