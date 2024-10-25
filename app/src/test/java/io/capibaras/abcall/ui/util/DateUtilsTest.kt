package io.capibaras.abcall.ui.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class DateUtilTest {
    @Test
    fun `should format date to Spanish locale`() {
        val dateString = "2024-10-25T14:30:00Z"
        val locale = Locale("es", "ES")

        val formattedDate = formatDateToLocale(dateString, locale)

        assertEquals("25 de octubre de 2024", formattedDate)
    }

    @Test
    fun `should format date to Portuguese locale`() {
        val dateString = "2024-10-25T14:30:00Z"
        val locale = Locale("pt", "BR")

        val formattedDate = formatDateToLocale(dateString, locale)

        assertEquals(
            "25 de outubro de 2024",
            formattedDate
        )
    }

    @Test
    fun `should handle different timezone correctly`() {
        val dateString = "2024-10-25T14:30:00Z"  // UTC time
        val locale = Locale("es", "ES")

        val formattedDate = formatDateToLocale(dateString, locale)

        assertEquals("25 de octubre de 2024", formattedDate)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception for invalid date format`() {
        val invalidDateString = "invalid-date-string"
        val locale = Locale("es", "ES")

        formatDateToLocale(invalidDateString, locale)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw IllegalArgumentException for invalid date format`() {
        val invalidDateString = "invalid-date-string"
        val locale = Locale("es", "AR")

        try {
            formatDateToLocale(invalidDateString, locale)
        } catch (e: IllegalArgumentException) {
            assertEquals("Invalid date format: invalid-date-string", e.message)
            throw e
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw IllegalArgumentException for blank date string`() {
        val blankDateString = ""
        val locale = Locale("es", "CO")

        try {
            formatDateToLocale(blankDateString, locale)
        } catch (e: IllegalArgumentException) {
            assertEquals("Date string cannot be empty", e.message)
            throw e
        }
    }
}
