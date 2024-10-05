package com.misoux.abcall.ui.views

import android.content.Context
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
import androidx.compose.material3.Surface
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
import com.misoux.abcall.R
import com.misoux.abcall.ui.components.CustomOutlinedTextField
import com.misoux.abcall.ui.theme.ABCallTheme
import com.misoux.abcall.ui.theme.linkText

@ExperimentalMaterial3Api
@Composable
fun SignUpScreen(navController: NavController) {
    val context = LocalContext.current
    ABCallTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
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
                    text = context.getString(R.string.signup_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 30.dp, bottom = 40.dp)
                )

                var name by remember { mutableStateOf("") }
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var confirmPassword by remember { mutableStateOf("") }
                var selectedText by remember { mutableStateOf("") }

                var nameError by remember { mutableStateOf("") }
                var emailError by remember { mutableStateOf("") }
                var passwordError by remember { mutableStateOf("") }
                var confirmPasswordError by remember { mutableStateOf("") }
                var companyError by remember { mutableStateOf("") }

                // List of companies
                val options = listOf("Claro", "Movistar", "Tigo")
                val filteredOptions =
                    options.filter { it.contains(selectedText, ignoreCase = true) }
                var allowExpanded by remember { mutableStateOf(false) }
                val expanded =
                    selectedText.isNotEmpty() && allowExpanded && filteredOptions.isNotEmpty()

                CustomOutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = context.getString(R.string.form_name)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    isError = nameError.isNotEmpty(),
                    supportingText = { Text(nameError) }
                )

                CustomOutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = context.getString(R.string.form_email)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    isError = emailError.isNotEmpty(),
                    supportingText = { Text(emailError) }
                )

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
                        onValueChange = {
                            selectedText = it
                        },
                        label = { Text(context.getString(R.string.form_company)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded,
                                modifier = Modifier.menuAnchor(MenuAnchorType.SecondaryEditable)
                            )
                        },
                        isError = companyError.isNotEmpty(),
                        supportingText = { Text(companyError) }
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
                                    selectedText = option
                                    allowExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }

                CustomOutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = context.getString(R.string.form_password)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = passwordError.isNotEmpty(),
                    supportingText = { Text(passwordError) }
                )

                CustomOutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(text = context.getString(R.string.form_confirm_password)) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = confirmPasswordError.isNotEmpty(),
                    supportingText = {
                        if (confirmPasswordError.isNotEmpty()) {
                            Text(confirmPasswordError)
                        }
                    }
                )

                Button(
                    onClick = {
                        val validationResults = validateFields(
                            context = context,
                            name = name,
                            email = email,
                            company = selectedText,
                            password = password,
                            confirmPassword = confirmPassword
                        )

                        nameError = validationResults["nameError"] ?: ""
                        emailError = validationResults["emailError"] ?: ""
                        companyError = validationResults["companyError"] ?: ""
                        passwordError = validationResults["passwordError"] ?: ""
                        confirmPasswordError = validationResults["confirmPasswordError"] ?: ""

                        if (validationResults.values.all { it.isEmpty() }) {
                            /* TODO: Go to home page" */
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = context.getString(R.string.signup_button))
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
                            append(context.getString(R.string.signup_login_question) + " ")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = linkText.color,
                                fontWeight = linkText.fontWeight,
                            )
                        ) {
                            append(context.getString(R.string.signup_login_action))
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
}

fun validateFields(
    context: Context,
    name: String,
    email: String,
    company: String,
    password: String,
    confirmPassword: String
): Map<String, String> {
    val errors = mutableMapOf<String, String>()

    if (name.isBlank()) {
        errors["nameError"] = context.getString(R.string.form_required)
    }

    if (email.isBlank()) {
        errors["emailError"] = context.getString(R.string.form_required)
    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        errors["emailError"] = context.getString(R.string.form_invalid_email)
    }

    if (company.isBlank()) {
        errors["companyError"] = context.getString(R.string.form_required)
    }

    if (password.isBlank()) {
        errors["passwordError"] = context.getString(R.string.form_required)
    } else if (password.length < 8) {
        errors["passwordError"] = context.getString(R.string.form_password_length)
    }

    if (confirmPassword.isBlank()) {
        errors["confirmPasswordError"] = context.getString(R.string.form_required)
    } else if (confirmPassword != password) {
        errors["confirmPasswordError"] = context.getString(R.string.form_confirm_password_invalid)
    }

    return errors
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