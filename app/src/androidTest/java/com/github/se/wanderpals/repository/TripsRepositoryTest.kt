package com.github.se.wanderpals.repository

import com.github.se.wanderpals.model.repository.TripsRepository
import org.junit.Test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Assert.assertTrue
import org.junit.Assert.fail

class TripsRepositoryTest {

    private lateinit var repository: TripsRepository
    private val testUid = "testUser123"
    private val testTripId = "testTrip123"

    @Before
    fun setUp() {
        repository = TripsRepository(testUid)
    }

    @Test
    fun testAddAndGetAndRemoveTripId() = runBlocking(Dispatchers.IO) {
        try {
            // Set a 5-second timeout for the entire block of operations
            withTimeout(5000) {
                // Add a test trip ID
                //val addResult = repository.addTripId(testTripId)
                //assertTrue(addResult)

                // Retrieve trip IDs and check if the test ID is present
                val tripIds = repository.getTripsIds()
                assertTrue(tripIds.contains(testTripId))

                //repository.removeTripId(testTripId)
                //val tripIdsAfterDelete = repository.getTripsIds()
                //assertFalse(tripIds.contains(testTripId))

            }
        } catch (e: TimeoutCancellationException) {
            // If a timeout occurs, fail the test
            fail("The operation timed out after 5 seconds")
        }
    }


}
