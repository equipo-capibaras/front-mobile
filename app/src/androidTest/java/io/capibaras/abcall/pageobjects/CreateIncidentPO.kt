package io.capibaras.abcall.pageobjects

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import io.capibaras.abcall.ui.MainActivity

class CreateIncidentPO(private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) :
    PageObject(composeTestRule) {

    fun assertCreateIncidentScreenVisible() {
        waitUntilLoadingAndSnackbarDisappear()
        findExactlyOne(hasTestTag("create-incident-form-description")).assertIsDisplayed()
    }

    fun fillIncidentName(name: String) {
        fillInputField("form-name", name)
    }

    fun fillIncidentDescription(description: String) {
        fillInputField("form-description", description)
    }

    fun submitIncident() {
        clickElementByTestTag("create-incident-button")
    }

}