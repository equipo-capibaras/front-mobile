package io.capibaras.abcall.ui.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import io.capibaras.abcall.R
import io.capibaras.abcall.ui.viewmodels.utils.ErrorUIState
import io.capibaras.abcall.ui.viewmodels.utils.SuccessUIState

@Composable
fun HandleErrorState(
    errorUIState: ErrorUIState,
    snackbarHostState: SnackbarHostState,
    onClearError: () -> Unit
) {
    if (errorUIState is ErrorUIState.Error) {
        val errorMessage =
            errorUIState.resourceId?.let { stringResource(it) } ?: errorUIState.message
            ?: stringResource(R.string.unknown_error)
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

@Composable
fun HandleSuccessState(
    successUIState: SuccessUIState,
    snackbarHostState: SnackbarHostState,
    onClearSuccess: () -> Unit
) {
    if (successUIState is SuccessUIState.Success) {
        val successMessage =
            successUIState.resourceId?.let { stringResource(it) }
                ?: stringResource(R.string.success_action)
        LaunchedEffect(successMessage) {
            snackbarHostState.showSnackbar(
                CustomSnackbarVisuals(
                    message = successMessage,
                    state = SnackbarState.SUCCESS
                )
            )
            onClearSuccess()
        }
    }
}


