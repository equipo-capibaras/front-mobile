package io.capibaras.abcall.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
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
import io.capibaras.abcall.ui.components.DefaultTextField
import io.capibaras.abcall.ui.components.InitialPagesTitle
import io.capibaras.abcall.ui.components.TextFieldConfig
import io.capibaras.abcall.ui.components.TextFieldType
import io.capibaras.abcall.ui.theme.ABCallTheme
import io.capibaras.abcall.ui.theme.linkText
import io.capibaras.abcall.ui.viewmodels.LoginViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val emailValidationState = viewModel.emailValidationState
    val passwordValidationState = viewModel.passwordValidationState
    val pageTitle = stringResource(R.string.login_title)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        InitialPagesTitle(pageTitle)

        DefaultTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            validationState = emailValidationState,
            labelRes = R.string.form_email,
            config = TextFieldConfig(
                type = TextFieldType.EMAIL,
                testTag = "form-email"
            )
        )

        DefaultTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            validationState = passwordValidationState,
            labelRes = R.string.form_password,
            config = TextFieldConfig(
                type = TextFieldType.PASSWORD,
                testTag = "form-password"
            )
        )

        Button(
            onClick = {
                val isValid =
                    viewModel.validateFields()

                if (isValid) {
                    viewModel.loginUser(
                        onSuccess = { _ ->
                            navController.navigate("home")
                        }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp)
                .testTag("login-button"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = stringResource(R.string.login_button))
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
                    append(stringResource(R.string.login_signup_question) + " ")
                }
                withStyle(
                    style = SpanStyle(
                        color = linkText.color,
                        fontWeight = linkText.fontWeight,
                    )
                ) {
                    append(stringResource(R.string.login_signup_action))
                }
            }

            Text(
                text = annotatedString,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .testTag("navigate-signup")
                    .clickable {
                        navController.navigate("signup")
                    },
                textAlign = TextAlign.Center
            )
        }
    }

}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    ABCallTheme {
        LoginScreen(navController)
    }
}
