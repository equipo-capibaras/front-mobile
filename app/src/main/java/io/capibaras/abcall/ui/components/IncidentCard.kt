package io.capibaras.abcall.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.capibaras.abcall.ui.util.IncidentStatus
import java.util.Locale


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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("incident-card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(165.dp)
                .background(MaterialTheme.colorScheme.surface)
                .clickable(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current,
                    onClick = onClick
                )
                .padding(16.dp)
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .padding(bottom = 12.dp),
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                lineHeight = 32.sp,
                color = MaterialTheme.colorScheme.onTertiary,
            )
            if (status != null) {
                IncidentChips(modifier = Modifier.padding(bottom = 12.dp), status, recentlyUpdated)
                IncidentDates(status, filedDate, locale, escalatedDate, closedDate)
            }
        }
    }
}
