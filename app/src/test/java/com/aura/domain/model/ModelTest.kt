package com.aura.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for model classes in the com.aura.domain.model package.
 */
class ModelTest {
    /**
     * Tests [LoginReportModel] with a null value.
     */
    @Test
    fun `LoginReportModel granted should be null when not provided`() {
        // When
        val loginReport = LoginReportModel(granted = null)
        // Then
        assertNull(loginReport.granted)
    }


    /**
     * Tests that [LoginReportModel] correctly stores the granted property.
     */
    @Test
    fun `LoginReportModel should correctly store granted property`() {
        // When
        val loginReport = LoginReportModel(granted = true)
        // Then
        assertTrue(loginReport.granted!!)
    }

    /**
     * Tests that [BalanceReportModel] correctly stores the balance property.
     */
    @Test
    fun `BalanceReportModel should correctly store balance property`() {
        // When
        val balanceReport = BalanceReportModel(balance = 1500.00)
        // Then
        assertEquals(1500.00, balanceReport.balance!!, 0.0001)
    }

    /**
     * Tests [BalanceReportModel] with a null balance value.
     */
    @Test
    fun `BalanceReportModel balance should be null when not provided`() {
        // When
        val balanceReport = BalanceReportModel(balance = null)
        // Then
        assertNull(balanceReport.balance)
    }

    /**
     * Tests that [TransferReportModel] correctly stores the done property.
     */
    @Test
    fun `TransferReportModel should correctly store done property`() {
        // When
        val transferReport = TransferReportModel(done = false)
        // Then
        assertFalse(transferReport.done!!)
    }

    /**
     * Tests [TransferReportModel] with a null done value.
     */
    @Test
    fun `TransferReportModel done should be null when not provided`() {
        // When
        val transferReport = TransferReportModel(done = null)
        // Then
        assertNull(transferReport.done)
    }

    /**
     * Tests that [User] correctly stores the id and password properties.
     */
    @Test
    fun `User should correctly store id and password`() {
        // When
        val user = User(id = "user123", password = "securePassword")
        // Then
        assertEquals("user123", user.id)
        assertEquals("securePassword", user.password)
    }

    /**
     * Tests that [Account] correctly stores the id, main, and balance properties.
     */
    @Test
    fun `Account should correctly store id, main, and balance`() {
        // When
        val account = Account(id = "account001", main = true, balance = 1000.00)
        // Then
        assertEquals("account001", account.id)
        assertEquals(true, account.main)
        assertEquals(1000.00, account.balance, 0.0001)
    }

    /**
     * Tests that [Transfer] correctly stores the sender, recipient, and amount properties.
     */
    @Test
    fun `Transfer should correctly store sender, recipient and amount`() {
        // When
        val transfer = Transfer(sender = "acc001", recipient = "acc002", amount = 250.0)
        // Then
        assertEquals("acc001", transfer.sender)
        assertEquals("acc002", transfer.recipient)
        assertEquals(250.0, transfer.amount, 0.0001)
    }
}
