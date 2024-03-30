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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.data.Stop
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSuggestionScreen(
    onCreateSuggestionClick: (Suggestion) -> Unit
) {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = "Create a Suggestion",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                ) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        content = { padding ->
            Surface(modifier = Modifier.padding(16.dp)) {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {

                    Row {
                        OutlinedTextField(
                            value = suggestionText,
                            onValueChange = { suggestionText = it },
                            label = { Text("Suggestion Title") },
                            modifier = Modifier
                                .weight(3f)
                                .testTag("inputSuggestionTitle"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        OutlinedTextField(
                            value = budget,
                            onValueChange = { budget = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text("Budget") },
                            modifier =
                            Modifier
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
                                .clickable { showDatePickerStart = true },
                            isError = errorText.isNotEmpty(),
                            singleLine = true,
                            interactionSource = DateInteractionSource {
                                showDatePickerStart = true
                            })

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
                            interactionSource = DateInteractionSource {
                                showTimePickerStart = true
                            })

                    }
                    if (showDatePickerStart) {
                        MyDatePickerDialog(
                            onDateSelected = { startDate = it },
                            onDismiss = { showDatePickerStart = false })
                    }

                    if (showDatePickerEnd) {
                        MyDatePickerDialog(
                            onDateSelected = { endDate = it },
                            onDismiss = { showDatePickerEnd = false })
                    }

                    if (showTimePickerStart) {
                        MyTimePickerDialog(
                            onTimeSelected = { startTime = it },
                            onDismiss = { showTimePickerStart = false })
                    }

                    if (showTimePickerEnd) {
                        MyTimePickerDialog(
                            onTimeSelected = { endTime = it },
                            onDismiss = { showTimePickerEnd = false })
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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Address") },
                            modifier =
                            Modifier
                                .testTag("inputSuggestionDescription")
                                .horizontalScroll(
                                    state = rememberScrollState(0),
                                    enabled = true
                                )
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
                            .horizontalScroll(
                                state = rememberScrollState(0),
                                enabled = true
                            ),
                        isError = errorText.isNotEmpty(),
                        singleLine = true,
                        placeholder = { Text("Website") })

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = { /* GET AN IMAGE FROM GALLERY */ }) {
                        Text("Select an Image")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            verifyArgument()
                            parseArgument()
                            // Create the Suggestion object
                            val suggestion = Suggestion(
                                suggestionId = "", // Generate or get suggestion ID (surely from the stop title)
                                userId = "", // Get user ID
                                userName = "", // Get username
                                text = "", // Empty for now
                                createdAt = LocalDate.now(), // Should add time
                                stop = Stop(
                                    "", // get from title
                                    suggestionText,
                                    address,
                                    LocalDate.of(0,0,0), // need to parse
                                    LocalTime.of(0,0), // need to parse
                                    0, // need to calculate
                                    budget.toDouble(),
                                    description,
                                    GeoCords(0.0, 0.0), // from address i guess, chatGPT tell me to use Google Maps
                                    website,
                                    "" // and i don't know
                                )
                            )

                            // Pass the created suggestion to the callback function
                            onCreateSuggestionClick(suggestion)
                        }
                    )
                    {
                        Text(
                            "Create Suggestion",
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }


    )

}

fun parseArgument() {
    TODO("Not yet implemented")
}

fun verifyArgument() {
    TODO("Not yet implemented")
}

@Preview(showBackground = true)
@Composable
fun CreateSuggestionScreenPreview() {
    CreateSuggestionScreen(onCreateSuggestionClick = {})
}


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
    val selectedTime = String.format("%02d:%02d", timePickerState.hour,timePickerState.minute)

    TimePickerDialog(
        onCancel = onDismiss,
        onConfirm = {
            onTimeSelected(selectedTime)
            onDismiss()
        })
    {
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
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            toggle()
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel
                    ) { Text("Cancel") }
                    TextButton(
                        onClick = onConfirm
                    ) { Text("OK") }
                }
            }
        }
    }
}