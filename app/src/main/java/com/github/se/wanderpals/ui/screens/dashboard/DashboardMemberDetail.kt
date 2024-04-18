package com.github.se.wanderpals.ui.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.User

@Composable
fun DashboardMemberDetail(member: User, onDismiss : () -> Unit)
{
    Dialog(onDismissRequest = onDismiss)
    {
        val cardColors =
            CardDefaults.cardColors(
                containerColor = Color.White // This sets the background color of the Card
            )

        Card(
            modifier =
            Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .border(width = 1.dp, color = Color.White, shape = RoundedCornerShape(25.dp)),
            colors = cardColors
        ) {
            Column(modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row()
                    {
                        //Image(painter = , contentDescription = ) profile picture, don't know how to get the image
                        if(member.nickname.length > 10){
                            Column(modifier = Modifier.width(180.dp)) {
                                Text(
                                    text = member.name+ " - ",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.testTag("memberName" + member.userId))
                                Text(
                                    text =  member.nickname,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 14.sp,
                                    modifier = Modifier.testTag("memberName" + member.userId),
                                    color = Color.Gray)
                            }
                        }
                        else {
                            Row(modifier = Modifier.width(180.dp)) {
                                Text(
                                    text = member.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.testTag("memberName" + member.userId)
                                )
                                Text(
                                    text = " - " + member.nickname,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 14.sp,
                                    modifier = Modifier.testTag("memberName" + member.userId),
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                    Text(
                        text = member.role.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.testTag("memberRole" + member.userId),
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Text(text = member.email,
                    style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMemberDetail() {
    DashboardMemberDetail(
        User(
            userId = "1",
            name = "John Doe",
            email = "www.example@xxx.com",
            role = Role.MEMBER,
            profilePictureURL = "https://example.com/image.jpg",
            nickname = "JD121212121"), {}
    )
}