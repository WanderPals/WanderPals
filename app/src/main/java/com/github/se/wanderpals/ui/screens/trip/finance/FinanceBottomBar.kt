package com.github.se.wanderpals.ui.screens.trip.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FinanceBottomBar(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(MaterialTheme.colorScheme.primary),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 18.dp)
        ) {
            Text(
                text = "My total expenses",
                color = Color.White,
                modifier = Modifier.align(Alignment.Start)
            )
            Text(
                text = "400 CHF",
                color = Color.White,
                modifier = Modifier.align(Alignment.Start),

                )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(end = 18.dp)
        ) {
            Text(
                text = "Total trip expenses",
                color = Color.White,
                modifier = Modifier.align(Alignment.End)
            )
            Text(
                text = "5000 CHF",
                color = Color.White,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}