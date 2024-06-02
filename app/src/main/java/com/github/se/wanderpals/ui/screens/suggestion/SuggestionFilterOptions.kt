package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * Composable function to display the filter options for suggestions.
 *
 * @param onFilterSelected The callback function for when a filter is selected.
 */
@Composable
fun SuggestionFilterOptions(onFilterSelected: (String) -> Unit) {
  // Possible sorting options
  val sortOptions = listOf("Creation date", "Like number", "Comment number")

  // This will keep track of the currently selected filter
  var selectedFilter by remember { mutableStateOf(sortOptions.first()) }

  // Call onFilterSelected whenever the filter changes
  LaunchedEffect(selectedFilter) { onFilterSelected(selectedFilter) }

  // UI for filter options
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(start = 27.dp, end = 27.dp)
              .padding(vertical = 16.dp)
              .testTag("suggestionSortingOptions"),
      horizontalArrangement = Arrangement.SpaceBetween) {
        sortOptions.forEach { filter ->
          SuggestionFilterButton(
              text = filter,
              isSelected = selectedFilter == filter,
              onSelect = { selectedFilter = filter })
        }
      }
}
