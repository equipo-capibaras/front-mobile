package io.capibaras.abcall.pageobjects

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import io.capibaras.abcall.ui.MainActivity

class SignUpPO(private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) :
    PageObject(composeTestRule) {

    fun fillName(name: String) {
        findExactlyOne(
            hasTestTag("form-name")
        ).performClick().performTextInput(name)
    }

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

    fun fillConfirmPassword(confirmPassword: String) {
        findExactlyOne(
            hasTestTag("form-confirm-password")
        ).performClick().performTextInput(confirmPassword)
    }

    fun selectCompany(index: Int) {
        findExactlyOne(
            hasTestTag("form-company")
        ).performClick()
        findExactlyOne(
            hasTestTag("form-company-$index")
        ).performClick()
    }

    fun submit() {
        findExactlyOne(
            hasTestTag("signup-button")
        ).performClick()
    }
}
