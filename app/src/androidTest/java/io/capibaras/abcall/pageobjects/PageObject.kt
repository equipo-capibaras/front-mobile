package io.capibaras.abcall.pageobjects

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import io.capibaras.abcall.ui.MainActivity

abstract class PageObject(private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) {
    fun findExactlyOne(matcher: SemanticsMatcher): SemanticsNodeInteraction {
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodes(matcher).fetchSemanticsNodes().size == 1
        }

        return composeTestRule.onNode(matcher)
    }

    fun findAtLeastOne(matcher: SemanticsMatcher): SemanticsNodeInteractionCollection {
        composeTestRule.waitUntil(5000) {
            val nodes = composeTestRule.onAllNodes(matcher).fetchSemanticsNodes()
            nodes.isNotEmpty()
        }

        return composeTestRule.onAllNodes(matcher)
    }

    fun fillInputField(testTag: String, text: String) {
        findExactlyOne(
            hasTestTag(testTag)
        ).performClick().performTextInput(text)
    }

    fun clickElementByTestTag(testTag: String) {
        findExactlyOne(hasTestTag(testTag)).performClick()
    }

    fun waitUntilLoadingAndSnackbarDisappear() {
        composeTestRule.waitUntil(timeoutMillis = 7000) {
            composeTestRule.onAllNodes(hasTestTag("full-loading")).fetchSemanticsNodes()
                .isEmpty() &&
                    composeTestRule.onAllNodes(hasTestTag("snackbar")).fetchSemanticsNodes()
                        .isEmpty()
        }
    }
}
