package com.github.se.wanderpals.ui.screens.dashboard

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.User

@Composable
fun DashboardMemberDetail(member: User, onDismiss: () -> Unit) {
  Dialog(onDismissRequest = onDismiss) {
    val cardColors =
        CardDefaults.cardColors(
            containerColor = Color.White // This sets the background color of the Card
            )

    Card(
        modifier =
            Modifier.padding(8.dp)
                .fillMaxWidth()
                .border(width = 1.dp, color = Color.White, shape = RoundedCornerShape(25.dp))
                .testTag("memberDetail" + member.userId),
        colors = cardColors) {
          Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                  Row() {
                    Text(
                        text = member.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("memberDetailName" + member.userId))
                  }
                  Row {
                    Text(
                        text = member.role.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.testTag("memberDetailRole" + member.userId))
                    if (member.role == Role.OWNER) {
                      Spacer(modifier = Modifier.width(8.dp))
                      Icon(
                          imageVector = Icons.Default.Star,
                          contentDescription = "ownerIcon",
                          tint = Color.Gray,
                          modifier =
                              Modifier.size(20.dp).testTag("ownerDetailIcon" + member.userId))
                    }
                  }
                }

            Spacer(modifier = Modifier.padding(8.dp))

            Text(
                text = member.email,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("memberDetailEmail" + member.userId))
          }
        }
  }
}
