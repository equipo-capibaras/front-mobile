package io.capibaras.abcall.ui.viewmodels

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
sealed interface ValidationUIState {
    data class Error(@StringRes val resourceId: Int) : ValidationUIState
    data object NoError : ValidationUIState
}