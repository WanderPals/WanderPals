package com.github.se.wanderpals.ui.screens.trip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.CreateSuggestionViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.DateInteractionSource
import com.github.se.wanderpals.ui.screens.MyDatePickerDialog
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers

/**
 * CreateSuggestion composable responsible for adding a suggestion to a trip
 *
 * @param tripId the id of the trip
 * @param viewModel a CreateSuggestionViewModel that needs to be initialized beforehand
 * @param desc default value for the description entry (can be empty)
 * @param addr default value for the address entry (can be empty)
 * @param website default value for the website entry (can be empty)
 * @param title default value for the title entry (can be empty)
 * @param budget default value for the budget entry (can be empty)
 * @param onSuccess additional code to execute after the successful creation of the suggestion
 * @param onFailure code to execute if the creation of the suggestion fails
 */
@Composable
fun CreateSuggestion(
    tripId: String,
    viewModel: CreateSuggestionViewModel,
    desc: String = "",
    addr: String = "",
    website: String = "",
    title: String = "",
    budget: Double = Double.NaN,
    onSuccess: () -> Unit = {},
    onFailure: () -> Unit = {}
) {
  var description by remember { mutableStateOf(desc) }
  var address by remember { mutableStateOf(addr) }
  var _website by remember { mutableStateOf(website) }
  var suggestionText by remember { mutableStateOf(title) }
  var _budget by remember { mutableStateOf(if (budget.isNaN()) "" else budget.toString()) }
  var startDate by remember { mutableStateOf("") }
  var startTime by remember { mutableStateOf("00:00") }
  var endTime by remember { mutableStateOf("00:00") }
  var endDate by remember { mutableStateOf("") }

  var start_d_err by remember { mutableStateOf(false) }
  var end_d_err by remember { mutableStateOf(false) }
  var start_t_err by remember { mutableStateOf(false) }
  var end_t_err by remember { mutableStateOf(false) }
  var title_err by remember { mutableStateOf(false) }
  var budget_err by remember { mutableStateOf(false) }
  var desc_err by remember { mutableStateOf(false) }
  var addr_err by remember { mutableStateOf(false) }

  var showDatePickerStart by remember { mutableStateOf(false) }
  var showDatePickerEnd by remember { mutableStateOf(false) }

  var showTimePickerStart by remember { mutableStateOf(false) }
  var showTimePickerEnd by remember { mutableStateOf(false) }

  val dateRegexPattern = """^\d{4}-\d{2}-\d{2}$"""
  val timeRegexPattern = """^\d{2}:\d{2}$"""
  //  val websiteRegexPattern =
  // """^(http|https)://[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}(?:/[a-zA-Z0-9.-]*)*$"""

  val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

  Surface(modifier = Modifier.padding(12.dp)) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top) {
          Row {
            OutlinedTextField(
                value = suggestionText,
                onValueChange = { suggestionText = it },
                label = { Text("Suggestion Title*") },
                modifier = Modifier.weight(3f).testTag("inputSuggestionTitle"),
                isError = title_err,
                singleLine = true)
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedTextField(
                value = _budget,
                onValueChange = { _budget = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Budget") },
                modifier = Modifier.weight(1.5f).testTag("inputSuggestionBudget"),
                isError = budget_err,
                singleLine = true,
                placeholder = { Text("Budget") })
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                placeholder = { Text("From*") },
                modifier =
                    Modifier.testTag("inputSuggestionStartDate").weight(1f).clickable {
                      showDatePickerStart = true
                    },
                isError = start_d_err,
                singleLine = true,
                interactionSource = DateInteractionSource { showDatePickerStart = true })

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
                value = startTime,
                onValueChange = { startTime = it },
                placeholder = { Text("From time*") },
                modifier = Modifier.weight(1f).testTag("inputSuggestionStartTime"),
                isError = start_t_err,
                singleLine = true,
                interactionSource = DateInteractionSource { showTimePickerStart = true })
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = endDate,
                onValueChange = { endDate = it },
                placeholder = { Text("To*") },
                modifier = Modifier.weight(1f).testTag("inputSuggestionEndDate"),
                isError = end_d_err,
                singleLine = true,
                interactionSource = DateInteractionSource { showDatePickerEnd = true })

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
                value = endTime,
                onValueChange = { endTime = it },
                placeholder = { Text("To time*") },
                modifier = Modifier.weight(1f).testTag("inputSuggestionEndTime"),
                isError = end_t_err,
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
              label = { Text("Suggestion Description*") },
              modifier =
                  Modifier.testTag("inputSuggestionDescription")
                      .fillMaxWidth()
                      .height(150.dp)
                      .padding(bottom = 12.dp)
                      .scrollable(rememberScrollState(), Orientation.Vertical),
              isError = desc_err,
              singleLine = false,
              placeholder = { Text("Describe the suggestion") })

          Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier =
                    Modifier.testTag("inputSuggestionAddress")
                        .horizontalScroll(state = rememberScrollState(0), enabled = true)
                        .weight(6f),
                isError = addr_err,
                singleLine = true,
                placeholder = { Text("Address of the suggestion") })

            // Spacer(modifier = Modifier.width(12.dp))
            /*
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

            */
          }

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
              value = _website,
              onValueChange = { _website = it },
              label = { Text("Website") },
              modifier =
                  Modifier.testTag("inputSuggestionWebsite")
                      .fillMaxWidth()
                      .horizontalScroll(state = rememberScrollState(0), enabled = true),
              singleLine = true,
              placeholder = { Text("Website") })

          Spacer(modifier = Modifier.height(12.dp))

          Button(
              modifier = Modifier.testTag("createSuggestionButton"),
              onClick = {
                title_err = suggestionText.isEmpty()
                budget_err = _budget.isNotEmpty() && !isConvertibleToDouble(_budget)
                desc_err = description.isEmpty()
                // addr_err = address.isEmpty()
                start_d_err = !isStringInFormat(startDate, dateRegexPattern)
                start_t_err = !isStringInFormat(startTime, timeRegexPattern)
                end_d_err = !isStringInFormat(endDate, dateRegexPattern)
                end_t_err = !isStringInFormat(endTime, timeRegexPattern)

                if (!(title_err ||
                    budget_err ||
                    desc_err ||
                    start_d_err ||
                    start_t_err ||
                    end_t_err ||
                    end_d_err)) {

                  // Parse start date and time
                  val startDateObj = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE)
                  val startTimeObj = LocalTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_TIME)

                  // Parse end date and time
                  val endDateObj = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE)
                  val endTimeObj = LocalTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_TIME)

                  // Combine start date and time
                  val startDateTime = LocalDateTime.of(startDateObj, startTimeObj)

                  // Combine end date and time
                  val endDateTime = LocalDateTime.of(endDateObj, endTimeObj)

                  // Calculate duration in minutes
                  val duration = Duration.between(startDateTime, endDateTime)
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
                                  title = suggestionText,
                                  address = address,
                                  date = startDateObj,
                                  startTime = startTimeObj,
                                  duration = duration.toMinutes().toInt(),
                                  budget = if (_budget.isEmpty()) 0.0 else _budget.toDouble(),
                                  description = description,
                                  geoCords = GeoCords(0.0, 0.0),
                                  website = _website,
                                  imageUrl = ""))
                  // Pass the created suggestion to the callback function
                  if (viewModel.addSuggestion(tripId, suggestion)) onSuccess() else onFailure()
                }
              }) {
                Text("Create Suggestion", fontSize = 24.sp)
              }
        }
  }
}

/**
 * || DO NOT USE FOR NOW || WIP || Dialog containing a CreateSuggestion window
 *
 * @param showDialog Boolean to specify when to show the dialog (should be turned off in the
 *   onDismiss function)
 * @param onDismiss Unit function without parameters that is executed when the dialog is requested
 *   to dismiss
 */
/*
@Composable
fun CreateSuggestionDialog(showDialog: Boolean, onDismiss: () -> Unit) {
  if (showDialog) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss) {
          Surface(modifier = Modifier.padding(top = 12.dp).fillMaxSize()) {
            CreateSuggestion(
                "aaaa", CreateSuggestionViewModel(TripsRepository("aaaa", Dispatchers.IO)))
          }
          Box(contentAlignment = Alignment.TopEnd, modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "dismiss",
                modifier = Modifier.clickable { onDismiss() })
          }
        }
  }
}

 */

/**
 * CreateSuggestion composable responsible for adding a suggestion to a trip
 *
 * @param tripId the id of the trip
 * @param viewModel a CreateSuggestionViewModel that needs to be initialized beforehand
 * @param nav NavigationActions to navigate back to Suggestion the suggestion is successfully
 *   created
 * @param desc default value for the description entry (can be empty)
 * @param addr default value for the address entry (can be empty)
 * @param website default value for the website entry (can be empty)
 * @param title default value for the title entry (can be empty)
 * @param budget default value for the budget entry (can be empty)
 * @param onSuccess additional code to execute after the successful creation of the suggestion (can
 *   be empty)
 * @param onFailure code to execute if the creation of the suggestion fails (can be empty)
 */
@Composable
fun CreateSuggestionScreen(
    tripId: String,
    viewModel: CreateSuggestionViewModel,
    nav: NavigationActions,
    desc: String = "",
    addr: String = "",
    website: String = "",
    title: String = "",
    budget: Double = Double.NaN,
    onSuccess: () -> Unit = {},
    onFailure: () -> Unit = {}
) {
  CreateSuggestion(
      tripId = tripId,
      viewModel = viewModel,
      desc,
      addr,
      website,
      title,
      budget,
      onSuccess = {
        nav.navigateTo(Route.SUGGESTION) // could be change to only execute onSuccess()
        onSuccess()
      },
      onFailure = { onFailure() })
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

/** for quick testing purposes */
@Preview(showBackground = true)
@Composable
fun CreateSuggestionPreview() {
  CreateSuggestion(
      "aaaa",
      CreateSuggestionViewModel(TripsRepository("aaaa", Dispatchers.IO)),
      title = "aiaia",
      budget = 0.0)
}

/** for quick testing purposes */
/*
@Preview(showBackground = true)
@Composable
fun CreateSuggestionDialogPreview() {
  CreateSuggestionDialog(true, {})
}
 */

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

// needs to be moved in another file, works for now
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
