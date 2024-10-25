package io.capibaras.abcall.ui.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.format.DateTimeParseException
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

    @Test(expected = DateTimeParseException::class)
    fun `should throw exception for invalid date format`() {
        val invalidDateString = "invalid-date-string"
        val locale = Locale("es", "ES")

        formatDateToLocale(invalidDateString, locale)
    }
}
