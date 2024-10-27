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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IncidentViewModel(
    private val incidentRepository: IncidentsRepository,
    private val stateMediator: StateMediator
) : ViewModel() {
    var incidents by mutableStateOf<List<Incident>>(emptyList())
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    init {
        getIncidents()
    }

    private fun getIncidents() {
        if (stateMediator.isLoading) return
        if (!isRefreshing) stateMediator.setLoadingState(true)

        viewModelScope.launch {
            val result = incidentRepository.getIncidents()

            result.fold(
                onSuccess = { incidentList ->
                    incidents = incidentList
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
    }

    fun onRefresh() {
        isRefreshing = true
        viewModelScope.launch {
            delay(500)
            getIncidents()
            isRefreshing = false
        }
    }
}
