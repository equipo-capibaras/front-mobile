package io.capibaras.abcall.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.capibaras.abcall.ui.theme.LocalCustomColors

enum class SnackbarState {
    ERROR,
    SUCCESS,
    INFO
}

@Composable
fun getSnackbarColors(state: SnackbarState): Pair<Color, Color> {
    return when (state) {
        SnackbarState.ERROR -> Pair(
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.onError
        )

        SnackbarState.SUCCESS -> Pair(
            LocalCustomColors.current.success,
            LocalCustomColors.current.onSuccess
        )

        SnackbarState.INFO -> Pair(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary
        )
    }
}
