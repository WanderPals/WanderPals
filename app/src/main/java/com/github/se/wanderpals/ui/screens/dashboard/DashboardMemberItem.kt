package com.github.se.wanderpals.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.service.SessionManager

/**
 * Composable function that represents a single member item in the member list.
 *
 * @param member The member object to be displayed.
 * @param onClick The callback for the member item click.
 */
@Composable
fun DashboardMemberItem(member: User, onClick: () -> Unit = {}) {
  val cardColors =
      ButtonDefaults.buttonColors(
          containerColor = Color.White // This sets the background color of the Card
          )
  val currentUser = SessionManager.getCurrentUser()

  Button(
      modifier = Modifier.padding(5.dp).testTag("memberCard" + member.userId),
      onClick = onClick,
      shape = RoundedCornerShape(30),
      colors = cardColors // Use the cardColors with the white background
      ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text =
                        member.name +
                            if (member.userId == currentUser?.userId) " (You)"
                            else "", // Displays the name and a marker if the user is the current
                    // user
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.testTag("memberName" + member.userId).width(230.dp))
                Row {
                  Text(
                      text = member.role.toString(),
                      style = MaterialTheme.typography.bodyMedium,
                      color = Color.Black,
                      modifier = Modifier.testTag("memberRole" + member.userId))
                  if (member.role == Role.OWNER) { // Display a star icon if the user is the owner
                    // Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "ownerIcon",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp).testTag("ownerIcon" + member.userId))
                  }
                }
              }
        }
      }
}
