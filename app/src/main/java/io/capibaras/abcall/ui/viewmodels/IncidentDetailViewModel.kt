package io.capibaras.abcall.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.capibaras.abcall.data.database.models.Incident
import io.capibaras.abcall.data.repositories.IncidentsRepository
import io.capibaras.abcall.ui.util.ErrorMessage
import io.capibaras.abcall.ui.util.StateMediator
import io.capibaras.abcall.ui.util.mapErrorToMessage
import io.capibaras.abcall.ui.viewmodels.utils.ErrorUIState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IncidentDetailViewModel(
    private val incidentsRepository: IncidentsRepository,
    private val stateMediator: StateMediator
) : ViewModel() {
    var isRefreshing by mutableStateOf(false)
        private set

    var incident by mutableStateOf<Incident?>(null)
        private set

    fun loadIncidentAndMarkAsViewed(incidentId: String) {
        viewModelScope.launch {
            getIncident(incidentId)
            markIncidentAsViewed(incidentId)
        }
    }

    private suspend fun getIncident(incidentId: String) {
        if (stateMediator.isLoading) return
        if (!isRefreshing) stateMediator.setLoadingState(true)

        val result = incidentsRepository.getIncident(incidentId)

        result.fold(
            onSuccess = { incidentDetail ->
                incident = incidentDetail
                stateMediator.clearErrorUIState()
            },
            onFailure = { error ->
                when (val errorMessage = mapErrorToMessage(error)) {
                    is ErrorMessage.Res -> {
                        stateMediator.setErrorState(ErrorUIState.Error(errorMessage.resId))
                    }

                    is ErrorMessage.Text -> {
                        stateMediator.setErrorState(ErrorUIState.Error(message = errorMessage.message))
                    }
                }
            }
        )
        stateMediator.setLoadingState(false)

    }

    private suspend fun markIncidentAsViewed(incidentId: String) {
        incidentsRepository.markAsViewed(incidentId)

    }

    fun onRefresh(incidentId: String) {
        isRefreshing = true
        viewModelScope.launch {
            delay(500)
            loadIncidentAndMarkAsViewed(incidentId)
            isRefreshing = false
        }
    }
}