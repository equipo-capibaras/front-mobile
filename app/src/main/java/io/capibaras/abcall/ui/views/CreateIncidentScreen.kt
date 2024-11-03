package io.capibaras.abcall.ui.views

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.capibaras.abcall.R
import io.capibaras.abcall.ui.components.DefaultTextField
import io.capibaras.abcall.ui.components.TextFieldConfig
import io.capibaras.abcall.ui.components.TextFieldType
import io.capibaras.abcall.ui.viewmodels.CreateIncidentViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateIncidentScreen(
    navController: NavController,
    viewModel: CreateIncidentViewModel = koinViewModel(),
) {
    val nameValidationState = viewModel.nameValidationState
    val descriptionValidationState = viewModel.descriptionValidationState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.create_incident_description),
            modifier = Modifier
                .padding(bottom = 24.dp)
        )

        DefaultTextField(
            value = viewModel.name,
            onValueChange = { viewModel.name = it },
            validationState = nameValidationState,
            labelRes = R.string.form_name,
            config = TextFieldConfig(
                type = TextFieldType.TEXT,
                testTag = "form-name"
            )
        )

        DefaultTextField(
            value = viewModel.description,
            onValueChange = { viewModel.description = it },
            validationState = descriptionValidationState,
            labelRes = R.string.form_description,
            config = TextFieldConfig(
                type = TextFieldType.TEXT,
                testTag = "form-description",
                minLines = 6,
                maxLines = 6
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    val isValid =
                        viewModel.validateFields()

                    if (isValid) {
                        viewModel.createIncident(
                            onSuccess = { incidentId ->
                                navController.navigate("create-incident/${incidentId}")
                            }
                        )
                    }
                },
                modifier = Modifier
                    .padding(vertical = 40.dp)
                    .testTag("create-incident-button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = stringResource(R.string.create_incident))
            }
        }
    }
}