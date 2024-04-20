package com.github.se.wanderpals.ui.screens

import android.graphics.drawable.Icon
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.DividerItemDecoration
import com.github.se.wanderpals.R
import com.github.se.wanderpals.model.viewmodel.AdminViewModel

/** The Admin screen. */
@Composable
fun Admin( adminViewModel: AdminViewModel) {
    val userList by adminViewModel.listOfUsers.collectAsState()

    val rainbowColorsBrush = remember {
    Brush.sweepGradient(
        listOf(
            Color(0xFF9575CD),
            Color(0xFFBA68C8),
            Color(0xFFE57373),
            Color(0xFFFFB74D),
            Color(0xFFFFF176),
            Color(0xFFAED581),
            Color(0xFF4DD0E1),
            Color(0xFF9575CD)))
  }
  Column {
      Card(
          modifier =
          Modifier
              .align(alignment = Alignment.CenterHorizontally)
              .padding(vertical = 50.dp)
              .size(width = 300.dp, height = 200.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          elevation = CardDefaults.cardElevation(defaultElevation = 15.dp)
      ) {
          Image( // Icon for the admin screen
              painterResource(id = R.drawable.ptn_file),
              contentDescription = "Admin Icon",
              contentScale = ContentScale.Crop,
              modifier =
              Modifier
                  .padding(40.dp)
                  .size(100.dp)
                  .clip(CircleShape)
                  .border(3.dp, rainbowColorsBrush, CircleShape)
          )
      }

      Text(
          text = "Admin",
          style = MaterialTheme.typography.displaySmall,
          modifier = Modifier.padding(start = 30.dp),
          fontWeight = FontWeight.Bold
      )
      HorizontalDivider(
          modifier = Modifier.padding(20.dp)
      )


            for (user in userList) {
                Log.d("Admin", "User: $user")

                Row(

                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 30.dp)
                    )


                        //to change the role of a user
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Edit Role",
                                modifier = Modifier
                                    .size(20.dp)

                            )
                        }
                        //to delete a user
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Delete User",
                                modifier = Modifier
                                    .size(20.dp)

                            )
                        }



                }



        }

  }
}
