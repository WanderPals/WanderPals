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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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
fun FinanceTopBarPreview() {
    FinanceTopBar(
        currentSelectedOption = FinanceOption.EXPENSES,
        onSelectOption = { /* handle option selection */ },
        onCurrencyClick = {}
    )
}
/**
 * Composable function for displaying the top bar in the Finance screen. Provides navigation options
 * and a back button.
 *
 * @param currentSelectedOption The currently selected finance option.
 * @param onSelectOption Callback function for selecting a finance option.
 */
@Composable
fun FinanceTopBar(
    currentSelectedOption: FinanceOption,
    onSelectOption: (FinanceOption) -> Unit,
    onCurrencyClick: () -> Unit) {
  Column(
      modifier = Modifier.testTag("financeTopBar"),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement =  Arrangement.Center
            ){
                IconButton(
                    modifier = Modifier.testTag("financeBackButton"),
                    onClick = { navigationActions.navigateTo(Route.DASHBOARD) },
                ) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                    )
                }
                Text(
                    modifier = Modifier.padding(start = 20.dp),
                    text = "Finance",
                    textAlign = TextAlign.Center,
                    style =
                    TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                )
            }

            OutlinedButton(
                modifier = Modifier.padding(end = 20.dp),
                onClick = {  },
            ) {
                Text(
                    text = "CHF",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,

                )
            }
            }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically) {
              NavigationButton(
                  text = FinanceOption.EXPENSES.toString(),
                  imageId = R.drawable.expenses,
                  isSelected = currentSelectedOption == FinanceOption.EXPENSES,
                  onClick = { onSelectOption(FinanceOption.EXPENSES) })
              NavigationButton(
                  text = FinanceOption.CATEGORIES.toString(),
                  imageId = R.drawable.categories,
                  isSelected = currentSelectedOption == FinanceOption.CATEGORIES,
                  onClick = { onSelectOption(FinanceOption.CATEGORIES) })
              NavigationButton(
                  text = FinanceOption.DEBTS.toString(),
                  imageId = R.drawable.balance,
                  isSelected = currentSelectedOption == FinanceOption.DEBTS,
                  onClick = { onSelectOption(FinanceOption.DEBTS) })
            }
      }
}

/**
 * Composable function for displaying a navigation button.
 *
 * @param text The text to display on the button.
 * @param imageId The resource ID of the image for the button.
 * @param isSelected Whether the button is currently selected.
 * @param onClick Callback function for when the button is clicked.
 */
@Composable
fun NavigationButton(text: String, imageId: Int, isSelected: Boolean, onClick: () -> Unit) {

  Column(
      modifier = Modifier
          .clickable(onClick = onClick)
          .testTag(text + "Button"),
      horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painterResource(id = imageId),
            contentDescription = text,
            modifier = Modifier.size(20.dp),
        )

        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier =
            Modifier
                .width(100.dp)
                .height(4.dp)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                ),
        ) {}
      }
}
