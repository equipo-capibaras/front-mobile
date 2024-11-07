package io.capibaras.abcall.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.capibaras.abcall.R
import io.capibaras.abcall.ui.util.IncidentStatus


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
fun IncidentChips(modifier: Modifier, status: IncidentStatus, recentlyUpdated: Boolean) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
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