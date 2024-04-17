package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionDetail(
    suggestionId: String,
    viewModel: SuggestionsViewModel,
    navActions: NavigationActions,
) {
    val suggestions = viewModel.state.collectAsState()
    val suggestion = suggestions.value.find { it.suggestionId == suggestionId } ?: return
    val isLiked = viewModel.getIsLiked(suggestion.suggestionId)
    val likesCount = viewModel.getNbrLiked(suggestion.suggestionId)

    var newCommentText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = suggestion.stop.title, style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("SuggestionTitle"))
                },
                navigationIcon = {
                    IconButton(onClick = { navActions.goBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            modifier = Modifier.testTag("BackButton")
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier =
            Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Adding the heart icon for likes
            IconButton(onClick = {
                viewModel.toggleLikeSuggestion(suggestion)
            }) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) Color.Red else Color.Gray,
                    modifier = Modifier.testTag("LikeButton")
                )
            }
            Text(
                text = "$likesCount likes",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("LikesCount")
            )
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("DescriptionTitle")
            )
            Text(
                text = suggestion.stop.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.testTag("DescriptionText")
            )

            // Additional details like address and website
            Text(
                text = "Address: ${suggestion.stop.address.ifBlank { "No address provided" }}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (suggestion.stop.address.isNotBlank()) Color.Black else Color.Gray,
                modifier = Modifier.testTag("AddressText").padding(top = 8.dp)
            )
            Text(
                text = "Website: ${suggestion.stop.website.ifBlank { "No website provided" }}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (suggestion.stop.website.isNotBlank()) Color.Black else Color.Gray,
                modifier = Modifier.testTag("WebsiteText").padding(top = 4.dp)
            )

            OutlinedTextField(
                value = newCommentText,
                onValueChange = { newCommentText = it },
                placeholder = { Text("Add a comment") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .testTag("NewCommentInput")
            )
            Button(
                onClick = {
                    if (newCommentText.isNotBlank()) {
                        viewModel.addComment(
                            suggestion,
                            Comment("", "", "tempUsername", newCommentText, LocalDate.now()))
                        newCommentText = ""
                    }
                },
                modifier = Modifier.align(Alignment.End).testTag("SendCommentButton")
            ) {
                Text("Send")
            }

            if (suggestion.comments.isNotEmpty()) {
                Text("Comments", style = MaterialTheme.typography.titleMedium, modifier = Modifier.testTag("CommentsHeader"))
                suggestion.comments.forEach { comment ->
                    SuggestionComment(comment = comment, suggestionsViewModel = viewModel)
                }
            } else {
                Text("No comments yet", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.testTag("NoCommentsMessage"))
            }
        }
    }
}