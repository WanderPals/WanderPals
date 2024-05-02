package com.github.se.wanderpals.ui.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role as semanticRole
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.github.se.wanderpals.model.data.Role as Role
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.viewmodel.AdminViewModel
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.PullToRefreshLazyColumn
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.dashboard.DashboardMemberDetail
import com.github.se.wanderpals.ui.screens.dashboard.DashboardMemberItem
import com.google.firebase.storage.StorageReference

/**
 * Admin screen that allows the owner to manage the users of the trip.
 *
 * @param adminViewModel The ViewModel that manages the Admin screen.
 */
@Composable
fun Admin(adminViewModel: AdminViewModel, storageReference: StorageReference) {
    val context = LocalContext.current
  val userList by adminViewModel.listOfUsers.collectAsState()
  val currentUser by adminViewModel.currentUser.collectAsState()

  var displayed by remember { mutableStateOf(false) }
  var userToDelete by remember { mutableStateOf("") }
  var displayedChoiceBox by remember { mutableStateOf(false) }
  var userToUpdate by remember { mutableStateOf(User()) }
  var selectedImages by remember { mutableStateOf<List<Uri?>>(emptyList()) }

  val radioOptions = listOf(Role.OWNER, Role.ADMIN, Role.MEMBER, Role.VIEWER)
  var (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

  var modifierButton by remember { mutableStateOf(false) }

  val singlePhotoPickerLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = { uri -> selectedImages = listOf(uri) })

  var selectedMember by remember { mutableStateOf<User?>(null) }

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

  LaunchedEffect(Unit) { adminViewModel.getUsers() }

  // Details of the users:

  selectedMember?.let { user ->
    DashboardMemberDetail(
        member = user,
        onDismiss = { selectedMember = null }, // When the popup is dismissed
    )
  }

  Column(modifier = Modifier.testTag("adminScreen")) {
    IconButton(
        onClick = { modifierButton = true },
        modifier = Modifier.align(BiasAlignment.Horizontal(0.9F))) {
          Icon(Icons.Default.Create, contentDescription = "Modify")
        }

    Card(
        modifier =
            Modifier.align(alignment = Alignment.CenterHorizontally)
                .padding(vertical = 10.dp)
                .size(width = 350.dp, height = 200.dp)
                .shadow(elevation = 20.dp, shape = RoundedCornerShape(50.dp))
                .clip(RoundedCornerShape(30.dp))
                .testTag("adminScreenCard"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        // elevation = CardDefaults.cardElevation(defaultElevation = 100.dp)
    ) {
      Row(verticalAlignment = BiasAlignment.Vertical(-0.6f)) {
        if (selectedImages.isNotEmpty()) {
            Log.d("Admin", "Selected Image: ${selectedImages[0]}")
            //create a reference to the uri of the image
            val riversRef = storageReference.child("images/${selectedImages[0]?.lastPathSegment}")
            //upload the image to the firebase storage
            val taskUp = riversRef.putFile(selectedImages[0]!!)

            //Register observers to listen for state changes
            // and progress of the upload
            taskUp
                .addOnFailureListener{
                    // Handle unsuccessful uploads
                    Log.d("Admin", "Failed to upload image")
                }
                .addOnSuccessListener {
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    Log.d("Admin", "Image uploaded successfully")
                    Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                }

            taskUp
                .continueWithTask{ task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    riversRef.downloadUrl

                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        adminViewModel.modifyCurrentUserProfilePhoto(task.result.toString())
                        Log.d("Admin", "Image URL: ${currentUser?.profilePhoto}")
                    }
                }
            adminViewModel.modifyCurrentUserProfilePhoto(selectedImages[0].toString())
          AsyncImage( // Icon for the admin screen
              model = currentUser?.profilePhoto!!,
              contentDescription = "Admin Icon",
              contentScale = ContentScale.Crop,
              modifier =
                  Modifier.padding(top = 40.dp, start = 40.dp, end = 40.dp)
                      .size(100.dp)
                      .clip(CircleShape)
                      .border(3.dp, rainbowColorsBrush, CircleShape)
                      .clickable {
                        if (modifierButton)
                            singlePhotoPickerLauncher.launch(PickVisualMediaRequest())
                      }
                      .testTag("IconAdminScreen"))
        } else {
          if (currentUser != null) {
              Log.d("Admin", "Current User: ${currentUser!!.profilePhoto}")
            AsyncImage(
                model = currentUser!!.profilePhoto,
                contentDescription = "Admin Icon",
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier.padding(top = 40.dp, start = 40.dp, end = 40.dp)
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(3.dp, rainbowColorsBrush, CircleShape)
                        .clickable {
                          if (modifierButton)
                              singlePhotoPickerLauncher.launch(PickVisualMediaRequest())
                        }
                        .testTag("IconAdminScreen"))
          }
        }
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 10.dp, top = 30.dp)) {
              if (currentUser != null) {
                Text(
                    text = currentUser!!.name,
                    style = MaterialTheme.typography.displaySmall,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("UserName"),
                    fontWeight = FontWeight.Bold)
              }
              HorizontalDivider(modifier = Modifier.padding(end = 30.dp).testTag("CardDivider"))

              if (currentUser != null) {
                Text(
                    text = currentUser!!.tripName,
                    style = MaterialTheme.typography.displayMedium,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 30.dp, top = 10.dp).testTag("UserEmail"))
              }
            }
      }
      Text(
          text = currentUser?.role.toString(),
          modifier = Modifier.padding(start = 60.dp, top = 20.dp),
          fontWeight = FontWeight.Bold,
          fontStyle = FontStyle.Italic)
    }

    Text(
        text = "Role Management",
        style = MaterialTheme.typography.displaySmall,
        modifier = Modifier.padding(start = 30.dp, top = 30.dp).testTag("AdminTitle"),
        fontWeight = FontWeight.Bold)

    HorizontalDivider(modifier = Modifier.padding(20.dp).testTag("AdminDivider"))
    if (currentUser?.role == Role.OWNER || currentUser?.role == Role.ADMIN) {
      val lazyColumn =
          @Composable {
            LazyColumn(Modifier.fillMaxSize()) {
              items(userList) { user ->
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                      Text(
                          text = user.name,
                          style = MaterialTheme.typography.titleMedium,
                          modifier = Modifier.padding(start = 30.dp).testTag("userName"))

                      // to change the role of a user
                      if (currentUser!!.role != Role.OWNER) {
                        IconButton(
                            onClick = {
                              userToUpdate = user
                              selectedOption = user.role
                              // Log.d("Admin", "User: $selectedOption")
                              // change the onOptionSelected
                              onOptionSelected(selectedOption)
                              displayedChoiceBox = true
                            },
                            modifier = Modifier.testTag("editRoleButton")) {
                              Icon(
                                  imageVector = Icons.Default.Person,
                                  contentDescription = "Edit Role",
                                  modifier = Modifier.size(20.dp))
                            }
                      }

                      // Transfer Owner rights
                      if (currentUser!!.role == Role.OWNER && currentUser!!.userId != user.userId) {
                        IconButton(
                            onClick = {
                              adminViewModel.modifyUser(user.copy(role = Role.OWNER))
                              adminViewModel.modifyCurrentUserRole(Role.ADMIN)
                            }) {
                              Icon(
                                  Icons.Default.Star,
                                  contentDescription = "transferRights",
                                  modifier = Modifier.size(20.dp))
                            }
                      }
                      // to delete a user
                      if ((currentUser!!.userId == user.userId ||
                          currentUser!!.role.ordinal < user.role.ordinal) &&
                          currentUser!!.role != Role.OWNER) {
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
              }
            }
          }
      PullToRefreshLazyColumn(
          inputLazyColumn = lazyColumn, onRefresh = { adminViewModel.getUsers() })
      if (displayed) {
        AlertDialog(
            onDismissRequest = { displayed = false },
            confirmButton = {
              TextButton(
                  onClick = {
                    adminViewModel.deleteUser(userToDelete)
                    if (userToDelete == currentUser?.userId)
                        navigationActions.navigateTo(Route.OVERVIEW)
                  },
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
                                modifier =
                                    Modifier.padding(start = 16.dp).testTag("stringRole$text"),
                                color =
                                    if (currentUser!!.role.ordinal <= text.ordinal) Color.Black
                                    else Color.Gray)
                          }
                    }
                  }
                  HorizontalDivider()
                  // ok button to the right
                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(
                        onClick = {
                          userToUpdate = userToUpdate.copy(role = selectedOption)
                          if (currentUser?.userId != userToUpdate.userId &&
                              userToUpdate.role.ordinal > currentUser?.role!!.ordinal) {
                            adminViewModel.modifyUser(userToUpdate)
                          } else if (currentUser?.userId == userToUpdate.userId &&
                              userToUpdate.role.ordinal > currentUser?.role!!.ordinal &&
                              currentUser?.role != Role.OWNER) {
                            adminViewModel.modifyCurrentUserRole(selectedOption)
                          }
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
    } else {
      Column(modifier = Modifier.padding(5.dp)) {
        val lazyColumn =
            @Composable {
              LazyColumn(Modifier.fillMaxSize()) {
                items(userList) { member ->
                  DashboardMemberItem(member = member, onClick = { selectedMember = member })
                  if (member != userList.last()) { // Add a divider between the members
                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(horizontal = 32.dp)
                                .testTag("divider" + member.userId))
                    Spacer(modifier = Modifier.height(4.dp))
                  }
                }
              }
            }
        PullToRefreshLazyColumn(
            inputLazyColumn = lazyColumn, onRefresh = { adminViewModel.getUsers() })
      }
    }
  }
}
