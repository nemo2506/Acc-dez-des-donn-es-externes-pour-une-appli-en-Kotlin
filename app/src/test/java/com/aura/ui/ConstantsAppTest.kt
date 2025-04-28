package com.aura.ui

import org.junit.Assert.*

import org.junit.Test

class ConstantsAppTest {

    @Test
    fun `test CURRENT_ID constant`() {
        assertEquals("currentId", ConstantsApp.CURRENT_ID)
    }

    @Test
    fun `test API_URL constant`() {
        assertEquals("http://192.168.1.11:8080/", ConstantsApp.API_URL)
    }
}