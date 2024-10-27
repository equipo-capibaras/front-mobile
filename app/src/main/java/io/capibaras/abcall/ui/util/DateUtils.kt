package io.capibaras.abcall.ui.util

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun formatDateToLocale(dateString: String, locale: Locale): String {
    require(dateString.isNotBlank()) { "Date string cannot be empty" }
    return try {
        val utcDateTime = ZonedDateTime.parse(dateString)
        val localDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale)
        localDateTime.format(formatter)
    } catch (e: Exception) {
        throw IllegalArgumentException("Invalid date format: $dateString", e)
    }
}