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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role as semanticRole
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.viewmodel.AdminViewModel
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.ui.PullToRefreshLazyColumn
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.dashboard.DashboardMemberDetail
import com.google.firebase.storage.StorageReference

/**
 * Admin screen that allows the owner to manage the users of the trip.
 *
 * @param adminViewModel The ViewModel that manages the Admin screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Admin(adminViewModel: AdminViewModel, storageReference: StorageReference?) {
  val context = LocalContext.current
  val userList by adminViewModel.listOfUsers.collectAsState()
  val currentUser by adminViewModel.currentUser.collectAsState()

  var deleteMemberDialog by remember { mutableStateOf(false) }
  var promoteMemberDialog by remember { mutableStateOf(false) }

  var userToDelete by remember { mutableStateOf("") }
  var userToUpdate by remember { mutableStateOf(User()) }
  var userToPromote by remember { mutableStateOf(User()) }

  var displayedChoiceBox by remember { mutableStateOf(false) }
  var selectedImages by remember { mutableStateOf<List<Uri?>>(emptyList()) }

  var editNameDialog by remember { mutableStateOf(false) }
  var newName by remember { mutableStateOf("") }

  val radioOptions = listOf(Role.OWNER, Role.ADMIN, Role.MEMBER, Role.VIEWER)
  var (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

  var modifierButton by remember { mutableStateOf(false) }

  val singlePhotoPickerLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = { uri -> selectedImages = listOf(uri) })

  var selectedMember by remember { mutableStateOf<User?>(null) }

  var isAlreadyClicked by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) { adminViewModel.getUsers() }

  // Details of the users:
  selectedMember?.let { user ->
    DashboardMemberDetail(
        member = user,
        onDismiss = { selectedMember = null }, // When the popup is dismissed
    )
  }

  Column(modifier = Modifier.fillMaxSize()) {
    TopAppBar(
        title = {
          Text(
              text = "Members",
              modifier = Modifier.testTag("MembersTitle"),
              style =
                  TextStyle(
                      fontWeight = FontWeight.Bold,
                      fontSize = 20.sp,
                      color = MaterialTheme.colorScheme.onPrimary))
        },
        navigationIcon = {
          IconButton(
              onClick = { navigationActions.goBack() },
              modifier = Modifier.testTag("BackButton"),
              colors =
                  IconButtonDefaults.iconButtonColors(
                      contentColor = MaterialTheme.colorScheme.onPrimary)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
              }
        },
        colors =
            topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary),
    )

    Column(modifier = Modifier.fillMaxSize().testTag("adminScreen")) {
      Card(
          modifier =
              Modifier.align(alignment = Alignment.CenterHorizontally)
                  .padding(vertical = 10.dp)
                  .size(width = 350.dp, height = 200.dp)
                  .shadow(elevation = 20.dp, shape = RoundedCornerShape(30.dp))
                  .clip(RoundedCornerShape(30.dp))
                  .testTag("adminScreenCard"),
          colors =
              CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Box(modifier = Modifier.fillMaxSize()) {
              // Top half with Primary color
              Surface(
                  modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f).align(Alignment.TopCenter),
                  color = MaterialTheme.colorScheme.primary) {
                    Box(modifier = Modifier.fillMaxSize()) {
                      IconButton(
                          onClick = {
                            // If modifier is already pressed pressing again will cancel the action
                            modifierButton = !modifierButton
                            isAlreadyClicked = false
                          },
                          modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                            Icon(Icons.Default.Create, contentDescription = "Modify")
                          }
                    }
                  }

              // Bottom half with surface variant color
              Surface(
                  modifier =
                      Modifier.fillMaxWidth().fillMaxHeight(0.7f).align(Alignment.BottomCenter),
                  color = MaterialTheme.colorScheme.surfaceVariant) {
                    Column(
                        verticalArrangement = Arrangement.Top,
                    ) {
                      if (currentUser != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        // Display the user name
                        Row(
                            modifier = Modifier.padding(horizontal = 100.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                          Spacer(modifier = Modifier.width(12.dp))
                          Text(
                              text = currentUser!!.name,
                              style = MaterialTheme.typography.bodyLarge,
                              fontSize = 14.sp,
                              color =
                                  if (modifierButton && !isAlreadyClicked)
                                      MaterialTheme.colorScheme.error
                                  else MaterialTheme.colorScheme.primary,
                              modifier =
                                  Modifier.clickable {
                                        if (modifierButton && !isAlreadyClicked) {
                                          isAlreadyClicked = true
                                          modifierButton = false
                                          newName = currentUser!!.name
                                          editNameDialog = true
                                        }
                                      }
                                      .padding(horizontal = 10.dp)
                                      .testTag("userName"),
                              fontWeight = FontWeight.Bold)
                        }

                        // Add vertical padding
                        Spacer(modifier = Modifier.height(20.dp))

                        // Display the trip name
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                              Spacer(modifier = Modifier.width(12.dp))
                              Icon(
                                  imageVector = Icons.Default.LocationOn,
                                  contentDescription = "Trip Icon",
                                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                  modifier = Modifier.size(20.dp))
                              Spacer(modifier = Modifier.width(10.dp))
                              Text(
                                  text = "Current trip : " + currentUser!!.tripName,
                                  style = MaterialTheme.typography.bodyMedium,
                                  fontSize = 14.sp,
                                  fontWeight = FontWeight.Bold,
                                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                                  modifier = Modifier.testTag("tripName"))
                            }
                        // Add vertical padding
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                              Spacer(modifier = Modifier.width(14.dp))
                              Icon(
                                  imageVector = Icons.Default.Build,
                                  contentDescription = "Role Icon",
                                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                  modifier = Modifier.size(20.dp))
                              Spacer(modifier = Modifier.width(10.dp))
                              Text(
                                  text = "Your role : " + currentUser?.role.toString(),
                                  style = MaterialTheme.typography.bodyMedium,
                                  fontSize = 14.sp,
                                  fontWeight = FontWeight.Bold,
                                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                                  modifier = Modifier.testTag("userRole"))
                            }

                        // Add vertical padding
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                              Spacer(modifier = Modifier.width(14.dp))
                              Icon(
                                  imageVector = Icons.Default.Email,
                                  contentDescription = "Nickname Icon",
                                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                  modifier = Modifier.size(20.dp))
                              Spacer(modifier = Modifier.width(10.dp))
                              Text(
                                  text = currentUser!!.nickname,
                                  style = MaterialTheme.typography.bodyMedium,
                                  fontSize = 14.sp,
                                  fontWeight = FontWeight.Bold,
                                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                                  modifier = Modifier.testTag("userNickname"))
                            }
                      }
                    }
                  }

              // Centered round icon
              // If the user has a profile photo, display it, otherwise display a placeholder
              if (selectedImages.isNotEmpty() && selectedImages[0] != null) {
                Log.d("Admin", "Selected Image: ${selectedImages[0]}")
                val riversRef =
                    storageReference?.child("images/${selectedImages[0]?.lastPathSegment}")
                val taskUp = riversRef?.putFile(selectedImages[0]!!)

                taskUp
                    ?.addOnFailureListener { Log.d("Admin", "Failed to upload image") }
                    ?.addOnSuccessListener {
                      Log.d("Admin", "Image uploaded successfully")
                      Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT)
                          .show()
                    }
                taskUp
                    ?.continueWithTask { task ->
                      if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                      }
                      riversRef.downloadUrl
                    }
                    ?.addOnCompleteListener { task ->
                      if (task.isSuccessful) {
                        adminViewModel.modifyCurrentUserProfilePhoto(task.result.toString())
                        Log.d("Admin", "Image URL: ${currentUser?.profilePhoto}")
                      }
                    }
                AsyncImage(
                    model = currentUser?.profilePhoto!!,
                    contentDescription = "Admin Icon",
                    contentScale = ContentScale.Crop,
                    modifier =
                        Modifier.size(80.dp)
                            .align(Alignment.CenterStart)
                            .clip(CircleShape)
                            .offset(x = 20.dp, y = (-37).dp)
                            .border(
                                4.dp,
                                color =
                                    if (modifierButton && !isAlreadyClicked)
                                        MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape)
                            .clickable {
                              if (modifierButton && !isAlreadyClicked) {
                                isAlreadyClicked = true
                                modifierButton = false
                                singlePhotoPickerLauncher.launch(PickVisualMediaRequest())
                              }
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
                          Modifier.size(80.dp)
                              .align(Alignment.CenterStart)
                              .offset(x = 20.dp, y = (-37).dp)
                              .clip(CircleShape)
                              .border(
                                  4.dp,
                                  color =
                                      if (modifierButton && !isAlreadyClicked)
                                          MaterialTheme.colorScheme.error
                                      else MaterialTheme.colorScheme.surfaceVariant,
                                  CircleShape)
                              .clickable {
                                if (modifierButton && !isAlreadyClicked) {
                                  isAlreadyClicked = true
                                  modifierButton = false
                                  singlePhotoPickerLauncher.launch(PickVisualMediaRequest())
                                }
                              }
                              .testTag("IconAdminScreen"))
                }
              }
            }
          }

      // Manage Members
      Text(
          text = "Manage Members",
          style = MaterialTheme.typography.titleLarge,
          modifier = Modifier.padding(start = 30.dp, top = 10.dp).testTag("ManageMembersTitle"),
          fontWeight = FontWeight.Bold)

      HorizontalDivider(
          modifier =
              Modifier.align(Alignment.CenterHorizontally)
                  .padding(10.dp)
                  .width(330.dp)
                  .testTag("ManageMembersDivider"))
      val lazyColumn =
          @Composable {
            LazyColumn(Modifier.fillMaxSize()) {
              items(userList) { user ->
                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .width(340.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                      Row(
                          modifier = Modifier.fillMaxWidth().padding(16.dp),
                          verticalAlignment = Alignment.CenterVertically) {
                            // Display the member profile picture
                            AsyncImage(
                                model = user.profilePictureURL,
                                contentDescription = "User Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier =
                                    Modifier.size(40.dp)
                                        .clip(CircleShape)
                                        .testTag("userProfilePicture" + user.userId))
                            Spacer(modifier = Modifier.width(12.dp))
                            // Display the member name
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f).testTag("userName" + user.userId))

                            // Edit role button
                            if (currentUser!!.role == Role.OWNER &&
                                currentUser!!.userId != user.userId ||
                                currentUser!!.role != Role.OWNER) {
                              IconButton(
                                  onClick = {
                                    userToUpdate = user
                                    selectedOption = user.role
                                    onOptionSelected(selectedOption)
                                    displayedChoiceBox = true
                                  },
                                  modifier = Modifier.testTag("editRoleButton" + user.userId)) {
                                    Icon(
                                        imageVector = Icons.Default.Build,
                                        contentDescription = "Edit Role",
                                        modifier = Modifier.size(20.dp))
                                  }
                            }

                            // Promote member to owner button
                            if (currentUser!!.role == Role.OWNER &&
                                currentUser!!.userId != user.userId) {
                              IconButton(
                                  onClick = {
                                    userToPromote = user
                                    promoteMemberDialog = true
                                  },
                                  modifier = Modifier.testTag("promoteUserButton" + user.userId)) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Promote User",
                                        modifier = Modifier.size(20.dp))
                                  }
                            }

                            // Delete member button
                            if ((currentUser!!.userId == user.userId ||
                                currentUser!!.role.ordinal < user.role.ordinal) &&
                                user.role != Role.OWNER) {
                              IconButton(
                                  onClick = {
                                    deleteMemberDialog = true
                                    userToDelete = user.userId
                                  },
                                  modifier = Modifier.testTag("deleteUserButton" + user.userId)) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "Delete User",
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.error)
                                  }
                            }
                          }
                    }
              }
            }
          }
      PullToRefreshLazyColumn(
          inputLazyColumn = lazyColumn, onRefresh = { adminViewModel.getUsers() })

      // Dialog to promote a member of the trip to owner
      if (promoteMemberDialog) {
        AlertDialog(
            onDismissRequest = { promoteMemberDialog = false },
            confirmButton = {
              TextButton(
                  onClick = {
                    adminViewModel.modifyUser(userToPromote.copy(role = Role.OWNER))
                    adminViewModel.modifyCurrentUserRole(Role.ADMIN)
                  },
                  modifier = Modifier.testTag("confirmPromoteUserButton")) {
                    Text("Confirm")
                  }
            },
            title = { Text("Confirm promotion to Owner") },
            text = { Text("Are you sure you want to promote this user to Owner of the trip ?") },
            dismissButton = {
              TextButton(
                  onClick = { promoteMemberDialog = false },
                  modifier = Modifier.testTag("cancelPromoteUserButton")) {
                    Text("Cancel")
                  }
            })
      }

      // Dialog to kick a member of the trip
      if (deleteMemberDialog) {
        AlertDialog(
            onDismissRequest = { deleteMemberDialog = false },
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
                  onClick = { deleteMemberDialog = false },
                  modifier = Modifier.testTag("cancelDeleteUserButton")) {
                    Text("Cancel")
                  }
            })
      }
      // Box to choose a member's role
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
                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(
                        onClick = {
                          if (currentUser?.userId != userToUpdate.userId &&
                              userToUpdate.role.ordinal > currentUser?.role!!.ordinal) {
                            adminViewModel.modifyUser(userToUpdate.copy(role = selectedOption))
                          } else if (currentUser?.userId == userToUpdate.userId &&
                              userToUpdate.role.ordinal > currentUser?.role!!.ordinal &&
                              currentUser?.role != Role.OWNER) {
                            adminViewModel.modifyCurrentUserRole(selectedOption)
                            adminViewModel.modifyCurrentUserRole(selectedOption)
                          }
                          displayedChoiceBox = false
                        },
                        modifier = Modifier.padding(10.dp).testTag("confirmRoleChangeButton")) {
                          Text("Update Role")
                        }
                  }
                }
              }
        }
      }

      // Dialog to edit the user name
      if (editNameDialog) {
        AlertDialog(
            onDismissRequest = { editNameDialog = false },
            confirmButton = {
              TextButton(
                  onClick = {
                    adminViewModel.modifyCurrentUserName(newName)
                    editNameDialog = false
                  },
                  modifier = Modifier.testTag("confirmEditUserNameButton")) {
                    Text("Save")
                  }
            },
            title = { Text("Edit User Name") },
            text = {
              Column {
                Text("Enter new user name:")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = newName,
                    onValueChange = { newName = it },
                    modifier = Modifier.testTag("editUserNameTextField"))
              }
            },
            dismissButton = {
              TextButton(
                  onClick = { editNameDialog = false },
                  modifier = Modifier.testTag("cancelEditUserNameButton")) {
                    Text("Cancel")
                  }
            })
      }
    }
  }
}
