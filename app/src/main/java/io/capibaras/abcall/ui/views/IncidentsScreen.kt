package io.capibaras.abcall.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.capibaras.abcall.ui.components.IncidentCard
import io.capibaras.abcall.ui.components.IncidentStatus
import io.capibaras.abcall.ui.viewmodels.IncidentViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun IncidentsScreen(viewModel: IncidentViewModel = koinViewModel()) {
    val scrollState = rememberScrollState()
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        viewModel.incidents.forEach { incident ->
            IncidentCard(
                title = incident.name,
                status = IncidentStatus.fromString(incident.history.last().action),
                escalatedDate = incident.escalatedDate,
                filedDate = incident.filedDate,
                closedDate = incident.closedDate,
                recentlyUpdated = incident.recentlyUpdated,
                onClick = {}
            )
        }
    }
}
