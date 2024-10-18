package io.capibaras.abcall.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.capibaras.abcall.ui.viewmodels.ValidationUIState

@Composable
fun DefaultTextField(
    value: String,
    onValueChange: (String) -> Unit,
    validationState: ValidationUIState,
    @androidx.annotation.StringRes labelRes: Int,
    isPassword: Boolean = false,
    testTag: String
) {
    CustomOutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = stringResource(labelRes)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .testTag(testTag),
        isError = validationState is ValidationUIState.Error,
        supportingText = {
            if (validationState is ValidationUIState.Error) {
                Text(stringResource(validationState.resourceId))
            }
        },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Password
        ) else null
    )
}

