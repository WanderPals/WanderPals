package com.github.se.wanderpals.ui.screens.trip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.wanderpals.R
import com.github.se.wanderpals.model.viewmodel.AgendaViewModel
import com.github.se.wanderpals.ui.theme.WanderPalsTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.util.Locale

@Preview(showSystemUi = true)
@Composable
fun AgendaPreview() {
  WanderPalsTheme { Agenda("") }
}

@Composable
fun Agenda(tripId: String, viewModel: AgendaViewModel = viewModel()) {
  val uiState by viewModel.uiState.collectAsState()
  Surface(
      modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      CalendarWidget(
          days = DateUtil.daysOfWeek,
          yearMonth = uiState.yearMonth,
          dates = uiState.dates,
          onPreviousMonthButtonClicked = { prevMonth -> viewModel.toPreviousMonth(prevMonth) },
          onNextMonthButtonClicked = { nextMonth -> viewModel.toNextMonth(nextMonth) },
          onDateClickListener = { date -> viewModel.onDateSelected(date) })
      Spacer(modifier = Modifier.padding(1.dp))
      HorizontalDivider(
          modifier = Modifier.padding(horizontal = 12.dp),
          thickness = 1.dp,
          color = MaterialTheme.colorScheme.secondary)
    }
  }
}

@Composable
fun CalendarWidget(
    days: Array<String>,
    yearMonth: YearMonth,
    dates: List<CalendarUiState.Date>,
    onPreviousMonthButtonClicked: (YearMonth) -> Unit,
    onNextMonthButtonClicked: (YearMonth) -> Unit,
    onDateClickListener: (CalendarUiState.Date) -> Unit,
) {
  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Row {
      repeat(days.size) {
        val item = days[it]
        DayItem(item, modifier = Modifier.weight(1f))
      }
    }
    Header(
        yearMonth = yearMonth,
        onPreviousMonthButtonClicked = onPreviousMonthButtonClicked,
        onNextMonthButtonClicked = onNextMonthButtonClicked)
    Content(dates = dates, onDateClickListener = onDateClickListener)
  }
}

@Composable
fun Header(
    yearMonth: YearMonth,
    onPreviousMonthButtonClicked: (YearMonth) -> Unit,
    onNextMonthButtonClicked: (YearMonth) -> Unit,
) {
  Row {
    IconButton(onClick = { onPreviousMonthButtonClicked.invoke(yearMonth.minusMonths(1)) }) {
      Icon(
          imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
          contentDescription = stringResource(id = R.string.back))
    }
    Text(
        text = yearMonth.getDisplayName(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.weight(1f).align(Alignment.CenterVertically))
    IconButton(onClick = { onNextMonthButtonClicked.invoke(yearMonth.plusMonths(1)) }) {
      Icon(
          imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
          contentDescription = stringResource(id = R.string.next))
    }
  }
}

@Composable
fun DayItem(day: String, modifier: Modifier = Modifier) {
  Box(modifier = modifier) {
    Text(
        text = day,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.align(Alignment.Center).padding(10.dp))
  }
}

@Composable
fun Content(
    dates: List<CalendarUiState.Date>,
    onDateClickListener: (CalendarUiState.Date) -> Unit,
) {
  Column {
    var index = 0
    repeat(6) {
      if (index >= dates.size) return@repeat
      Row {
        repeat(7) {
          val item = if (index < dates.size) dates[index] else CalendarUiState.Date.Empty
          ContentItem(
              date = item, onClickListener = onDateClickListener, modifier = Modifier.weight(1f))
          index++
        }
      }
    }
  }
}

@Composable
fun ContentItem(
    date: CalendarUiState.Date,
    onClickListener: (CalendarUiState.Date) -> Unit,
    modifier: Modifier = Modifier
) {
  // Define the content description
  val description =
      "Date ${date.dayOfMonth}, ${if (date.isSelected) "Selected" else "Not Selected"}"

  Box(
      modifier =
          modifier
              .aspectRatio(1f) // Makes the box a square to fit a circle perfectly
              .clip(CircleShape) // Clips the Box into a Circle
              .background(
                  color =
                      if (date.isSelected) {
                        MaterialTheme.colorScheme.secondaryContainer
                      } else {
                        Color.Transparent
                      })
              .clickable { onClickListener(date) }
              .semantics {
                contentDescription = description
              } // Add semantics with contentDescription
      ) {
        Text(
            text = date.dayOfMonth,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.Center).padding(10.dp))
      }
}

data class CalendarUiState(
    val yearMonth: YearMonth,
    val dates: List<Date>,
    val selectedDate: LocalDate? = null
) {
  companion object {
    val Init = CalendarUiState(yearMonth = YearMonth.now(), dates = emptyList())
  }

  data class Date(
      val dayOfMonth: String,
      val yearMonth: YearMonth,
      val year: Year,
      val isSelected: Boolean
  ) {
    companion object {
      val Empty = Date("", YearMonth.now(), Year.now(), false)
    }
  }
}

class CalendarDataSource {
  fun getDates(yearMonth: YearMonth, selectedDate: LocalDate?): List<CalendarUiState.Date> {
    return yearMonth.getDayOfMonthStartingFromMonday().map { date ->
      val isSelected = date == selectedDate && date.monthValue == yearMonth.monthValue
      CalendarUiState.Date(
          dayOfMonth = if (date.monthValue == yearMonth.monthValue) "${date.dayOfMonth}" else "",
          yearMonth = yearMonth,
          year = Year.of(date.year),
          isSelected = isSelected)
    }
  }
}

object DateUtil {

  val daysOfWeek: Array<String>
    get() {
      val daysOfWeek = Array(7) { "" }

      for (dayOfWeek in DayOfWeek.values()) {
        val localizedDayName =
            dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault())
        daysOfWeek[dayOfWeek.value - 1] = localizedDayName
      }

      return daysOfWeek
    }
}

fun YearMonth.getDayOfMonthStartingFromMonday(): List<LocalDate> {
  val firstDayOfMonth = LocalDate.of(year, month, 1)
  val firstMondayOfMonth = firstDayOfMonth.with(DayOfWeek.MONDAY)
  val firstDayOfNextMonth = firstDayOfMonth.plusMonths(1)

  return generateSequence(firstMondayOfMonth) { it.plusDays(1) }
      .takeWhile { it.isBefore(firstDayOfNextMonth) }
      .toList()
}

fun YearMonth.getDisplayName(): String {
  return "${month.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())} $year"
}
