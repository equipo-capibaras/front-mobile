package io.capibaras.abcall.ui.viewmodels.utils

import io.capibaras.abcall.R

object FieldValidator {
    fun validateField(
        value: String,
        setValidationState: (ValidationUIState) -> Unit,
        checks: List<Pair<() -> Boolean, Int>> = emptyList()
    ): Boolean {
        if (value.isBlank()) {
            setValidationState(ValidationUIState.Error(R.string.form_required))
            return false
        }

        checks.forEach { (check, errorMessageId) ->
            if (!check()) {
                setValidationState(ValidationUIState.Error(errorMessageId))
                return false
            }
        }

        setValidationState(ValidationUIState.NoError)
        return true
    }
}