package io.capibaras.abcall.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

class CustomSnackbarVisuals(
    override val message: String,
    val state: SnackbarState
) : SnackbarVisuals {

    override val actionLabel: String? = null
    override val duration: SnackbarDuration = SnackbarDuration.Short
    override val withDismissAction: Boolean = true
}

