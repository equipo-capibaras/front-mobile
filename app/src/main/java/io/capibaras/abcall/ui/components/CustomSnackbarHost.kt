package io.capibaras.abcall.ui.components

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

@Composable
fun CustomSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { snackbarData ->
            val snackbarState =
                (snackbarData.visuals as? CustomSnackbarVisuals)?.state
                    ?: SnackbarState.SUCCESS
            val (backgroundColor, contentColor) = getSnackbarColors(snackbarState)
            Snackbar(
                snackbarData = snackbarData,
                containerColor = backgroundColor,
                contentColor = contentColor,
                dismissActionContentColor = contentColor
            )
        }
    )
}