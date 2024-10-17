package io.capibaras.abcall.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.capibaras.abcall.ui.theme.LocalCustomColors

@Composable
fun CustomOutlinedButton(
    onClick: () -> Unit,
    content: @Composable() (RowScope.() -> Unit)
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonColors(
            containerColor = Color.Transparent,
            contentColor = LocalCustomColors.current.outlineButtonContent,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.Gray
        ),
        content = content
    )
}
