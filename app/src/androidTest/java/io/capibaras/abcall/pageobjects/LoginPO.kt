package io.capibaras.abcall.pageobjects

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import io.capibaras.abcall.ui.MainActivity

class LoginPO(private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) :
    PageObject(composeTestRule) {

    fun fillEmail(email: String) {
        fillInputField("form-email", email)
    }

    fun fillPassword(password: String) {
        fillInputField("form-password", password)
    }

    fun submit() {
        clickElementByTestTag("login-button")
    }

    fun navigateToSignUp() {
        clickElementByTestTag("navigate-signup")
    }

    fun assertLoginScreenVisible() {
        waitUntilLoadingAndSnackbarDisappear()
        findExactlyOne(hasTestTag("form-email")).assertIsDisplayed()
    }
}
