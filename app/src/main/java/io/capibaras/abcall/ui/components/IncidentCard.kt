package io.capibaras.abcall.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.capibaras.abcall.R
import io.capibaras.abcall.ui.util.formatDateToLocale
import java.util.Locale


enum class IncidentStatus {
    OPEN, ESCALATED, CLOSED, UPDATED;

    companion object {
        fun fromString(status: String): IncidentStatus? {
            return when (status.lowercase()) {
                "created" -> OPEN
                "escalated" -> ESCALATED
                "closed" -> CLOSED
                "updated" -> UPDATED
                else -> null
            }
        }
    }
}

@Composable
fun getChipAttributesFromStatus(status: IncidentStatus): Chip {
    return when (status) {
        IncidentStatus.OPEN -> Chip(
            text = stringResource(R.string.open),
            chipType = ChipType.PRIMARY,
            icon = Icons.Outlined.Schedule
        )

        IncidentStatus.ESCALATED -> Chip(
            text = stringResource(R.string.escalated),
            chipType = ChipType.WARNING,
            icon = Icons.Outlined.WarningAmber
        )

        IncidentStatus.CLOSED -> Chip(
            text = stringResource(R.string.closed),
            chipType = ChipType.SUCCESS,
            icon = Icons.Outlined.Check
        )

        IncidentStatus.UPDATED -> Chip(
            text = stringResource(R.string.updated),
            chipType = ChipType.NEUTRAL,
        )
    }
}

@Composable
fun DateAnnotatedString(dateLabel: Int, date: String, locale: Locale) {
    val dateAnnotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
            append("${stringResource(dateLabel)}: ")
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

@Composable
fun incidentCardContentDescription(
    title: String,
    status: IncidentStatus?,
    recentlyUpdated: Boolean,
    escalatedDate: String? = null,
    filedDate: String,
    closedDate: String? = null,
    locale: Locale
): String {
    val statusText = stringResource(R.string.status)
    val filedText = stringResource(R.string.filed)
    val escalatedText = stringResource(R.string.escalated)
    val closedText = stringResource(R.string.closed)
    val recentlyUpdatedText = stringResource(R.string.recently_updated)

    return buildString {
        append(
            "$title, $statusText: ${getChipAttributesFromStatus(status!!).text}, $filedText: ${
                formatDateToLocale(
                    filedDate,
                    locale
                )
            }"
        )

        if (recentlyUpdated) append(", $recentlyUpdatedText")

        escalatedDate?.let {
            append(", $escalatedText: ${formatDateToLocale(it, locale)}.")
        } ?: closedDate?.let {
            append(", $closedText: ${formatDateToLocale(it, locale)}.")
        }
    }
}


@Composable
fun IncidentCard(
    title: String,
    status: IncidentStatus?,
    recentlyUpdated: Boolean,
    escalatedDate: String? = null,
    filedDate: String,
    closedDate: String? = null,
    onClick: () -> Unit,
) {
    val locale: Locale = Locale.getDefault()
    val interactionSource = remember { MutableInteractionSource() }
    val contentDescriptionText = incidentCardContentDescription(
        title,
        status,
        recentlyUpdated,
        escalatedDate,
        filedDate,
        closedDate,
        locale
    )
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
            .clearAndSetSemantics {
                contentDescription = contentDescriptionText
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(165.dp)
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                lineHeight = 32.sp,
                modifier = Modifier.padding(bottom = 12.dp),
                color = MaterialTheme.colorScheme.onTertiary
            )
            if (status != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    val chipAttributes = getChipAttributesFromStatus(status)
                    CustomChip(
                        text = chipAttributes.text,
                        chipType = chipAttributes.chipType,
                        icon = chipAttributes.icon
                    )

                    if (recentlyUpdated) {
                        val updatedChipAttributes =
                            getChipAttributesFromStatus(IncidentStatus.UPDATED)
                        CustomChip(
                            text = updatedChipAttributes.text,
                            chipType = updatedChipAttributes.chipType,
                            icon = updatedChipAttributes.icon
                        )
                    }

                }
            }

            DateAnnotatedString(R.string.filed, filedDate, locale)

            if (status === IncidentStatus.ESCALATED && escalatedDate !== null) {
                DateAnnotatedString(
                    R.string.escalated,
                    escalatedDate,
                    locale
                )
            } else if (status === IncidentStatus.CLOSED && closedDate !== null) {
                DateAnnotatedString(R.string.closed, closedDate, locale)
            }
        }
    }
}
