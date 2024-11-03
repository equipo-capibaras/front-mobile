package io.capibaras.abcall.ui.components

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics

@Composable
fun CustomSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.semantics {
            liveRegion = LiveRegionMode.Assertive
        },
        snackbar = { snackbarData ->
            val snackbarState = when (val visuals = snackbarData.visuals) {
                is CustomSnackbarVisuals -> visuals.state
                else -> SnackbarState.SUCCESS
            }
            val (backgroundColor, contentColor) = getSnackbarColors(snackbarState)
            Snackbar(
                snackbarData = snackbarData,
                containerColor = backgroundColor,
                contentColor = contentColor,
                dismissActionContentColor = contentColor,
                modifier = Modifier
                    .testTag("snackbar")
                    .clearAndSetSemantics {
                        contentDescription = snackbarData.visuals.message
                    }
            )
        }
    )
}

