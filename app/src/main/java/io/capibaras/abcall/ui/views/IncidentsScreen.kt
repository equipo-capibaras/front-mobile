package io.capibaras.abcall.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.capibaras.abcall.R
import io.capibaras.abcall.ui.components.IncidentCard
import io.capibaras.abcall.ui.util.IncidentStatus
import io.capibaras.abcall.ui.viewmodels.IncidentViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentsScreen(
    navController: NavController,
    viewModel: IncidentViewModel = koinViewModel()
) {
    val pullToRefreshState = rememberPullToRefreshState()

    DisposableEffect(Unit) {
        viewModel.getIncidents()
        onDispose {}
    }

    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = viewModel.isRefreshing,
        onRefresh = { viewModel.onRefresh() },
    ) {
        if (viewModel.incidents.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.empty_incidents_list),
                    fontSize = 18.sp
                )
            }
        } else {
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
                        onClick = { navController.navigate("create-incident/${incident.id}") }
                    )
                }
            }
        }
    }

}
