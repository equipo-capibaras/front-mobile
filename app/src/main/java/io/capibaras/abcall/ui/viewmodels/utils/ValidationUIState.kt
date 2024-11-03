package io.capibaras.abcall.ui.viewmodels.utils

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
sealed interface ValidationUIState {
    data class Error(@StringRes val resourceId: Int) : ValidationUIState
    data object NoError : ValidationUIState
}