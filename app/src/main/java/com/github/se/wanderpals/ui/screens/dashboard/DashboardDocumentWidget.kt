package com.github.se.wanderpals.ui.screens.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.viewmodel.DashboardViewModel
import com.github.se.wanderpals.ui.theme.onPrimaryContainerLight
import com.github.se.wanderpals.ui.theme.surfaceVariantLight
import com.github.se.wanderpals.ui.theme.tertiaryLight

@Composable
fun DashboardDocumentWidget(onClick: () -> Unit, viewModel: DashboardViewModel) {
  val lastAddedTripDocument by viewModel.lastSharedDocument.collectAsState()
  val lastAddedPrivateDocument by viewModel.lastPrivateDocument.collectAsState()

  Card(
      modifier =
          Modifier.padding(16.dp)
              .clip(RoundedCornerShape(20))
              .clickable(onClick = onClick)
              .size(150.dp)
              .testTag("suggestionCard"),
      colors =
          CardDefaults.cardColors(
              containerColor = surfaceVariantLight // This sets the background color of the Card
              ),
      shape = RoundedCornerShape(20)) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
          Text(
              text = "Documents",
              style =
                  TextStyle(
                      color = onPrimaryContainerLight,
                      fontSize = 20.sp,
                      fontWeight = FontWeight.Bold))

          Spacer(modifier = Modifier.height(20.dp))
          Text(
              textAlign = TextAlign.Center,
              text = "Last Private:",
              style =
                  TextStyle(
                      color = tertiaryLight, fontSize = 12.sp, fontWeight = FontWeight.Normal))
          Spacer(modifier = Modifier.height(5.dp))
          Text(
              textAlign = TextAlign.Center,
              text = lastAddedPrivateDocument,
              style =
                  TextStyle(color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Normal))
          Spacer(modifier = Modifier.height(5.dp))
          Text(
              textAlign = TextAlign.Center,
              text = "Last Shared:",
              style =
                  TextStyle(
                      color = tertiaryLight, fontSize = 12.sp, fontWeight = FontWeight.Normal))

          Spacer(modifier = Modifier.height(5.dp))
          Text(
              textAlign = TextAlign.Center,
              text = lastAddedTripDocument,
              style =
                  TextStyle(color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Normal))
        }
      }
}
