package io.capibaras.abcall.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.capibaras.abcall.R
import io.capibaras.abcall.ui.components.CustomOutlinedTextField
import io.capibaras.abcall.ui.components.DefaultTextField
import io.capibaras.abcall.ui.components.InitialPagesTitle
import io.capibaras.abcall.ui.components.TextFieldType
import io.capibaras.abcall.ui.theme.ABCallTheme
import io.capibaras.abcall.ui.theme.linkText
import io.capibaras.abcall.ui.viewmodels.SignUpViewModel
import io.capibaras.abcall.ui.viewmodels.ValidationUIState
import org.koin.androidx.compose.koinViewModel

@ExperimentalMaterial3Api
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = koinViewModel()
) {
    val pageTitle = stringResource(R.string.signup_title)
    val nameValidationState = viewModel.nameValidationState
    val emailValidationState = viewModel.emailValidationState
    val companyValidationState = viewModel.companyValidationState
    val passwordValidationState = viewModel.passwordValidationState
    val confirmPasswordValidationState = viewModel.confirmPasswordValidationState

    val companies = viewModel.companies.map { it.name }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        InitialPagesTitle(pageTitle)

        DefaultTextField(
            value = viewModel.name,
            onValueChange = { viewModel.name = it },
            validationState = nameValidationState,
            labelRes = R.string.form_name,
            testTag = "form-name"
        )

        DefaultTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            validationState = emailValidationState,
            labelRes = R.string.form_email,
            type = TextFieldType.EMAIL,
            testTag = "form-email"
        )

        CompanyDropdown(
            selectedText = viewModel.company,
            onValueChange = { viewModel.company = it },
            companyValidationState = companyValidationState,
            options = companies
        )

        DefaultTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            validationState = passwordValidationState,
            labelRes = R.string.form_password,
            type = TextFieldType.PASSWORD,
            testTag = "form-password"
        )

        DefaultTextField(
            value = viewModel.confirmPassword,
            onValueChange = { viewModel.confirmPassword = it },
            validationState = confirmPasswordValidationState,
            labelRes = R.string.form_confirm_password,
            type = TextFieldType.PASSWORD,
            testTag = "form-confirm-password"
        )

        Button(
            onClick = {
                val isValid = viewModel.validateFields()

                if (isValid) {
                    viewModel.createUser(
                        onSuccess = { navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp)
                .testTag("signup-button"),
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
                modifier = Modifier
                    .testTag("navigate-login")
                    .clickable {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                textAlign = TextAlign.Center
            )
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
    val expanded = allowExpanded && filteredOptions.isNotEmpty()
    val noOptionsText = stringResource(R.string.form_no_company_options)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { allowExpanded = it },
    ) {
        CustomOutlinedTextField(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .testTag("form-company")
                .semantics {
                    if (filteredOptions.isEmpty()) {
                        contentDescription = noOptionsText
                        liveRegion = LiveRegionMode.Assertive
                    }
                },
            value = selectedText,
            onValueChange = onValueChange,
            label = { Text(stringResource(R.string.form_company)) },
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
            filteredOptions.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        onValueChange(option)
                        allowExpanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    modifier = Modifier.testTag("form-company-$index")
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
