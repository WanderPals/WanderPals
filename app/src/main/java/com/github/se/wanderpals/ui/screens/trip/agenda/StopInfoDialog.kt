package com.github.se.wanderpals.ui.screens.trip.agenda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.github.se.wanderpals.model.data.Stop
import java.time.format.DateTimeFormatter

/**
 * Composable function to display a dialog containing information about a stop in a trip agenda.
 *
 * @param stop The [Stop] object containing information about the stop.
 * @param closeDialogueAction Callback function to be invoked when the dialog is dismissed.
 */
@Composable
fun StopInfoDialog(stop: Stop, closeDialogueAction: () -> Unit) {
  Dialog(onDismissRequest = { closeDialogueAction() }) {
    Surface(
        modifier =
            Modifier.fillMaxSize().padding(top = 30.dp, bottom = 30.dp).testTag("activityDialog"),
        color = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(16.dp)) {
          Column(
              modifier = Modifier.fillMaxSize().padding(16.dp),
              verticalArrangement = Arrangement.SpaceEvenly) {

                // Title
                Text(
                    text = stop.title,
                    style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally).testTag("titleText"))
                Spacer(modifier = Modifier.height(15.dp))

                // Description
                OutlinedTextField(
                    value = stop.description,
                    onValueChange = {},
                    modifier =
                        Modifier.fillMaxWidth().height(225.dp).testTag("activityDescription"),
                    readOnly = true)
                Spacer(modifier = Modifier.height(15.dp))

                // Date
                Text(
                    text = "Date",
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.testTag("titleDate"))

                Text(
                    text = stop.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    style = TextStyle(fontSize = 16.sp),
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier.align(Alignment.Start)
                            .padding(bottom = 20.dp)
                            .testTag("activityDate"))

                // Schedule
                Text(
                    text = "Schedule",
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.testTag("titleSchedule"))

                Text(
                    text =
                        "${stop.startTime} - ${stop.startTime.plusMinutes(stop.duration.toLong())}",
                    style = TextStyle(fontSize = 16.sp),
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier.align(Alignment.Start)
                            .padding(bottom = 20.dp)
                            .testTag("activitySchedule"))

                // Address
                Text(
                    text = "Address",
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.testTag("titleAddress"))

                Text(
                    text = stop.address.ifEmpty { "No address provided" },
                    style = TextStyle(fontSize = 16.sp),
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier.align(Alignment.Start)
                            .padding(bottom = 20.dp)
                            .testTag("activityAddress"))

                // Budget
                Text(
                    text = "Budget",
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.testTag("titleBudget"))

                Text(
                    text = "${stop.budget}",
                    style = TextStyle(fontSize = 16.sp),
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier.align(Alignment.Start)
                            .padding(bottom = 20.dp)
                            .testTag("activityBudget"))
              }
        }
  }
}
