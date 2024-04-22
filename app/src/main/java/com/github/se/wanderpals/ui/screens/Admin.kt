package com.github.se.wanderpals.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role as semanticRole
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.se.wanderpals.R
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Role as Role
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.viewmodel.AdminViewModel

/**
 * Admin screen that allows the owner to manage the users of the trip.
 *
 * @param adminViewModel The ViewModel that manages the Admin screen.
 */
@Composable
fun Admin(adminViewModel: AdminViewModel) {
  val userList by adminViewModel.listOfUsers.collectAsState()
  var displayed by remember { mutableStateOf(false) }
  var userToDelete by remember { mutableStateOf("") }
  var displayedChoiceBox by remember { mutableStateOf(false) }
  var userToUpdate = User("", "", "", "", Role.VIEWER, GeoCords(0.0, 0.0), "")

  val radioOptions = listOf(Role.OWNER, Role.ADMIN, Role.MEMBER, Role.VIEWER)
  val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

  val rainbowColorsBrush = remember {
    Brush.sweepGradient(
        listOf(
            Color(0xFF9575CD),
            Color(0xFFBA68C8),
            Color(0xFFE57373),
            Color(0xFFFFB74D),
            Color(0xFFFFF176),
            Color(0xFFAED581),
            Color(0xFF4DD0E1),
            Color(0xFF9575CD)))
  }
  Column(Modifier.testTag("adminScreen")) {
    Card(
        modifier =
            Modifier.align(alignment = Alignment.CenterHorizontally)
                .padding(vertical = 50.dp)
                .size(width = 300.dp, height = 200.dp)
                .testTag("adminScreenCard"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 15.dp)) {
          Image( // Icon for the admin screen
              painterResource(id = R.drawable.logo_nsa),
              contentDescription = "Admin Icon",
              contentScale = ContentScale.Crop,
              modifier =
                  Modifier.padding(40.dp)
                      .size(100.dp)
                      .clip(CircleShape)
                      .border(3.dp, rainbowColorsBrush, CircleShape)
                      .testTag("IconAdminScreen"))
        }

    Text(
        text = "Admin",
        style = MaterialTheme.typography.displaySmall,
        modifier = Modifier.padding(start = 30.dp).testTag("AdminTitle"),
        fontWeight = FontWeight.Bold)
    HorizontalDivider(modifier = Modifier.padding(20.dp).testTag("AdminDivider"))

    for (user in userList) {
      Log.d("Admin", "User: $user")

      Row(
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 30.dp).testTag("userName"))

            // to change the role of a user
            IconButton(
                onClick = {
                  userToUpdate = user
                  displayedChoiceBox = true
                },
                modifier = Modifier.testTag("editRoleButton")) {
                  Icon(
                      imageVector = Icons.Default.Person,
                      contentDescription = "Edit Role",
                      modifier = Modifier.size(20.dp))
                }
            // to delete a user
            IconButton(
                onClick = {
                  displayed = true
                  userToDelete = user.userId
                },
                modifier = Modifier.testTag("deleteUserButton")) {
                  Icon(
                      imageVector = Icons.Default.Close,
                      contentDescription = "Delete User",
                      modifier = Modifier.size(20.dp))
                }
          }
    }
    if (displayed) {
      AlertDialog(
          onDismissRequest = { displayed = false },
          confirmButton = {
            TextButton(
                onClick = { adminViewModel.deleteUser(userToDelete) },
                modifier = Modifier.testTag("confirmDeleteUserButton")) {
                  Text("Confirm")
                }
          },
          title = { Text("Confirm Deletion") },
          text = { Text("Are you sure you want to delete this user from the trip?") },
          dismissButton = {
            TextButton(
                onClick = { displayed = false },
                modifier = Modifier.testTag("cancelDeleteCommentButton")) {
                  Text("Cancel")
                }
          })
    }
    if (displayedChoiceBox) {

      Dialog(onDismissRequest = { displayedChoiceBox = false }) {
        Card(
            modifier = Modifier.height(380.dp).padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(3.dp),
            colors = CardDefaults.cardColors(AlertDialogDefaults.containerColor)) {
              Column {
                Text(
                    text = "Change Role",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp).testTag("changeRoleTitle"))
                HorizontalDivider()

                Column(Modifier.selectableGroup()) {
                  radioOptions.forEach { text ->
                    Row(
                        Modifier.fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (text == selectedOption),
                                onClick = { onOptionSelected(text) },
                                role = semanticRole.RadioButton)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                          RadioButton(
                              modifier = Modifier.testTag("radioButton"),
                              selected = (text == selectedOption),
                              onClick =
                                  null // null recommended for accessibility with ScreenReaders
                              )
                          Text(
                              text = text.toString(),
                              style = MaterialTheme.typography.titleSmall,
                              modifier = Modifier.padding(start = 16.dp).testTag("stringRole"))
                        }
                  }
                }
                HorizontalDivider()
                // ok button to the right
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                  TextButton(
                      onClick = {
                        userToUpdate = userToUpdate.copy(role = selectedOption)
                        adminViewModel.modifyUser(user = userToUpdate)
                        displayedChoiceBox = false
                      },
                      modifier = Modifier.padding(10.dp).testTag("ConfirmRoleChangeButton")) {
                        Text("Update Role")
                      }
                }
              }
            }
      }
    }
  }
}
