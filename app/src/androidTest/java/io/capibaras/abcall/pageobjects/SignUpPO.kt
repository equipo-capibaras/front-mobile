package io.capibaras.abcall.pageobjects

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import io.capibaras.abcall.ui.MainActivity

class SignUpPO(private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) :
    PageObject(composeTestRule) {

    fun fillName(name: String) {
        fillInputField("form-name", name)
    }

    fun fillEmail(email: String) {
        fillInputField("form-email", email)
    }

    fun fillPassword(password: String) {
        fillInputField("form-password", password)
    }

    fun fillConfirmPassword(confirmPassword: String) {
        fillInputField("form-confirm-password", confirmPassword)
    }

    fun selectCompany(index: Int) {
        clickElementByTestTag("form-company")
        clickElementByTestTag("form-company-$index")
    }

    fun submit() {
        clickElementByTestTag("signup-button")
    }
}