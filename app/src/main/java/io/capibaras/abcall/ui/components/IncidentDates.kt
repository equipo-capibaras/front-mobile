package io.capibaras.abcall.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import io.capibaras.abcall.R
import io.capibaras.abcall.ui.util.IncidentStatus
import io.capibaras.abcall.ui.util.formatDateToLocale
import java.util.Locale


@Composable
fun IncidentDates(
    status: IncidentStatus?,
    filedDate: String,
    locale: Locale,
    escalatedDate: String?,
    closedDate: String?
) {
    IncidentDateString(R.string.filed, filedDate, locale)

    if (status === IncidentStatus.ESCALATED && escalatedDate !== null) {
        IncidentDateString(
            R.string.escalated,
            escalatedDate,
            locale
        )
    } else if (status === IncidentStatus.CLOSED && closedDate !== null) {
        IncidentDateString(R.string.closed, closedDate, locale)
    }
}

@Composable
fun IncidentDateString(dateLabel: Int? = null, date: String, locale: Locale) {
    val dateAnnotatedString = buildAnnotatedString {
        if (dateLabel != null) {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                append("${stringResource(dateLabel)}: ")
            }
        }
        append(formatDateToLocale(date, locale))
    }

    Text(
        text = dateAnnotatedString,
        color = MaterialTheme.colorScheme.onBackground,
        lineHeight = 20.sp,
        fontSize = 14.sp
    )
}