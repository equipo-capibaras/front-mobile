package io.capibaras.abcall.ui.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class IncidentStatusTest {
    @Test
    fun `fromString should return OPEN for 'created'`() {
        val result = IncidentStatus.fromString("created")
        assertEquals(IncidentStatus.OPEN, result)
    }

    @Test
    fun `fromString should return ESCALATED for 'escalated'`() {
        val result = IncidentStatus.fromString("escalated")
        assertEquals(IncidentStatus.ESCALATED, result)
    }

    @Test
    fun `fromString should return CLOSED for 'closed'`() {
        val result = IncidentStatus.fromString("closed")
        assertEquals(IncidentStatus.CLOSED, result)
    }

    @Test
    fun `fromString should return UPDATED for 'updated'`() {
        val result = IncidentStatus.fromString("updated")
        assertEquals(IncidentStatus.UPDATED, result)
    }

    @Test
    fun `fromString should be case-insensitive`() {
        assertEquals(IncidentStatus.OPEN, IncidentStatus.fromString("CREATED"))
        assertEquals(IncidentStatus.ESCALATED, IncidentStatus.fromString("ESCALATED"))
        assertEquals(IncidentStatus.CLOSED, IncidentStatus.fromString("CLOSED"))
        assertEquals(IncidentStatus.UPDATED, IncidentStatus.fromString("UPDATED"))
    }

    @Test
    fun `fromString should return null for unknown status`() {
        val result = IncidentStatus.fromString("unknown_status")
        assertNull(result)
    }
}
