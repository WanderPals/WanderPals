package com.github.se.wanderpals.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.R
import com.github.se.wanderpals.model.viewmodel.DashboardViewModel

/**
 * Composable function for displaying the document widget in the dashboard screen.
 *
 * @param onClick Callback function for handling click on the document widget.
 * @param viewModel The view model for the dashboard screen.
 */
@Composable
fun DashboardDocumentWidget(onClick: () -> Unit, viewModel: DashboardViewModel) {
  val lastAddedTripDocument by viewModel.lastSharedDocument.collectAsState()
  val lastAddedPrivateDocument by viewModel.lastPrivateDocument.collectAsState()

  val titleStyle =
      TextStyle( // This sets the style for the title in each text box
          color = MaterialTheme.colorScheme.primary,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Start)

  ElevatedCard(
      modifier =
          Modifier.padding(horizontal = 16.dp)
              .fillMaxWidth()
              .clickable(onClick = onClick)
              .testTag("documentsCard"),
      colors =
          CardDefaults.cardColors(
              containerColor =
                  MaterialTheme.colorScheme
                      .surfaceVariant // This sets the background color of the Card
              ),
      shape = RoundedCornerShape(6.dp),
      elevation = CardDefaults.cardElevation(10.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
          Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Start,
              modifier =
                  Modifier.clip(RoundedCornerShape(4.dp))
                      .background(MaterialTheme.colorScheme.primaryContainer)
                      .padding(horizontal = 8.dp, vertical = 4.dp)) {
                Icon(
                    painterResource(id = R.drawable.documents),
                    contentDescription = "Documents Icon",
                    modifier = Modifier.size(16.dp).testTag("documentsIcon"),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(
                    text = "Documents",
                    modifier = Modifier.testTag("documentsTitle"),
                    style =
                        TextStyle(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold))
              }

          Spacer(modifier = Modifier.height(8.dp))

          Box(
              modifier =
                  Modifier.clip(RoundedCornerShape(4.dp))
                      .background(MaterialTheme.colorScheme.surface)
                      .fillMaxWidth()
                      .padding(8.dp)) {
                Column {
                  Text(
                      text = "Last private document:",
                      style = titleStyle,
                      modifier = Modifier.testTag("privateDocTitle")
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  DocumentTextBox(
                      text = lastAddedPrivateDocument.ifEmpty { "No private documents." }, "privateDoc")
                }
              }

          Spacer(modifier = Modifier.height(12.dp))

          Box(
              modifier =
                  Modifier.clip(RoundedCornerShape(4.dp))
                      .background(MaterialTheme.colorScheme.surface)
                      .fillMaxWidth()
                      .padding(8.dp)) {
                Column {
                  Text(
                      text = "Last shared document:",
                      style = titleStyle,
                        modifier = Modifier.testTag("sharedDocTitle")
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  DocumentTextBox(text = lastAddedTripDocument.ifEmpty { "No shared documents." }, "sharedDoc")
                }
              }
        }
      }
}

@Composable
fun DocumentTextBox(text: String, testTag: String) {
  val subtitleStyle =
      TextStyle( // This sets the style for the filename in each text box
          color = MaterialTheme.colorScheme.tertiary,
          fontSize = 12.sp,
          fontWeight = FontWeight.Normal,
          textAlign = TextAlign.Start)

  Text(
      text = text,
      modifier = Modifier.testTag(testTag),
      style = subtitleStyle,
      overflow = TextOverflow.Ellipsis,
      maxLines = 3,
  )
}
