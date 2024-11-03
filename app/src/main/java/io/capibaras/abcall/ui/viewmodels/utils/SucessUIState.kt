package io.capibaras.abcall.ui.viewmodels.utils

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
sealed interface SuccessUIState {
    data class Success(
        @StringRes val resourceId: Int? = null,
    ) : SuccessUIState

    data object NoSuccess : SuccessUIState
}