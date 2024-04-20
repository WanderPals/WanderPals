package com.github.se.wanderpals.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.R


/**
 * The Admin screen.
 */
@Composable
fun Admin() {
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
                Color(0xFF9575CD)
            )
        )
    }
    Column{
        Card( modifier = Modifier
            .align(alignment = Alignment.CenterHorizontally)
            .padding(vertical = 50.dp)
            .size(width = 300.dp, height = 200.dp),
            colors = CardDefaults.cardColors(
                containerColor  = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 15.dp
            )

        ){
            Image( // Icon for the admin screen
                painterResource(id = R.drawable.ptn_file),
                contentDescription = "Admin Icon",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(40.dp)
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(3.dp, rainbowColorsBrush, CircleShape)
            )

        }
    }
}