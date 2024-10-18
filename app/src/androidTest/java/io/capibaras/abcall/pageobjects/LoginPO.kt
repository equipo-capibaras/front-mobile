package io.capibaras.abcall.pageobjects

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import io.capibaras.abcall.ui.MainActivity

class LoginPO(private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) :
    PageObject(composeTestRule) {

    fun fillEmail(email: String) {
        findExactlyOne(
            hasTestTag("form-email")
        ).performClick().performTextInput(email)
    }

    fun fillPassword(password: String) {
        findExactlyOne(
            hasTestTag("form-password")
        ).performClick().performTextInput(password)
    }

    fun submit() {
        findExactlyOne(
            hasTestTag("login-button")
        ).performClick()
    }

    fun navigateToSignUp() {
        findExactlyOne(hasTestTag("navigate-signup")).performClick()
    }

    fun assertLoginScreenVisible() {
        findExactlyOne(hasTestTag("form-email")).assertIsDisplayed()
    }
}
