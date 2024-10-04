package com.misoux.abcall.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    ABCallTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
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
                    text = context.getString(R.string.login_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 30.dp, bottom = 40.dp)
                )

                var email by remember { mutableStateOf("") }
                CustomOutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = context.getString(R.string.login_email)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )

                var password by remember { mutableStateOf("") }
                CustomOutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = context.getString(R.string.login_password)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    visualTransformation = PasswordVisualTransformation()
                )

                Button(
                    onClick = { /* Acción de inicio de sesión */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = context.getString(R.string.login_button))
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
                            append(context.getString(R.string.login_signup_question) + " ")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = linkText.color,
                                fontWeight = linkText.fontWeight,
                            )
                        ) {
                            append(context.getString(R.string.login_signup_action))
                        }
                    }

                    Text(
                        text = annotatedString,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable {
                            // Acción de registro
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
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