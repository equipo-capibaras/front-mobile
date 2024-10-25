package io.capibaras.abcall.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.capibaras.abcall.ui.theme.LocalCustomColors

enum class ChipType {
    PRIMARY, SUCCESS, WARNING, ERROR, NEUTRAL
}

data class Chip(
    val text: String,
    val chipType: ChipType,
    val icon: ImageVector? = null
)

data class ChipAttributes(
    val backgroundColor: Color,
    val textColor: Color,
)

@Composable
fun chipAttributes(chipType: ChipType): ChipAttributes {
    return when (chipType) {
        ChipType.PRIMARY -> ChipAttributes(
            backgroundColor = MaterialTheme.colorScheme.primary,
            textColor = MaterialTheme.colorScheme.onPrimary,
        )

        ChipType.WARNING -> ChipAttributes(
            backgroundColor = LocalCustomColors.current.warning,
            textColor = LocalCustomColors.current.onWarning,
        )

        ChipType.SUCCESS -> ChipAttributes(
            backgroundColor = LocalCustomColors.current.success,
            textColor = LocalCustomColors.current.onSuccess,
        )

        ChipType.ERROR -> ChipAttributes(
            backgroundColor = MaterialTheme.colorScheme.error,
            textColor = MaterialTheme.colorScheme.onError,
        )

        ChipType.NEUTRAL -> ChipAttributes(
            backgroundColor = LocalCustomColors.current.neutral,
            textColor = Color.White,
        )
    }
}

@Composable
fun CustomChip(
    icon: ImageVector? = null,
    text: String,
    chipType: ChipType,
) {
    val attributes = chipAttributes(chipType)

    Row(
        modifier = Modifier
            .background(attributes.backgroundColor, shape = RoundedCornerShape(8.dp))
            .height(32.dp)
            .padding(horizontal = 16.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = text
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = attributes.textColor
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        Text(
            text = text,
            color = attributes.textColor,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        )
    }
}

