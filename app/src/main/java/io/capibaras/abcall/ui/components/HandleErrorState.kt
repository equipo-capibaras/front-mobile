package io.capibaras.abcall.ui.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import io.capibaras.abcall.R
import io.capibaras.abcall.ui.viewmodels.ErrorUIState

@Composable
fun HandleErrorState(
    errorUIState: ErrorUIState,
    snackbarHostState: SnackbarHostState,
    onClearError: () -> Unit
) {
    if (errorUIState is ErrorUIState.Error) {
        val errorMessage =
            errorUIState.resourceId?.let { stringResource(it) } ?: errorUIState.message
            ?: stringResource(R.string.unknow_error)
        LaunchedEffect(errorMessage) {
            snackbarHostState.showSnackbar(
                CustomSnackbarVisuals(
                    message = errorMessage,
                    state = SnackbarState.ERROR
                )
            )
            onClearError()
        }
    }
}