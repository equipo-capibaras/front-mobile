package io.capibaras.abcall.ui.viewmodels

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
sealed interface ErrorUIState {
    data class Error(@StringRes val resourceId: Int) : ErrorUIState
    data object NoError : ErrorUIState
}