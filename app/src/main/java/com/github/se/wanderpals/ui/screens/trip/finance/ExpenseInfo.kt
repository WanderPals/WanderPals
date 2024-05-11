package com.github.se.wanderpals.ui.screens.trip.finance
import android.graphics.Paint.Align
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Category
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.viewmodel.FinanceViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Preview
@Composable
fun ExpenseInfo(/*financeViewModel: FinanceViewModel,navigationActions : NavigationActions*/){
    //val selectedExpense by financeViewModel.selectedExpense.collectAsState()
    val expense =  Expense(
        expenseId = "3",
        title = "Museum tickets",
        amount = 25.0,
        category = Category.ACTIVITIES,
        userId = "user3",
        userName = "Bob",
        participantsIds = listOf("user1", "user3"),
        names = listOf("John", "Bob"),
        localDate = LocalDate.now()
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ExpenseTopInfo(expense)


    }
}

@Composable
fun ExpenseTopInfo(expense : Expense){
    Surface(
        color = MaterialTheme.colorScheme.primary,
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth().padding(top = 5.dp,end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier.align(Alignment.Top),
                    onClick = { /*navigationActions.navigateTo(Route.FINANCE) */},
                ) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                    )
                }
                ClickableText(
                    onClick = { /* Action à exécuter lors du clic sur le texte "Modify" */ },

                    text = AnnotatedString(
                        text = "Delete",
                        spanStyle = SpanStyle(fontSize = 18.sp, color = Color.White)),
                )
            }
            Text(

                text = expense.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = "${expense.amount} CHF",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Paid by ${expense.userName}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    text = expense.localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,),
                    maxLines = 1
                )
            }
            Text(
                modifier = Modifier.padding(bottom = 5.dp).align(Alignment.Start),
                text = "For ${expense.participantsIds.size} participant(s) :",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Start
            )

        }
    }
}


