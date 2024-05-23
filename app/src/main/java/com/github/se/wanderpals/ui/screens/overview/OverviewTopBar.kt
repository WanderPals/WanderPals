package com.github.se.wanderpals.ui.screens.overview

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.se.wanderpals.model.viewmodel.OverviewViewModel
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.service.default_profile_photo
import com.github.se.wanderpals.ui.navigation.Route
import com.google.firebase.auth.FirebaseAuth

// Constant for empty search text
const val EMPTY_SEARCH = ""
/**
 * Composable function that represents the top bar for overview screen. Displays a search bar with
 * an option to clear the search text and a menu icon for additional actions.
 *
 * @param searchText The current text in the search bar.
 * @param onSearchTextChanged Callback function triggered when the search text changes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewTopBar(
    overviewViewModel: OverviewViewModel,
    searchText: String,
    onSearchTextChanged: (String) -> Unit
) {

  // State to track search bar activation:
  var active by remember { mutableStateOf(false) }
  var logout by remember { mutableStateOf(false) }

  val currentUser by overviewViewModel.currentUser.collectAsState()

  if (logout) {
    AlertDialog(
        onDismissRequest = { logout = false },
        title = { Text("Confirm Logout") },
        text = { Text("Are you sure you want to logout?") },
        confirmButton = {
          TextButton(
              onClick = {
                logout = false
                SessionManager.logout()
                FirebaseAuth.getInstance().signOut()
                navigationActions.navigateTo(Route.SIGN_IN)
              },
              modifier = Modifier.testTag("confirmLogoutButton")) {
                Text("Confirm")
              }
        },
        dismissButton = {
          TextButton(
              onClick = { logout = false }, modifier = Modifier.testTag("cancelLogoutButton")) {
                Text("Cancel")
              }
        },
        modifier = Modifier.testTag("logoutDialog"))
  }

  Box(modifier = Modifier.fillMaxWidth()) {
    // DockedSearchBar component
    DockedSearchBar(
        modifier =
            Modifier.align(Alignment.Center)
                .padding(top = 16.dp, bottom = 14.dp)
                .testTag("dockedSearchBar"),
        query = searchText,
        onQueryChange = { newText -> onSearchTextChanged(newText) },
        onSearch = {},
        active = false,
        onActiveChange = { active = it },
        placeholder = { Text("Search a trip") },
        trailingIcon = {
          // Show search icon if search text is empty, otherwise show clear icon
          if (searchText.isNotEmpty()) {
            IconButton(
                modifier = Modifier.testTag("clearSearchButton"),
                onClick = { onSearchTextChanged(EMPTY_SEARCH) }) {
                  Icon(
                      imageVector = Icons.Default.Clear,
                      contentDescription = Icons.Default.Clear.name,
                      modifier = Modifier.size(24.dp),
                      tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
          } else {
            AsyncImage(
                model = (currentUser?.profilePhoto ?: default_profile_photo),
                contentDescription = "Profile photo",
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier.size(34.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape)
                        .clickable { logout = true }
                        .testTag("profilePhoto"))
          }
        },
        leadingIcon = {
          Icon(
              imageVector = Icons.Default.Search,
              contentDescription = Icons.Default.Search.name,
              modifier = Modifier.size(24.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }) {}
  }
}
