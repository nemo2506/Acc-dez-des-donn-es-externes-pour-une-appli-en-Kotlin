package com.aura.ui

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for verifying constant values defined in [ConstantsApp].
 */
class ConstantsAppTest {

    /**
     * Tests that [ConstantsApp.CURRENT_ID] has the expected value.
     */
    @Test
    fun `test CURRENT_ID constant`() {
        assertEquals("currentId", ConstantsApp.CURRENT_ID)
    }

    /**
     * Tests that [ConstantsApp.API_URL] has the expected value.
     */
    @Test
    fun `test API_URL constant`() {
        assertEquals("http://192.168.1.11:8080/", ConstantsApp.API_URL)
    }
}
