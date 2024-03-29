package com.github.se.wanderpals.ui.screens.trip
import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.data.Stop
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionCreationScreen(
    onCreateSuggestionClick: (Suggestion) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var suggestionText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = "Create a Suggestion",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                ) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    OutlinedTextField(
                        value = suggestionText,
                        onValueChange = { suggestionText = it },
                        label = { Text("Suggestion Title") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                }
                    Button(
                        onClick = {
                            // Create the Suggestion object
                            val suggestion = Suggestion(
                                suggestionId = "", // Generate or get suggestion ID
                                userId = "", // Get user ID
                                userName = "", // Get username
                                text = "",
                                createdAt = LocalDate.now(),
                                stop = Stop(
                                    "",
                                    suggestionText,
                                    "",
                                    LocalDate.now(),
                                    0.0,
                                    description,
                                    GeoCords(0.0, 0.0),
                                    "",
                                    ""
                                )
                            )

                            // Pass the created suggestion to the callback function
                            onCreateSuggestionClick(suggestion)
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                    ) {
                        Text("Create Suggestion")
                    }
                }

        }
    )

}

@Preview(showBackground = true)
@Composable
fun SuggestionCreationScreenPreview() {
    SuggestionCreationScreen(onCreateSuggestionClick = {})
}