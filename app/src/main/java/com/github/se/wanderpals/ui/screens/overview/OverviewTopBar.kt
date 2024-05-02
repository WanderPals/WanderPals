package com.github.se.wanderpals.ui.screens.overview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.ui.theme.onSurfaceVariantLight

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
fun OverviewTopBar(searchText: String, onSearchTextChanged: (String) -> Unit) {

  // State to track search bar activation
  var active by remember { mutableStateOf(false) }

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
                      tint = onSurfaceVariantLight)
                }
          }
        },
        leadingIcon = {
          Icon(
              imageVector = Icons.Default.Search,
              contentDescription = Icons.Default.Search.name,
              modifier = Modifier.size(24.dp),
              tint = onSurfaceVariantLight)
        }) {}
  }
}
