package com.github.se.wanderpals.ui.screens.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.theme.onPrimaryContainerLight
import com.github.se.wanderpals.ui.theme.primaryContainerLight

/**
 * Composable function that represents the bottom bar for the overview screen. Provides options for
 * joining a trip and creating a new trip.
 *
 * @param onCreateTripClick Callback function triggered when the "Create a new trip" button is
 *   clicked.
 * @param onLinkClick Callback function triggered when the "Join a trip" button is clicked.
 */
@Composable
fun OverviewBottomBar(onCreateTripClick: () -> Unit, onLinkClick: () -> Unit) {
  Column(modifier = Modifier.padding(bottom = 25.dp)) {

    // Button to join a trip
    Box(modifier = Modifier.fillMaxWidth()) {
      Button(
          enabled = SessionManager.getIsNetworkAvailable(),
          onClick = { onLinkClick() },
          modifier =
              Modifier.width(360.dp)
                  .height(70.dp)
                  .padding(bottom = 20.dp)
                  .align(Alignment.TopCenter)
                  .testTag("joinTripButton"),
          colors = ButtonDefaults.buttonColors(containerColor = primaryContainerLight)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                  imageVector = Icons.Default.Share,
                  contentDescription = Icons.Default.Share.name,
                  modifier = Modifier.width(18.dp).height(20.dp),
                  tint = onPrimaryContainerLight)
              Text(
                  text = "Join a Trip",
                  style =
                      TextStyle(
                          fontSize = 20.sp,
                          lineHeight = 20.sp,
                          fontWeight = FontWeight(500),
                          color = onPrimaryContainerLight,
                          textAlign = TextAlign.Center,
                          letterSpacing = 0.1.sp,
                      ))
            }
          }
    }

    // Button to create a new trip
    Box(modifier = Modifier.fillMaxWidth()) {
      Button(
          enabled = SessionManager.getIsNetworkAvailable(),
          onClick = { onCreateTripClick() },
          modifier =
              Modifier.width(360.dp)
                  .height(70.dp)
                  .padding(bottom = 20.dp)
                  .align(Alignment.TopCenter)
                  .testTag("createTripButton"),
          colors = ButtonDefaults.buttonColors(containerColor = primaryContainerLight)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = Icons.Default.Add.name,
                  modifier = Modifier.width(18.dp).height(18.dp),
                  tint = onPrimaryContainerLight)
              Text(
                  text = "Create a New Trip",
                  style =
                      TextStyle(
                          fontSize = 20.sp,
                          lineHeight = 20.sp,
                          fontWeight = FontWeight(500),
                          color = onPrimaryContainerLight,
                          textAlign = TextAlign.Center,
                          letterSpacing = 0.1.sp,
                      ))
            }
          }
    }
  }
}
