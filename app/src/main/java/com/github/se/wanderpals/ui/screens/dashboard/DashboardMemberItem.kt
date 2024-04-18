package com.github.se.wanderpals.ui.screens.dashboard

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.DashboardViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import kotlinx.coroutines.Dispatchers
import java.time.format.DateTimeFormatter

@Composable
fun DashboardMemberItem(member: User, onClick: () -> Unit = {}) {
    val cardColors =
        CardDefaults.cardColors(
            containerColor = Color.White // This sets the background color of the Card
        )

    Card(
        modifier =
        Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(width = 1.dp, color = Color.White, shape = RoundedCornerShape(25.dp))
            .clickable(
                onClick = onClick), // Invoke the onClick lambda when the item is clicked (see
        // SuggestionFeedContent.kt)
        colors = cardColors // Use the cardColors with the white background
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if(member.nickname.isEmpty()) member.name else member.nickname,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("memberName" + member.userId)
                )
                Text(
                    text = member.role.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("memberRole" + member.userId)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewMemberItem() {
    DashboardMemberItem(
        User(
            userId = "1",
            name = "John Doe",
            email = "www.example@xxx.com",
            role = Role.MEMBER,
            profilePictureURL = "https://example.com/image.jpg")
    )
}