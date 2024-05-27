package com.github.se.wanderpals.ui.screens.trip.stops

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
        shape = RoundedCornerShape(10.dp),
        modifier =
            Modifier.height(470.dp) // This will make the height dynamic
                .padding(horizontal = 13.dp)
                .fillMaxWidth() // Ensure the dialog utilizes the available width todo
        ) {
          Scaffold(
              modifier = Modifier.testTag("activityDialog"),
              containerColor =
                  MaterialTheme.colorScheme.surface, // Set the background color of the Scaffold
              topBar = {
                Column(
                    modifier =
                        Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary)) {
                      // Title
                      Text(
                          text = stop.title,
                          color = MaterialTheme.colorScheme.onPrimary,
                          style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                          textAlign = TextAlign.Center,
                          modifier =
                              Modifier.fillMaxWidth()
                                  .padding(vertical = 24.dp)
                                  .testTag("titleText"))
                    }
              },
              content = { padding ->
                Column(
                    modifier =
                        Modifier.fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(padding)
                            .padding(16.dp), // Apply padding to the content
                    verticalArrangement = Arrangement.Center) {
                      Row(
                          modifier = Modifier.fillMaxWidth().padding(top = 7.dp, bottom = 2.dp),
                      ) {
                        // Date
                        Text(
                            text =
                                stop.date.format(DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy")),
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight(500)),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Start,
                            lineHeight = 20.sp,
                            modifier =
                                Modifier // .weight(1f)
                                    .testTag("activityDate"))

                        Spacer(Modifier.weight(1f))

                        // Time
                        Text(
                            text =
                                "${stop.startTime} - ${stop.startTime.plusMinutes(stop.duration.toLong())}",
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight(500)),
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Right,
                            lineHeight = 20.sp,
                            modifier =
                                Modifier // .weight(1f)
                                    .testTag("activitySchedule"))
                      }

                      // Address
                      Text(
                          text = stop.address.ifEmpty { "No address provided" },
                          style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight(500)),
                          color = MaterialTheme.colorScheme.secondary,
                          textAlign = TextAlign.Start,
                          lineHeight = 20.sp,
                          modifier = Modifier.align(Alignment.Start).testTag("activityAddress"))

                      Spacer(Modifier.height(2.dp))

                      // Budget
                      Text(
                          text = "${stop.budget}",
                          style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight(500)),
                          textAlign = TextAlign.Start,
                          modifier = Modifier.align(Alignment.Start).testTag("activityBudget"))

                      Spacer(Modifier.height(14.dp)) // Space between Budget and Description

                      // Description
                      OutlinedTextField(
                          value = stop.description,
                          onValueChange = {},
                          textStyle =
                              TextStyle(
                                  fontSize = 16.sp,
                                  fontWeight = FontWeight(500),
                                  color = MaterialTheme.colorScheme.secondary),
                          readOnly = true,
                          modifier =
                              Modifier.fillMaxWidth()
                                  .height(300.dp)
                                  .background(MaterialTheme.colorScheme.background)
                                  .testTag("activityDescription"),
                      )
                      Spacer(
                          Modifier.height(
                              16.dp)) // Space between Description and the end of the dialog
                }
              })
        }
  }
}
