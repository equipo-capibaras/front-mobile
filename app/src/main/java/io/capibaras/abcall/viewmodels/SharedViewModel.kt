package io.capibaras.abcall.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.capibaras.abcall.ui.viewmodels.ErrorUIState
import io.capibaras.abcall.ui.viewmodels.SuccessUIState

class SharedViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var errorUIState by mutableStateOf<ErrorUIState>(ErrorUIState.NoError)
        private set
    var successUIState by mutableStateOf<SuccessUIState>(SuccessUIState.NoSuccess)
        private set

    fun setLoadingState(value: Boolean) {
        isLoading = value
    }

    fun setErrorState(errorState: ErrorUIState) {
        errorUIState = errorState
    }

    fun setSuccessState(successState: SuccessUIState) {
        successUIState = successState
    }


    fun clearErrorUIState() {
        errorUIState = ErrorUIState.NoError
    }

    fun clearSuccessUIState() {
        successUIState = SuccessUIState.NoSuccess
    }
}
