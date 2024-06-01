package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.viewmodel.CreateSuggestionViewModel
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.screens.DateInteractionSource
import com.github.se.wanderpals.ui.screens.MyDatePickerDialog
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * CreateSuggestion composable responsible for adding a suggestion to a trip
 *
 * @param tripId the id of the trip
 * @param viewModel a CreateSuggestionViewModel that needs to be initialized beforehand
 * @param suggestion the suggestion to be created (default is an empty suggestion)
 * @param onSuccess additional code to execute after the successful creation of the suggestion (can
 *   be empty)
 * @param onFailure code to execute if the creation of the suggestion fails (can be empty)
 * @param onCancel code to execute if the user cancels the creation of the suggestion (can be empty)
 */
@Composable
fun CreateOrEditSuggestion(
    tripId: String,
    viewModel: CreateSuggestionViewModel,
    suggestion: Suggestion = Suggestion(),
    onSuccess: () -> Unit = {},
    onFailure: () -> Unit = {},
    onCancel: () -> Unit = {}
) {

  var description by remember { mutableStateOf(suggestion.stop.description) }
  var address by remember { mutableStateOf(suggestion.stop.address) }
  var website by remember { mutableStateOf(suggestion.stop.website) }
  var suggestionText by remember { mutableStateOf(suggestion.stop.title) }
  var budget by remember {
    mutableStateOf(if (suggestion.stop.budget.isNaN()) "" else suggestion.stop.budget.toString())
  }
  var startDate by remember {
    mutableStateOf(
        if (suggestion.stop.date == LocalDate.of(0, 1, 1)) ""
        else suggestion.stop.date.format(DateTimeFormatter.ISO_LOCAL_DATE))
  }
  var startTime by remember {
    mutableStateOf(
        if (suggestion.stop.startTime == LocalTime.of(0, 0)) "00:00"
        else suggestion.stop.startTime.format(DateTimeFormatter.ISO_LOCAL_TIME).substring(0, 5))
  }
  val end =
      LocalDateTime.of(suggestion.stop.date, suggestion.stop.startTime)
          .plusMinutes(suggestion.stop.duration.toLong())
  var endTime by remember {
    mutableStateOf(
        if (suggestion.stop.duration == -1) "00:00"
        else end.toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME).substring(0, 5))
  }
  var endDate by remember {
    mutableStateOf(
        if (suggestion.stop.duration == -1) ""
        else end.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
  }

  var startDErr by remember { mutableStateOf(false) }
  var endDErr by remember { mutableStateOf(false) }
  var startTErr by remember { mutableStateOf(false) }
  var endTErr by remember { mutableStateOf(false) }
  var titleErr by remember { mutableStateOf(false) }
  var budgetErr by remember { mutableStateOf(false) }
  var descErr by remember { mutableStateOf(false) }

  var showDatePickerStart by remember { mutableStateOf(false) }
  var showDatePickerEnd by remember { mutableStateOf(false) }

  var showTimePickerStart by remember { mutableStateOf(false) }
  var showTimePickerEnd by remember { mutableStateOf(false) }

  val dateRegexPattern = """^\d{4}-\d{2}-\d{2}$"""
  val timeRegexPattern = """^\d{2}:\d{2}$"""

  Scaffold(
      topBar = {
        GoBackSuggestionTopBar(
            title =
                if (suggestion.suggestionId.isEmpty()) "Create a new suggestion"
                else "Edit the suggestion",
            onBack = onCancel)
      },
      content = { padding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(padding)
                    .padding(vertical = 12.dp) // Global vertical padding
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top) {
              Spacer(Modifier.height(8.dp))
              OutlinedTextField(
                  value = suggestionText,
                  onValueChange = { suggestionText = it },
                  label = { Text("Suggestion Title*") },
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(horizontal = 28.dp)
                          .testTag("inputSuggestionTitle"),
                  isError = titleErr,
                  singleLine = true)

              Spacer(Modifier.height(12.dp))

              OutlinedTextField(
                  value = description,
                  onValueChange = { description = it },
                  label = { Text("Suggestion Description*") },
                  modifier =
                      Modifier.fillMaxWidth()
                          .height(150.dp)
                          .padding(horizontal = 28.dp)
                          .testTag("inputSuggestionDescription")
                          .scrollable(rememberScrollState(), Orientation.Vertical),
                  isError = descErr,
                  singleLine = false,
                  placeholder = { Text("Describe the suggestion") })

              //                Spacer(Modifier.height(4.dp))
              Spacer(Modifier.height(12.dp))

              OutlinedTextField(
                  value = budget,
                  onValueChange = { budget = it },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  label = { Text("Budget") },
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(horizontal = 28.dp)
                          .testTag("inputSuggestionBudget"),
                  isError = budgetErr,
                  singleLine = true,
                  placeholder = { Text("Budget") })

              //                Spacer(Modifier.height(8.dp))
              Spacer(Modifier.height(12.dp))

              Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp)) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    placeholder = { Text("From*") },
                    modifier =
                        Modifier.weight(1f).testTag("inputSuggestionStartDate").clickable {
                          showDatePickerStart = true
                        },
                    isError = startDErr,
                    singleLine = true,
                    interactionSource = DateInteractionSource { showDatePickerStart = true })

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    placeholder = { Text("From time*") },
                    modifier = Modifier.weight(1f).testTag("inputSuggestionStartTime"),
                    isError = startTErr,
                    singleLine = true,
                    interactionSource = DateInteractionSource { showTimePickerStart = true })
              }

              //                Spacer(Modifier.height(8.dp))
              Spacer(Modifier.height(12.dp))

              Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp)) {
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    placeholder = { Text("To*") },
                    modifier = Modifier.weight(1f).testTag("inputSuggestionEndDate"),
                    isError = endDErr,
                    singleLine = true,
                    interactionSource = DateInteractionSource { showDatePickerEnd = true })

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    placeholder = { Text("To time*") },
                    modifier = Modifier.weight(1f).testTag("inputSuggestionEndTime"),
                    isError = endTErr,
                    singleLine = true,
                    interactionSource = DateInteractionSource { showTimePickerEnd = true })
              }

              if (showDatePickerStart) {
                MyDatePickerDialog(
                    onDateSelected = {
                      startDate =
                          if (isStringInFormat(it, """^\d{2}/\d{2}/\d{4}$""")) convertDateFormat(it)
                          else "To*"
                    },
                    onDismiss = { showDatePickerStart = false })
              }

              if (showDatePickerEnd) {
                MyDatePickerDialog(
                    onDateSelected = {
                      endDate =
                          if (isStringInFormat(it, """^\d{2}/\d{2}/\d{4}$""")) convertDateFormat(it)
                          else "To*"
                    },
                    onDismiss = { showDatePickerEnd = false })
              }

              if (showTimePickerStart) {
                MyTimePickerDialog(
                    onTimeSelected = { startTime = it },
                    onDismiss = { showTimePickerStart = false })
              }

              if (showTimePickerEnd) {
                MyTimePickerDialog(
                    onTimeSelected = { endTime = it }, onDismiss = { showTimePickerEnd = false })
              }

              if (suggestion.stop.address.isNotEmpty()) {
                //                    Spacer(Modifier.height(4.dp))
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp)) {
                  OutlinedTextField(
                      value = address,
                      onValueChange = { address = it },
                      label = { Text("Address") },
                      modifier =
                          Modifier.weight(6f)
                              .horizontalScroll(rememberScrollState())
                              .testTag("inputSuggestionAddress"),
                      isError = false,
                      singleLine = true,
                      placeholder = { Text("Address of the suggestion") },
                      enabled = false)
                }
              }

              //                Spacer(Modifier.height(4.dp))
              Spacer(Modifier.height(12.dp))

              OutlinedTextField(
                  value = website,
                  onValueChange = { website = it },
                  label = { Text("Website") },
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(horizontal = 28.dp)
                          .testTag("inputSuggestionWebsite")
                          .horizontalScroll(rememberScrollState()),
                  singleLine = true,
                  placeholder = { Text("Website") })

              //                Spacer(modifier = Modifier.height(224.dp))
              Spacer(modifier = Modifier.height(178.dp))

              Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
                ExtendedFloatingActionButton(
                    onClick = {
                      titleErr = suggestionText.trim().isEmpty()
                      budgetErr = budget.isNotEmpty() && !isConvertibleToDouble(budget)
                      descErr = description.trim().isEmpty()
                      startDErr = !isStringInFormat(startDate, dateRegexPattern)
                      startTErr = !isStringInFormat(startTime, timeRegexPattern)
                      endDErr = !isStringInFormat(endDate, dateRegexPattern)
                      endTErr = !isStringInFormat(endTime, timeRegexPattern)

                      if (!(titleErr ||
                          budgetErr ||
                          descErr ||
                          startDErr ||
                          startTErr ||
                          endTErr ||
                          endDErr)) {

                        val startDateObj =
                            LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE)
                        val startTimeObj =
                            LocalTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_TIME)

                        val endDateObj = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE)
                        val endTimeObj = LocalTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_TIME)

                        val startDateTime = LocalDateTime.of(startDateObj, startTimeObj)
                        val endDateTime = LocalDateTime.of(endDateObj, endTimeObj)

                        val duration = Duration.between(startDateTime, endDateTime)
                        val newSuggestion =
                            Suggestion(
                                suggestionId = "",
                                userId = "",
                                userName = SessionManager.getCurrentUser()!!.name,
                                text = "",
                                createdAt = LocalDate.now(),
                                createdAtTime = LocalTime.now(),
                                stop =
                                    Stop(
                                        stopId = suggestion.stop.stopId,
                                        title = suggestionText,
                                        address = address,
                                        date = startDateObj,
                                        startTime = startTimeObj,
                                        duration = duration.toMinutes().toInt(),
                                        budget = if (budget.isEmpty()) 0.0 else budget.toDouble(),
                                        description = description,
                                        geoCords = suggestion.stop.geoCords,
                                        website = website,
                                        imageUrl = ""))
                        if (suggestion.suggestionId.isNotEmpty()) {
                          if (viewModel.updateSuggestion(
                              tripId,
                              suggestion.copy(
                                  stop =
                                      newSuggestion.stop.copy(stopId = suggestion.stop.stopId)))) {
                            onSuccess()
                          } else {
                            onFailure()
                          }
                        } else {
                          if (viewModel.addSuggestion(tripId, newSuggestion)) {
                            onSuccess()
                          } else {
                            onFailure()
                          }
                        }
                      }
                    },
                    modifier =
                        Modifier.fillMaxWidth(0.5f)
                            .padding(10.dp)
                            .testTag("createSuggestionButton"),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    elevation =
                        FloatingActionButtonDefaults.elevation(
                            2.dp, 1.dp) // to have the shadow effect
                    ) {
                      Text(
                          text =
                              if (suggestion.suggestionId.isEmpty()) "Create Suggestion"
                              else "Edit Suggestion",
                          style =
                              TextStyle(
                                  color = MaterialTheme.colorScheme.onSecondaryContainer,
                                  fontWeight = FontWeight.SemiBold))
                    }
              }
            }
      })
}

/** needs to be discussed in meeting if shared with rest of project */
private fun isConvertibleToDouble(input: String): Boolean {
  return try {
    input.toDouble()
    true // Conversion successful
  } catch (e: NumberFormatException) {
    false // Conversion failed
  }
}

/** needs to be discussed in meeting if shared with rest of project */
private fun isStringInFormat(input: String, regexPattern: String): Boolean {
  val regex = Regex(regexPattern)
  return regex.matches(input)
}

fun convertDateFormat(inputDate: String): String {
  // Split the input string by "/"
  val parts = inputDate.split("/")

  // Rearrange the parts into "yyyy-mm-dd" format
  val year = parts[2]
  val month = parts[1]
  val day = parts[0]

  // Construct the new date format
  return "$year-$month-$day"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTimePickerDialog(onTimeSelected: (String) -> Unit, onDismiss: () -> Unit) {
  val timePickerState = rememberTimePickerState()
  val selectedTime = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)

  TimePickerDialog(
      onCancel = onDismiss,
      onConfirm = {
        onTimeSelected(selectedTime)
        onDismiss()
      }) {
        TimePicker(state = timePickerState)
      }
}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
  Dialog(
      onDismissRequest = onCancel,
      properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 6.dp,
        modifier =
            Modifier.width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface),
    ) {
      toggle()
      Column(
          modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                text = title,
                style = MaterialTheme.typography.labelMedium)
            content()
            Row(modifier = Modifier.height(40.dp).fillMaxWidth()) {
              Spacer(modifier = Modifier.weight(1f))
              TextButton(onClick = onCancel) { Text("Cancel") }
              TextButton(onClick = onConfirm) { Text("OK") }
            }
          }
    }
  }
}
