package io.capibaras.abcall.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import io.capibaras.abcall.ui.viewmodels.IncidentDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun IncidentDetailScreen(viewModel: IncidentDetailViewModel = koinViewModel(), incidentId: String) {
    LaunchedEffect(Unit) {
        viewModel.markIncidentAsViewed(incidentId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Próximamente detalle del incidente",
            modifier = Modifier
                .padding(bottom = 24.dp)
                .testTag("incident-detail-title")
        )

    }
}