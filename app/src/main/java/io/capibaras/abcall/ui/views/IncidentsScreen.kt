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
        IncidentCard(
            title = "Cobro incorrecto",
            status = IncidentStatus.fromString("open"),
            escalatedDate = "2024-09-10T12:34:56Z",
            filedDate = "2024-09-10T12:34:56Z",
            closedDate = null,
            recentlyUpdated = false,
            onClick = {}
        )

        IncidentCard(
            title = "Internet no funciona",
            status = IncidentStatus.fromString("escalated"),
            escalatedDate = "2024-09-10T12:34:56Z",
            filedDate = "2024-09-15T12:34:56Z",
            recentlyUpdated = true,
            onClick = {}
        )

        IncidentCard(
            title = "Retorno pago",
            status = IncidentStatus.fromString("closed"),
            filedDate = "2024-09-10T12:34:56Z",
            closedDate = "2024-10-01T12:34:56Z",
            recentlyUpdated = false,
            onClick = {}
        )

        IncidentCard(
            title = "Fallo servicios",
            status = IncidentStatus.fromString("closed"),
            filedDate = "2024-09-10T12:34:56Z",
            closedDate = "2024-10-01T12:34:56Z",
            recentlyUpdated = false,
            onClick = {}
        )
    }
}
