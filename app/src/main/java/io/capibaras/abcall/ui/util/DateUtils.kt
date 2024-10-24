package io.capibaras.abcall.ui.util

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatDateToLocale(dateString: String, locale: Locale): String {
    val utcDateTime = ZonedDateTime.parse(dateString)
    val localDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", locale)

    return localDateTime.format(formatter)
}