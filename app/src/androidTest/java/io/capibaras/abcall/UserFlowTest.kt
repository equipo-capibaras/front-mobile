package io.capibaras.abcall

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import io.capibaras.abcall.pageobjects.AccountPO
import io.capibaras.abcall.pageobjects.BottomNavBarPO
import io.capibaras.abcall.pageobjects.LoginPO
import io.capibaras.abcall.pageobjects.SignUpPO
import io.capibaras.abcall.ui.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserFlowTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun clearSharedPreferences() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE).edit().clear().apply()
    }

    @Test
    fun testUserRegistrationLoginAndLogoutFlow() {
        // Given I am on the Login screen
        val loginPO = LoginPO(composeTestRule)

        // When I navigate to the Sign Up screen
        loginPO.navigateToSignUp()

        // Then I should be on the Sign Up screen
        val signUpPO = SignUpPO(composeTestRule)

        // Given I am on the Sign Up screen
        // And I fill in the registration form
        val email = "testuser${System.currentTimeMillis()}@example.com" // Unique email
        signUpPO.fillName("Test User")
        signUpPO.fillEmail(email)
        signUpPO.selectCompany(0) // Select the first company
        signUpPO.fillPassword("password123")
        signUpPO.fillConfirmPassword("password123")

        // When I submit the registration form
        signUpPO.submit()

        // Then I should be redirected back to the Login screen
        loginPO.fillEmail(email)
        loginPO.fillPassword("password123")

        // When I submit the login form
        loginPO.submit()

        // Then I should be redirected to the Home screen
        // When I navigate to the Account screen using BottomNavBar
        val bottomNavBarPO = BottomNavBarPO(composeTestRule)
        bottomNavBarPO.navigateToAccount()

        // Then I should be on the Account screen
        val accountPO = AccountPO(composeTestRule)

        // When I click the logout button
        accountPO.logout()

        // Then I should be redirected to the Login screen again
        loginPO.assertLoginScreenVisible()
    }
}
