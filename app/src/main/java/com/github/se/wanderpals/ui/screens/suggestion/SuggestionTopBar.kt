package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.R
import androidx.compose.ui.res.painterResource


/**
 * Composable function that represents the top bar for suggestion feed screen. Displays a search bar
 * with an option to clear the search text and a menu icon for additional actions.
 *
 * @param searchSuggestionText The current text in the search bar.
 * @param onSearchSuggestionTextChanged Callback function triggered when the search text changes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionTopBar(
    searchSuggestionText: String,
    onSearchSuggestionTextChanged: (String) -> Unit,
    onHistoryClick: () -> Unit  // Add this parameter for navigation action
) {

    // Constant for empty search text
    val EMPTY_SEARCH = ""

    // State to track search bar activation
    var active by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // DockedSearchBar component
            DockedSearchBar(
                modifier =
                Modifier.padding(top = 16.dp, start = 4.dp).testTag("suggestionSearchBar"),
                query = searchSuggestionText,
                onQueryChange = { newText -> onSearchSuggestionTextChanged(newText) },
                onSearch = {},
                active = false,
                onActiveChange = { active = it },
                placeholder = {
                    Text(
                        text = "Search a suggestion",
                        style =
                        TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight(400),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp,
                        ))
                },
                trailingIcon = {
                    // Show search icon if search text is empty, otherwise show clear icon
                    if (searchSuggestionText.isNotEmpty()) {
                        IconButton(
                            modifier = Modifier.testTag("clearSuggestionSearchButton"),
                            onClick = { onSearchSuggestionTextChanged(EMPTY_SEARCH) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = Icons.Default.Clear.name,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = Icons.Default.Search.name,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }) {}

            IconButton(onClick = { onHistoryClick()}) {
                Icon(
                    painter = painterResource(R.drawable.history),
                    contentDescription = "History",
                    modifier = Modifier.padding(top=16.dp).size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
