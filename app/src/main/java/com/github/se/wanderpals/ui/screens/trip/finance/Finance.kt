package com.github.se.wanderpals.ui.screens.trip.finance

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.R
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.ui.navigation.Route


@Preview
@Composable
fun FinancePreview() {
    Finance()
}
/** The Finance screen. */
@Composable
fun Finance() {

    var selectedOption by remember { mutableStateOf("Expenses") }
    Scaffold(
        topBar = {
            Column(
                   verticalArrangement = Arrangement.Center,
                   horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                        OutlinedButton(
                            onClick = { navigationActions.navigateTo(Route.DASHBOARD) },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                        Text(
                            text = "Finance",
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = 24.sp, // Taille de police personnalisée
                                fontWeight = FontWeight.Bold // Police en gras
                            ),
                            modifier = Modifier.padding(start = 30.dp)

                        )

                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavigationButton(
                        text = "Expenses",
                        imageId= R.drawable.expenses,
                        isSelected = selectedOption == "Expenses" ,
                        onClick = {selectedOption = "Expenses"})
                    NavigationButton(
                        text = "Categories",
                        imageId= R.drawable.categories ,
                        isSelected = selectedOption == "Categories" ,
                        onClick = {selectedOption = "Categories"})
                    NavigationButton(
                        text = "Debts",
                        imageId= R.drawable.balance ,
                        isSelected = selectedOption == "Debts" ,
                        onClick = {selectedOption = "Debts"})
                }

            }
        },
        bottomBar = {

        }) {
        it
    }
}


@Composable
fun NavigationButton(
    text: String,
    imageId : Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painterResource(id = imageId),
            contentDescription = text,
            modifier = Modifier.size(20.dp),)

        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,

            )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(100.dp)
                .height(4.dp).background(if(isSelected) Color.Blue else Color.Transparent),

            ) {}
    }
}