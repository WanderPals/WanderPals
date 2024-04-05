package com.github.se.wanderpals.ui.screens.trip

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSuggestion() {
  var description by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }
  var website by remember { mutableStateOf("") }
  var suggestionText by remember { mutableStateOf("") }
  var budget by remember { mutableStateOf("") }
  var errorText by remember { mutableStateOf("") }
  var startDate by remember { mutableStateOf("") }
  var startTime by remember { mutableStateOf("") }
  var endTime by remember { mutableStateOf("") }
  var endDate by remember { mutableStateOf("") }

  var showDatePickerStart by remember { mutableStateOf(false) }
  var showDatePickerEnd by remember { mutableStateOf(false) }

  var showTimePickerStart by remember { mutableStateOf(false) }
  var showTimePickerEnd by remember { mutableStateOf(false) }

  Surface(modifier = Modifier.padding(16.dp)) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top) {
          Row {
            OutlinedTextField(
                value = suggestionText,
                onValueChange = { suggestionText = it },
                label = { Text("Suggestion Title") },
                modifier = Modifier
                    .weight(3f)
                    .testTag("inputSuggestionTitle"),
                singleLine = true)
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                value = budget,
                onValueChange = { budget = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Budget") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("inputSuggestionBudget"),
                isError = errorText.isNotEmpty(),
                singleLine = true,
                placeholder = { Text("Budget") })
          }

          Spacer(modifier = Modifier.height(16.dp))

          Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                placeholder = { Text("From") },
                modifier =
                Modifier
                    .testTag("inputSuggestionStartDate")
                    .weight(1f)
                    .clickable {
                        showDatePickerStart = true
                    },
                isError = errorText.isNotEmpty(),
                singleLine = true,
                interactionSource = DateInteractionSource { showDatePickerStart = true })

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedTextField(
                value = endTime,
                onValueChange = { endTime = it },
                placeholder = { Text("00:00") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("inputSuggestionEndDate"),
                isError = errorText.isNotEmpty(),
                singleLine = true,
                interactionSource = DateInteractionSource { showTimePickerEnd = true })
          }

          Spacer(modifier = Modifier.height(16.dp))

          Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = endDate,
                onValueChange = { endDate = it },
                placeholder = { Text("To") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("inputSuggestionEndDate"),
                isError = errorText.isNotEmpty(),
                singleLine = true,
                interactionSource = DateInteractionSource { showDatePickerEnd = true })

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedTextField(
                value = startTime,
                onValueChange = { startTime = it },
                placeholder = { Text("00:00") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("inputSuggestionEndDate"),
                isError = errorText.isNotEmpty(),
                singleLine = true,
                interactionSource = DateInteractionSource { showTimePickerStart = true })
          }
          if (showDatePickerStart) {
            MyDatePickerDialog(
                onDateSelected = { startDate = it }, onDismiss = { showDatePickerStart = false })
          }

          if (showDatePickerEnd) {
            MyDatePickerDialog(
                onDateSelected = { endDate = it }, onDismiss = { showDatePickerEnd = false })
          }

          if (showTimePickerStart) {
            MyTimePickerDialog(
                onTimeSelected = { startTime = it }, onDismiss = { showTimePickerStart = false })
          }

          if (showTimePickerEnd) {
            MyTimePickerDialog(
                onTimeSelected = { endTime = it }, onDismiss = { showTimePickerEnd = false })
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
              value = description,
              onValueChange = { description = it },
              label = { Text("Suggestion Description") },
              modifier =
              Modifier
                  .testTag("inputSuggestionDescription")
                  .fillMaxWidth()
                  .height(200.dp)
                  .padding(bottom = 16.dp),
              isError = errorText.isNotEmpty(),
              singleLine = false,
              maxLines = 5,
              placeholder = { Text("Describe the trip") })

          Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier =
                Modifier
                    .testTag("inputSuggestionDescription")
                    .horizontalScroll(state = rememberScrollState(0), enabled = true)
                    .weight(6f),
                isError = errorText.isNotEmpty(),
                singleLine = true,
                placeholder = { Text("Address of the suggestion") })

            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "pick on map",
                Modifier
                    .weight(1f)
                    .padding(top = 6.dp)
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                    .size(48.dp)
                    .align(Alignment.CenterVertically)
                    .clickable(onClick = {}) // go to map to get data
                )
          }

          Spacer(modifier = Modifier.height(16.dp))

          OutlinedTextField(
              value = website,
              onValueChange = { website = it },
              label = { Text("Website") },
              modifier =
              Modifier
                  .testTag("inputSuggestionWebsite")
                  .fillMaxWidth()
                  .horizontalScroll(state = rememberScrollState(0), enabled = true),
              isError = errorText.isNotEmpty(),
              singleLine = true,
              placeholder = { Text("Website") })

          Spacer(modifier = Modifier.height(16.dp))

          Button(onClick = { /* GET AN IMAGE FROM GALLERY */}) { Text("Select an Image") }

          Spacer(modifier = Modifier.height(16.dp))

          Button(
              onClick = {
                if (verifyArgument()) {
                  val dateFormatter = DateTimeFormatter.ofPattern("dd/mm/yyyy")
                  val timeFormatter = DateTimeFormatter.ofPattern("hh:mm")

                  // Parse start date and time
                  val startDateObj = LocalDate.parse(startDate, dateFormatter)
                  val startTimeObj = LocalTime.parse(startTime, timeFormatter)

                  // Parse end date and time
                  val endDateObj = LocalDate.parse(endDate, dateFormatter)
                  val endTimeObj = LocalTime.parse(endTime, timeFormatter)

                  // Combine start date and time
                  val startDateTime = LocalDateTime.of(startDateObj, startTimeObj)

                  // Combine end date and time
                  val endDateTime = LocalDateTime.of(endDateObj, endTimeObj)

                  // Calculate duration in minutes
                  val duration = Duration.between(startDateTime, endDateTime)

                  // Create the Suggestion object
                  val suggestion =
                      Suggestion(
                          suggestionId = "", // modified by database
                          userId = "", // modified by database
                          userName = "tempUsername", // modified by database
                          text = "", // Empty for now
                          createdAt = LocalDate.now(), // Should add time
                          stop =
                              Stop(
                                  "", // modified by database
                                  suggestionText,
                                  address,
                                  startDateObj,
                                  startTimeObj,
                                  duration.toMinutes().toInt(),
                                  budget.toDouble(),
                                  description,
                                  GeoCords(
                                      0.0,
                                      0.0), // from address i guess, chatGPT tell me to use Google
                                            // Maps
                                  website,
                                  ""))
                  // Pass the created suggestion to the callback function
                  // addSuggestionToTrip(suggestion)
                }
              }) {
                Text("Create Suggestion", fontSize = 24.sp)
              }
        }
  }
}

@Composable
fun CreateSuggestionDialog(showDialog:Boolean, onDismiss: () -> Unit) {
    if(showDialog) {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = {}) {
            Surface(modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxSize()) { CreateSuggestion() }
            Box(contentAlignment = Alignment.TopEnd, modifier = Modifier.fillMaxSize()) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "dismiss",
                    modifier = Modifier.clickable{onDismiss()})
            }
        }
    }
}

fun verifyArgument(): Boolean {
  return false
}

@Preview(showBackground = true)
@Composable
fun CreateSuggestionScreenPreview() {
  CreateSuggestion()
}

@Preview(showBackground = true)
@Composable
fun CreateSuggestionDialogPreview() {
  CreateSuggestionDialog(true, {})
}

/*
@Composable
fun AutocompleteTextField(
    placesClient: PlacesClient,
    onPlaceSelected: (Place) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }

    TextField(
        value = searchText,
        onValueChange = { searchText = it },
        label = { Text("Enter a location") },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            // Perform Autocomplete API call
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(searchText)
                .build()
            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    predictions = response.autocompletePredictions
                }
                .addOnFailureListener { exception ->
                    // Handle error
                }
        })
    )

    // Display autocomplete suggestions
    Column {
        predictions.forEach { prediction ->
            Text(
                text = prediction.getFullText(null).toString(),
                modifier = Modifier.clickable {
                    // Retrieve details of the selected place
                    val placeRequest = FetchPlaceRequest.builder(prediction.placeId)
                        .build()
                    placesClient.fetchPlace(placeRequest)
                        .addOnSuccessListener { response ->
                            val place = response.place
                            onPlaceSelected(place)
                        }
                        .addOnFailureListener { exception ->
                            // Handle error
                        }
                }
            )
        }
    }
}

 */

// To be removed, currently only for testing purpose... might want to reuse the one in CreateTrip
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

@SuppressLint("SimpleDateFormat")
private fun convertMillisToDate(millis: Long): String {
  val formatter = SimpleDateFormat("dd/MM/yyyy")
  return formatter.format(Date(millis))
}

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
        Modifier
            .width(IntrinsicSize.Min)
            .height(IntrinsicSize.Min)
            .background(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface
            ),
    ) {
      toggle()
      Column(
          modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                text = title,
                style = MaterialTheme.typography.labelMedium)
            content()
            Row(modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()) {
              Spacer(modifier = Modifier.weight(1f))
              TextButton(onClick = onCancel) { Text("Cancel") }
              TextButton(onClick = onConfirm) { Text("OK") }
            }
          }
    }
  }
}
