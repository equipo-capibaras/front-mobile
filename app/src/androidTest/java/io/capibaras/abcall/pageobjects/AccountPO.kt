package io.capibaras.abcall.pageobjects

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import io.capibaras.abcall.ui.MainActivity

class AccountPO(private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) :
    PageObject(composeTestRule) {

    fun assertAccountScreenVisible() {
        waitUntilLoadingAndSnackbarDisappear()
        findExactlyOne(hasTestTag("account-avatar")).assertIsDisplayed()
    }

    fun logout() {
        findExactlyOne(
            hasTestTag("logout-button")
        ).performClick()

        findExactlyOne(
            hasTestTag("confirm-logout")
        ).performClick()
    }
}
