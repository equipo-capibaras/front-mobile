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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.capibaras.abcall.R
import io.capibaras.abcall.ui.components.CustomOutlinedButton
import io.capibaras.abcall.ui.viewmodels.AccountViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AccountScreen(
    viewModel: AccountViewModel = koinViewModel(),
) {
    val userInfo = viewModel.user
    val showDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (userInfo != null) {
            Box(
                modifier = Modifier
                    .testTag("account-avatar")
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .semantics(mergeDescendants = true) {
                        contentDescription = "Avatar"
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userInfo.name[0].uppercase(),
                    modifier = Modifier.clearAndSetSemantics { },
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(
                modifier = Modifier.padding(top = 50.dp, bottom = 50.dp),
            ) {

                AccountItem(
                    icon = Icons.Outlined.Person,
                    text = userInfo.name,
                    iconLabel = stringResource(R.string.form_name)
                )
                AccountItem(
                    icon = Icons.Outlined.Email,
                    text = userInfo.email,
                    iconLabel = stringResource(R.string.form_email)
                )
                if (userInfo.clientName != null) {
                    AccountItem(
                        icon = Icons.Filled.Domain,
                        text = userInfo.clientName,
                        iconLabel = stringResource(R.string.company)
                    )
                }


            }
        }

        Button(
            onClick = { showDialog.value = true },
            modifier = Modifier
                .padding(top = 24.dp)
                .testTag("logout-button"),
        ) {
            Text(text = stringResource(R.string.logout), color = Color.White)
        }

        if (showDialog.value) ConfirmLogout(showDialog, logout = { viewModel.logout() })
    }
}

@Composable
fun AccountItem(icon: ImageVector, text: String, iconLabel: String) {
    val borderColor = MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "$iconLabel, $text"
            }
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
            tint = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = text,
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
                .clearAndSetSemantics { },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ConfirmLogout(showDialog: MutableState<Boolean>, logout: () -> Unit) {
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = {
            Text(
                text = stringResource(R.string.logout),
                modifier = Modifier.clearAndSetSemantics { })
        },
        text = { Text(text = stringResource(R.string.confirm_logout_question)) },
        confirmButton = {
            Button(
                onClick = {
                    showDialog.value = false
                    logout()
                },
                modifier = Modifier.testTag("confirm-logout")
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            CustomOutlinedButton(onClick = { showDialog.value = false }) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
