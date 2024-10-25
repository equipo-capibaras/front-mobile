package io.capibaras.abcall.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import io.capibaras.abcall.ui.components.IncidentCard
import io.capibaras.abcall.ui.components.IncidentStatus
import io.capibaras.abcall.ui.viewmodels.IncidentViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentsScreen(
    viewModel: IncidentViewModel = koinViewModel()
) {
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = viewModel.isRefreshing,
        onRefresh = { viewModel.onRefresh() },
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            items(viewModel.incidents.size) { index ->
                val incident = viewModel.incidents[index]
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

}
