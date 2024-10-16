package io.capibaras.abcall.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.capibaras.abcall.R

@Composable
fun AccountScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "M",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier.padding(top = 50.dp, bottom = 50.dp),
        ) {
            AccountItem(icon = Icons.Outlined.Person, text = "Maria Aristizabal")
            AccountItem(icon = Icons.Outlined.Email, text = "maria@gmail.com")
            AccountItem(icon = Icons.Filled.Domain, text = "Claro")
        }



        Button(
            onClick = { /* TODO: Acción para cerrar sesión */ },
            modifier = Modifier
                .padding(top = 24.dp),
        ) {
            Text(text = stringResource(R.string.logout), color = Color.White)
        }
    }
}


@Composable
fun AccountItem(icon: ImageVector, text: String) {
    val borderColor = MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                val y = size.height + 16.dp.toPx() - strokeWidth / 2
                drawLine(
                    color = borderColor,
                    start = androidx.compose.ui.geometry.Offset(0f, y),
                    end = androidx.compose.ui.geometry.Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = text,
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
