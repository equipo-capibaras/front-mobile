package io.capibaras.abcall.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.capibaras.abcall.R
import io.capibaras.abcall.ui.components.CustomOutlinedTextField
import io.capibaras.abcall.ui.theme.ABCallTheme
import io.capibaras.abcall.ui.theme.linkText
import io.capibaras.abcall.ui.viewmodels.ValidationUIState
import io.capibaras.abcall.viewmodels.SignUpViewModel
import org.koin.androidx.compose.koinViewModel

@ExperimentalMaterial3Api
@Composable
fun SignUpScreen(navController: NavController, viewModel: SignUpViewModel = koinViewModel()) {
    val context = LocalContext.current
    val nameValidationState = viewModel.nameValidationState
    val emailValidationState = viewModel.emailValidationState
    val companyValidationState = viewModel.companyValidationState
    val passwordValidationState = viewModel.passwordValidationState
    val confirmPasswordValidationState = viewModel.confirmPasswordValidationState

    ABCallTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo de ABCall",
                modifier = Modifier
                    .width(184.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = stringResource(R.string.signup_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 30.dp, bottom = 40.dp)
            )

            CustomOutlinedTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = { Text(text = stringResource(R.string.form_name)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                isError = nameValidationState is ValidationUIState.Error,
                supportingText = {
                    if (nameValidationState is ValidationUIState.Error) {
                        Text(stringResource(nameValidationState.resourceId))
                    }
                }
            )

            CustomOutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text(text = stringResource(R.string.form_email)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                isError = emailValidationState is ValidationUIState.Error,
                supportingText = {
                    if (emailValidationState is ValidationUIState.Error) {
                        Text(stringResource(emailValidationState.resourceId))
                    }
                }
            )

            CompanyDropdown(
                selectedText = viewModel.selectedText,
                onValueChange = { viewModel.selectedText = it },
                companyValidationState = companyValidationState,
                options = listOf("Claro", "Movistar", "Tigo")
            )

            CustomOutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = { Text(text = stringResource(R.string.form_password)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                visualTransformation = PasswordVisualTransformation(),
                isError = passwordValidationState is ValidationUIState.Error,
                supportingText = {
                    if (passwordValidationState is ValidationUIState.Error) {
                        Text(stringResource(passwordValidationState.resourceId))
                    }
                }
            )

            CustomOutlinedTextField(
                value = viewModel.confirmPassword,
                onValueChange = { viewModel.confirmPassword = it },
                label = { Text(text = stringResource(R.string.form_confirm_password)) },
                modifier = Modifier
                    .fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                isError = confirmPasswordValidationState is ValidationUIState.Error,
                supportingText = {
                    if (confirmPasswordValidationState is ValidationUIState.Error) {
                        Text(stringResource(confirmPasswordValidationState.resourceId))
                    }
                }
            )

            Button(
                onClick = {
                    val isValid = viewModel.validateFields()

                    if (isValid) {
                        /* TODO: Go to home page" */
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = stringResource(R.string.signup_button))
            }


            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp),
            ) {

                val annotatedString = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                        append(stringResource(R.string.signup_login_question) + " ")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = linkText.color,
                            fontWeight = linkText.fontWeight,
                        )
                    ) {
                        append(stringResource(R.string.signup_login_action))
                    }
                }

                Text(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable {
                        navController.navigate("login")
                    },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun CompanyDropdown(
    selectedText: String,
    onValueChange: (String) -> Unit,
    companyValidationState: ValidationUIState,
    options: List<String>
) {
    var allowExpanded by remember { mutableStateOf(false) }
    val filteredOptions = options.filter { it.contains(selectedText, ignoreCase = true) }
    val expanded = selectedText.isNotEmpty() && allowExpanded && filteredOptions.isNotEmpty()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { allowExpanded = it },
    ) {
        CustomOutlinedTextField(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            value = selectedText,
            onValueChange = onValueChange,
            label = { Text("De qué empresa eres cliente?") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded,
                    modifier = Modifier.menuAnchor(MenuAnchorType.SecondaryEditable)
                )
            },
            isError = companyValidationState is ValidationUIState.Error,
            supportingText = {
                if (companyValidationState is ValidationUIState.Error) {
                    Text(stringResource(companyValidationState.resourceId))
                }
            }
        )

        ExposedDropdownMenu(
            modifier = Modifier.heightIn(max = 280.dp),
            expanded = expanded,
            onDismissRequest = { allowExpanded = false },
        ) {
            filteredOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        onValueChange(option)
                        allowExpanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    val navController = rememberNavController()
    ABCallTheme {
        SignUpScreen(navController)
    }
}
