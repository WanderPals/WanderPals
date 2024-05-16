package com.github.se.wanderpals.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.DashboardViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers

/**
 * Composable function to display the stop widget in the dashboard screen
 * @param viewModel DashboardViewModel to get the stops from
 * @param onClick Function to execute when the widget is clicked
 */
@Composable
fun DashboardStopWidget(viewModel: DashboardViewModel, onClick: () -> Unit = {}) {
  val stops by viewModel.stops.collectAsState()
  val sortedStops =
      stops
          .sortedBy { LocalDateTime.of(it.date, it.startTime) }
          .filter {
            LocalDateTime.of(it.date, it.startTime)
                .plusMinutes(it.duration.toLong())
                .isAfter(LocalDateTime.now())
          }

  ElevatedCard(
      modifier =
          Modifier.padding(horizontal = 16.dp)
              .fillMaxWidth()
              .clickable(onClick = onClick)
              .testTag("stopCard"),
      colors =
          CardDefaults.cardColors(
              containerColor =
                  MaterialTheme.colorScheme
                      .surfaceVariant // This sets the background color of the Card
              ),
      shape = RoundedCornerShape(6.dp),
      elevation = CardDefaults.cardElevation(10.dp)) {
        // Finance Widget
        Row(
            modifier = Modifier.height(IntrinsicSize.Max).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
              // Finance Details, Left part of the widget
              Column(
                  modifier =
                      Modifier.padding(start = 8.dp, top = 8.dp, end = 4.dp, bottom = 8.dp)
                          .fillMaxWidth()) {
                    // Top part of the texts
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()) {
                          Row(
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.Start,
                              modifier =
                                  Modifier.clip(RoundedCornerShape(4.dp))
                                      .background(MaterialTheme.colorScheme.primaryContainer)
                                      .padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = "Stop Icon",
                                    modifier = Modifier.size(16.dp).testTag("stopIcon"),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text(
                                    text = "Upcoming Stops",
                                    modifier = Modifier.testTag("stopTitle"),
                                    style =
                                        TextStyle(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Bold))
                              }

                          Spacer(modifier = Modifier.padding(4.dp))

                          Text(
                              text =
                                  "Total: ${sortedStops.size} stop" +
                                      if (sortedStops.size > 1) "s" else "",
                              modifier =
                                  Modifier.testTag("totalStops")
                                      .clip(RoundedCornerShape(4.dp))
                                      .background(MaterialTheme.colorScheme.surface)
                                      .padding(horizontal = 8.dp, vertical = 4.dp),
                              style =
                                  TextStyle(
                                      color = MaterialTheme.colorScheme.primary,
                                      fontWeight = FontWeight.Bold))
                        }

                    Spacer(modifier = Modifier.padding(4.dp))

                    // Latest expenses
                    Box(
                        modifier =
                            Modifier.clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .fillMaxWidth()) {
                          if (sortedStops.isEmpty()) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier =
                                    Modifier.padding(top = 16.dp, bottom = 40.dp).fillMaxSize()) {
                                  Text(
                                      text = "No stops yet.",
                                      modifier = Modifier.testTag("noStops"),
                                      style = TextStyle(color = MaterialTheme.colorScheme.primary),
                                  )
                                }
                          } else {
                            Column {
                              StopItem(stop = sortedStops[0])
                              HorizontalDivider(
                                  color = MaterialTheme.colorScheme.surfaceVariant,
                                  thickness = 1.dp,
                                  modifier = Modifier.padding(horizontal = 8.dp))
                              if (sortedStops.size > 1) {
                                StopItem(stop = sortedStops[1])
                              } else {
                                Box(modifier = Modifier.fillMaxSize())
                              }
                            }
                          }
                        }
                  }
            }
      }
}

/**
 * Composable function to display a single stop item in the dashboard widget
 * @param stop Stop object to display
 */
@Composable
fun StopItem(stop: Stop) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth().testTag("stopItem" + stop.stopId)) {
        Column(modifier = Modifier.padding(8.dp).weight(1f).fillMaxWidth()) {
          Text(
              text =
                  if (stop.title.length > 20) stop.title.subSequence(0, 18).toString() + "..."
                  else stop.title,
              style =
                  TextStyle(
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary,
                      fontSize = 15.sp),
              modifier = Modifier.testTag("stopTitle" + stop.stopId))
          Spacer(modifier = Modifier.height(4.dp))
          Text(
              text =
                  LocalDateTime.of(stop.date, stop.startTime)
                      .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
              style = TextStyle(color = MaterialTheme.colorScheme.tertiary, fontSize = 10.sp),
              modifier = Modifier.testTag("stopStart" + stop.stopId))
        }
        Column(modifier = Modifier.padding(8.dp).weight(1f).fillMaxWidth()) {
          Text(
              text =
                  if (stop.address.isEmpty()) ""
                  else if (stop.address.length > 40)
                      stop.address.subSequence(0, 38).toString() + "..."
                  else stop.address,
              style =
                  TextStyle(
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary,
                      fontSize = 15.sp,
                      textAlign = TextAlign.End),
              modifier = Modifier.testTag("stopAddress" + stop.stopId).fillMaxWidth())

          Spacer(modifier = Modifier.height(4.dp))

          Text(
              text =
                  LocalDateTime.of(stop.date, stop.startTime)
                      .plusMinutes(stop.duration.toLong())
                      .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
              style =
                  TextStyle(
                      color = MaterialTheme.colorScheme.tertiary,
                      fontSize = 10.sp,
                      textAlign = TextAlign.End),
              modifier = Modifier.testTag("stopEnd" + stop.stopId).fillMaxWidth())
        }
      }
}
