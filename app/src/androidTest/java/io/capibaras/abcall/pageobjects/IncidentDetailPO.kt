package io.capibaras.abcall.pageobjects

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import io.capibaras.abcall.ui.MainActivity

class IncidentDetailPO(private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) :
    PageObject(composeTestRule) {

    fun assertIncidentDetailScreenVisible() {
        waitUntilLoadingAndSnackbarDisappear()
        findExactlyOne(hasTestTag("incident-detail-title")).assertIsDisplayed()
    }
}