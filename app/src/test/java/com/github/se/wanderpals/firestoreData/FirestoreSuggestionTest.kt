package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class FirestoreSuggestionTest {

    @Test
    fun testFromSuggestionConversion() {
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
        val fixedTime = LocalTime.of(14, 30, 15)  // Fixed time for testing, including seconds

        val stop = Stop(
            stopId = "stop1",
            title = "Location Name",
            address = "123 Main St",
            date = LocalDate.now(),
            startTime = fixedTime,
            duration = 120,
            budget = 100.0,
            description = "Stop Description",
            geoCords = GeoCords(0.0, 0.0),
            website = "http://example.com",
            imageUrl = "http://example.com/image.png")

        val comments = listOf(
            Comment(
                commentId = "comment1",
                userId = "user1",
                userName = "User One",
                text = "Great idea!",
                createdAt = LocalDate.now(),
                createdAtTime = fixedTime))

        val suggestion = Suggestion(
            suggestionId = "suggestion1",
            userId = "user1",
            userName = "User One",
            text = "Suggestion text",
            createdAt = LocalDate.now(),
            createdAtTime = fixedTime,
            stop = stop,
            comments = comments,
            userLikes = emptyList())

        val firestoreSuggestion = FirestoreSuggestion.fromSuggestion(suggestion)
        assertEquals("suggestion1", firestoreSuggestion.suggestionId)
        assertEquals("Suggestion text", firestoreSuggestion.text)
        assertEquals(LocalDate.now().format(dateFormatter), firestoreSuggestion.createdAt)
        assertEquals(fixedTime.format(timeFormatter), firestoreSuggestion.createdAtTime)
        assertEquals("stop1", firestoreSuggestion.stop.stopId)
        assertEquals(1, firestoreSuggestion.comments.size)
        assertEquals("comment1", firestoreSuggestion.comments.first().commentId)
    }

    @Test
    fun testToSuggestionConversion() {
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
        val fixedDate = LocalDate.now()
        val fixedTime = LocalTime.of(14, 30, 15)  // Fixed time for testing, including seconds

        val firestoreStop = FirestoreStop(
            stopId = "stop1",
            title = "Location Name",
            address = "123 Main St",
            date = fixedDate.toString(),
            startTime = fixedTime.toString(),
            duration = 120,
            budget = 100.0,
            description = "Stop Description",
            geoCords = GeoCords(0.0, 0.0),
            website = "http://example.com",
            imageUrl = "http://example.com/image.png")

        val firestoreComments = listOf(
            FirestoreComment(
                commentId = "comment1",
                userId = "user1",
                userName = "User One",
                text = "Great idea!",
                createdAt = fixedDate.toString(),
                createdAtTime = fixedTime.format(timeFormatter)))

        val firestoreSuggestion = FirestoreSuggestion(
            suggestionId = "suggestion1",
            userId = "user1",
            userName = "User One",
            text = "Suggestion text",
            createdAt = fixedDate.format(dateFormatter),
            createdAtTime = fixedTime.format(timeFormatter),
            stop = firestoreStop,
            comments = firestoreComments,
            userLikes = emptyList())

        val suggestion = firestoreSuggestion.toSuggestion()
        assertEquals("suggestion1", suggestion.suggestionId)
        assertEquals("Suggestion text", suggestion.text)
        assertEquals(fixedDate, suggestion.createdAt)
        assertEquals(fixedTime, suggestion.createdAtTime)
        assertEquals("stop1", suggestion.stop.stopId)
        assertEquals(1, suggestion.comments.size)
        assertEquals("comment1", suggestion.comments.first().commentId)
    }
}
