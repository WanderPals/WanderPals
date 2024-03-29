package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

class FirestoreSuggestionTest {

    @Test
    fun testFromSuggestionConversion() {
        // Prepare a Stop object with necessary properties including date and startTime
        val stop = Stop(
            stopId = "stop1",
            title = "Location Name",
            address = "123 Main St",
            date = LocalDate.now(), // Using current date
            startTime = LocalTime.of(14, 0), // 2:00 PM for simplicity
            duration = 120, // Duration in minutes
            budget = 100.0,
            description = "Stop Description",
            geoCords = GeoCords(0.0, 0.0),
            website = "http://example.com",
            imageUrl = "http://example.com/image.png"
        )
        // Assuming Comment class has appropriate properties
        val comments = listOf(
            Comment(
                commentId = "comment1",
                userId = "user1",
                userName = "User One",
                text = "Great idea!",
                createdAt = LocalDate.now() // Using current date for simplicity
            )
        )
        // Assuming Suggestion class has appropriate properties
        val suggestion = Suggestion(
            suggestionId = "suggestion1",
            userId = "user1",
            userName = "User One",
            text = "Suggestion text",
            createdAt = LocalDate.now(), // Using current date for simplicity
            stop = stop,
            comments = comments
        )

        val firestoreSuggestion = FirestoreSuggestion.fromSuggestion(suggestion)
        assertEquals("suggestion1", firestoreSuggestion.suggestionId)
        assertEquals("Suggestion text", firestoreSuggestion.text)
        // Additional assertions as necessary for `date`, `startTime`, and `duration`
        assertEquals("stop1", firestoreSuggestion.stop.stopId)
        assertEquals(1, firestoreSuggestion.comments.size)
        assertEquals("comment1", firestoreSuggestion.comments.first().commentId)
    }

    @Test
    fun testToSuggestionConversion() {
        // Firestore versions of Stop and Comment need to handle the conversion of date and startTime to String
        val firestoreStop = FirestoreStop(
            stopId = "stop1",
            title = "Location Name",
            address = "123 Main St",
            date = LocalDate.now().toString(),
            startTime = LocalTime.of(14, 0).toString(), // "14:00"
            duration = 120, // Duration in minutes
            budget = 100.0,
            description = "Stop Description",
            geoCords = GeoCords(0.0, 0.0),
            website = "http://example.com",
            imageUrl = "http://example.com/image.png"
        )
        val firestoreComments = listOf(
            FirestoreComment(
                commentId = "comment1",
                userId = "user1",
                userName = "User One",
                text = "Great idea!",
                createdAt = LocalDate.now().toString()
            )
        )
        val firestoreSuggestion = FirestoreSuggestion(
            suggestionId = "suggestion1",
            userId = "user1",
            userName = "User One",
            text = "Suggestion text",
            createdAt = LocalDate.now().toString(),
            stop = firestoreStop,
            comments = firestoreComments
        )

        val suggestion = firestoreSuggestion.toSuggestion()
        assertEquals("suggestion1", suggestion.suggestionId)
        assertEquals("Suggestion text", suggestion.text)
        // Assertions to ensure correct conversion of `date`, `startTime`, and `duration`
        assertEquals("stop1", suggestion.stop.stopId)
        assertEquals(1, suggestion.comments.size)
        assertEquals("comment1", suggestion.comments.first().commentId)
    }
}
