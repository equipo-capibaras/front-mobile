package io.capibaras.abcall.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.capibaras.abcall.R
import io.capibaras.abcall.data.repositories.IncidentsRepository
import io.capibaras.abcall.ui.util.ErrorMessage
import io.capibaras.abcall.ui.util.StateMediator
import io.capibaras.abcall.ui.util.mapErrorToMessage
import io.capibaras.abcall.ui.viewmodels.utils.ErrorUIState
import io.capibaras.abcall.ui.viewmodels.utils.FieldValidator
import io.capibaras.abcall.ui.viewmodels.utils.SuccessUIState
import io.capibaras.abcall.ui.viewmodels.utils.ValidationUIState
import kotlinx.coroutines.launch

class CreateIncidentViewModel(
    private val stateMediator: StateMediator,
    private val incidentRepository: IncidentsRepository
) : ViewModel() {
    var name by mutableStateOf("")
    var description by mutableStateOf("")

    var nameValidationState by mutableStateOf<ValidationUIState>(ValidationUIState.NoError)
        private set
    var descriptionValidationState by mutableStateOf<ValidationUIState>(ValidationUIState.NoError)
        private set

    fun validateFields(): Boolean {
        var isValid = true

        isValid = FieldValidator.validateField(
            name,
            { nameValidationState = it },
            checks = listOf(
                { name.length <= 60 } to R.string.form_60_length
            )
        ) && isValid

        isValid = FieldValidator.validateField(
            description,
            { descriptionValidationState = it },
            checks = listOf(
                { description.length <= 1000 } to R.string.form_1000_length
            )
        ) && isValid

        return isValid
    }


    fun createIncident(onSuccess: (String) -> Unit) {
        println("createIncident ${stateMediator.isLoading}")
        if (stateMediator.isLoading) return
        stateMediator.setLoadingState(true)
        viewModelScope.launch {
            val result = incidentRepository.createIncident(name, description)

            result.fold(
                onSuccess = { createIncidentResponse ->
                    stateMediator.clearErrorUIState()
                    stateMediator.setSuccessState(SuccessUIState.Success(R.string.create_incident_success))
                    onSuccess(createIncidentResponse.id)
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
}
