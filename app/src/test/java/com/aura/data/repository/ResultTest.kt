package com.aura.data.repository

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for the [Result] sealed class, which represents either a success or failure state.
 */
class ResultTest {

    /**
     * Test that [Result.Success] correctly holds and returns the provided value.
     */
    @Test
    fun `Success should contain the correct value`() {
        // Given: A value to wrap in a Success result
        val expectedValue = "Test Success"

        // When: Creating a Success result
        val result = Result.Success(expectedValue)

        // Then: The result should be a Success and hold the expected value
        assertTrue(result is Result.Success)
        assertEquals(expectedValue, result.value)
    }

    /**
     * Test that [Result.Failure] correctly holds and returns the provided error message.
     */
    @Test
    fun `Failure should contain the correct error message`() {
        // Given: An error message to wrap in a Failure result
        val expectedMessage = "An error occurred"

        // When: Creating a Failure result
        val result = Result.Failure(expectedMessage)

        // Then: The result should be a Failure and hold the expected error message
        assertTrue(result is Result.Failure)
        assertEquals(expectedMessage, result.message)
    }

    /**
     * Test that [Result.Failure] can be created with a null error message.
     */
    @Test
    fun `Failure should allow null error message`() {
        // When: Creating a Failure result with no message
        val result = Result.Failure()

        // Then: The result should be a Failure and have a null message
        assertTrue(result is Result.Failure)
        assertNull(result.message)
    }
}
