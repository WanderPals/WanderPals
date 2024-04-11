package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Comment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun SuggestionComment(comment: Comment) {
    Column(
        modifier = Modifier
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
            .fillMaxWidth()
            .testTag(comment.commentId)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth(),
        ){
            Text(
                text = comment.userName,
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier.testTag("commentUserName")
            )
        }

        Text(
            text = "Created on : ${comment.createdAt.format(DateTimeFormatter.ofPattern("d MMM yyyy"))}",
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier.testTag("commentCreatedAt")
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).testTag("commentDivider"))

        Surface(modifier = Modifier
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .padding(8.dp)) {
            Text(
                text = comment.text,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
                    .testTag("commentText"),
                fontSize = 14.sp
            )
        }

    }
}

@Preview (showBackground = true)
@Composable
fun SuggestionCommentPreview() {
    SuggestionComment(
        Comment(
            commentId = "1",
            userId = "1",
            userName = "Alice",
            text = "This is a comment",
            createdAt = LocalDate.now()
        )
    )
}