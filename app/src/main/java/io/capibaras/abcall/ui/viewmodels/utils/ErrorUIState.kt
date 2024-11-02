package io.capibaras.abcall.ui.viewmodels.utils

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
sealed interface ErrorUIState {
    data class Error(
        @StringRes val resourceId: Int? = null,
        val message: String? = null
    ) : ErrorUIState

    data object NoError : ErrorUIState
}