package io.capibaras.abcall.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.capibaras.abcall.R
import io.capibaras.abcall.data.database.models.History
import io.capibaras.abcall.data.database.models.Incident
import io.capibaras.abcall.ui.components.IncidentChips
import io.capibaras.abcall.ui.components.IncidentDateString
import io.capibaras.abcall.ui.components.IncidentDates
import io.capibaras.abcall.ui.util.IncidentStatus
import io.capibaras.abcall.ui.viewmodels.IncidentDetailViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentDetailScreen(viewModel: IncidentDetailViewModel = koinViewModel(), incidentId: String) {
    DisposableEffect(Unit) {
        viewModel.loadIncidentAndMarkAsViewed(incidentId)
        onDispose {}
    }

    val incidentInfo = viewModel.incident
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = viewModel.isRefreshing,
        onRefresh = { viewModel.onRefresh(incidentId) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            if (incidentInfo != null) {
                val incidentStatus = IncidentStatus.fromString(incidentInfo.history.last().action)!!
                Text(
                    text = incidentInfo.name,
                    modifier = Modifier
                        .padding(bottom = 21.dp)
                        .testTag("incident-detail-title"),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                IncidentDates(
                    status = incidentStatus,
                    escalatedDate = incidentInfo.escalatedDate,
                    filedDate = incidentInfo.filedDate,
                    closedDate = incidentInfo.closedDate,
                    locale = Locale.getDefault()
                )
                IncidentChips(
                    modifier = Modifier.padding(vertical = 29.dp),
                    status = incidentStatus,
                    recentlyUpdated = incidentInfo.recentlyUpdated
                )

                Text(
                    text = incidentInfo.history.first().description,
                )

                if (incidentInfo.history.size > 1) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 21.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    IncidentHistory(incidentInfo)
                }
            }
        }
    }
}

@Composable
fun IncidentHistory(incidentInfo: Incident) {
    Text(text = stringResource(R.string.incident_history), fontWeight = FontWeight.SemiBold)
    incidentInfo.history.drop(1).reversed().forEach { comment ->
        if (comment.description.isNotEmpty())
            IncidentComment(history = comment)
    }
}

@Composable
fun IncidentComment(history: History) {
    Column(modifier = Modifier.padding(top = 21.dp)) {
        IncidentDateString(date = history.date, locale = Locale.getDefault())
        Text(text = history.description, modifier = Modifier.padding(top = 10.dp))
    }
}