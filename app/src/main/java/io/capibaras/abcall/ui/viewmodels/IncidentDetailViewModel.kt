package io.capibaras.abcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.capibaras.abcall.data.repositories.IncidentsRepository
import kotlinx.coroutines.launch

class IncidentDetailViewModel(
    private val incidentsRepository: IncidentsRepository
) : ViewModel() {


    fun markIncidentAsViewed(incidentId: String) {
        viewModelScope.launch {
            incidentsRepository.markAsViewed(incidentId)
        }
    }
}