package io.capibaras.abcall

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import io.capibaras.abcall.pageobjects.AccountPO
import io.capibaras.abcall.pageobjects.BottomNavBarPO
import io.capibaras.abcall.pageobjects.CreateIncidentPO
import io.capibaras.abcall.pageobjects.HomePO
import io.capibaras.abcall.pageobjects.IncidentDetailPO
import io.capibaras.abcall.pageobjects.IncidentsPO
import io.capibaras.abcall.pageobjects.LoginPO
import io.capibaras.abcall.pageobjects.SignUpPO
import io.capibaras.abcall.ui.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserFlowTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var loginPO: LoginPO
    private lateinit var signUpPO: SignUpPO
    private lateinit var accountPO: AccountPO
    private lateinit var homeScreenPO: HomePO
    private lateinit var incidentsScreenPO: IncidentsPO
    private lateinit var bottomNavBarPO: BottomNavBarPO
    private lateinit var createIncidentScreenPO: CreateIncidentPO
    private lateinit var detailIncidentPO: IncidentDetailPO
    private val email = "testuser${System.currentTimeMillis()}@example.com"
    private var password = "password123"

    @Before
    fun setUp() {
        clearSharedPreferences()
        loginPO = LoginPO(composeTestRule)
        signUpPO = SignUpPO(composeTestRule)
        homeScreenPO = HomePO(composeTestRule)
        incidentsScreenPO = IncidentsPO(composeTestRule)
        accountPO = AccountPO(composeTestRule)
        bottomNavBarPO = BottomNavBarPO(composeTestRule)
        createIncidentScreenPO = CreateIncidentPO(composeTestRule)
        detailIncidentPO = IncidentDetailPO(composeTestRule)
    }

    @Before
    fun clearSharedPreferences() {
        println("Email $email")
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE).edit().clear().apply()
    }

    @Test
    fun testUserRegistrationLoginAndLogoutFlow() {
        // Given I am on the Login screen
        loginPO.assertLoginScreenVisible()

        // When I navigate to the Sign Up screen
        loginPO.navigateToSignUp()

        // Then I should be on the Sign Up screen
        signUpPO.assertSignUpScreenVisible()

        // Given I am on the Sign Up screen
        // And I fill in the registration form
        signUpPO.fillName("Test User")
        signUpPO.fillEmail(email)
        signUpPO.selectCompany(0) // Select the first company
        signUpPO.fillPassword(password)
        signUpPO.fillConfirmPassword(password)

        // When I submit the registration form
        signUpPO.submit()

        // Then I should be redirected back to the Login screen
        loginPO.assertLoginScreenVisible()
        loginPO.fillEmail(email)
        loginPO.fillPassword(password)

        // When I submit the login form
        loginPO.submit()

        // Then I should be redirected to the Home screen
        homeScreenPO.assertHomeScreenVisible()

        // When I navigate to the Account screen using BottomNavBar
        bottomNavBarPO.navigateToAccount()

        // Then I should be on the Account screen
        accountPO.assertAccountScreenVisible()

        // When I click the logout button
        accountPO.logout()

        // Then I should be redirected to the Login screen again
        loginPO.assertLoginScreenVisible()
    }

    @Test
    fun loginCreateIncidentAndVerifyInList() {
        val email = "juan.rodriguez@example.com"
        val password = "juan123"
        // Given: I am on the Login screen
        loginPO.assertLoginScreenVisible()

        // When: I log in with valid credentials
        loginPO.fillEmail(email)
        loginPO.fillPassword(password)
        loginPO.submit()

        // Then: I should be redirected to the Home screen
        homeScreenPO.assertHomeScreenVisible()

        // Given: I am on the Home screen
        // When: I navigate to the Create Incident screen
        homeScreenPO.goToCreateIncidentScreen()

        // Then: I should be on the Create Incident screen
        createIncidentScreenPO.assertCreateIncidentScreenVisible()

        // Given: I am on the Create Incident screen
        // And: I fill in the incident details
        val incidentTitle = "Test Incident ${System.currentTimeMillis()}"
        val incidentDescription = "Description for test incident"
        createIncidentScreenPO.fillIncidentName(incidentTitle)
        createIncidentScreenPO.fillIncidentDescription(incidentDescription)
        createIncidentScreenPO.submitIncident()


        // Then: I should be redirected to the Incident Detail screen
        detailIncidentPO.assertIncidentDetailScreenVisible()
        detailIncidentPO.incidentTitle(incidentTitle)
        detailIncidentPO.incidentDescription(incidentDescription)

        // When: I navigate back to the Home screen
        bottomNavBarPO.navigateToHome()

        // Then: I should be back on the Incidents screen with the new incident visible
        homeScreenPO.assertHomeScreenVisible()
        incidentsScreenPO.findIncidentCard(incidentTitle)
    }

}
