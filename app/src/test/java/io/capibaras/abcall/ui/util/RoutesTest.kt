package io.capibaras.abcall.ui.util

import androidx.navigation.NavController
import io.capibaras.abcall.R
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class RoutesTest {
    @MockK(relaxed = true)
    private lateinit var mockNavController: NavController

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `shouldShowBackButton returns true for routes that need back button`() {
        assertEquals(true, Routes.shouldShowBackButton(Routes.CREATE_INCIDENT))
        assertEquals(true, Routes.shouldShowBackButton(Routes.INCIDENT_DETAIL))
    }

    @Test
    fun `shouldShowBackButton returns false for routes that do not need back button`() {
        assertEquals(false, Routes.shouldShowBackButton(Routes.HOME))
        assertEquals(false, Routes.shouldShowBackButton(Routes.SIGN_UP))
        assertEquals(false, Routes.shouldShowBackButton(Routes.LOGIN))
        assertEquals(false, Routes.shouldShowBackButton(Routes.ACCOUNT))
    }

    @Test
    fun `getBackNavigationAction returns navigation to HOME for INCIDENT_DETAIL route`() {
        every { mockNavController.navigate(Routes.HOME, builder = any()) } just runs

        val backAction = Routes.getBackNavigationAction(Routes.INCIDENT_DETAIL, mockNavController)
        backAction.invoke()

        verify {
            mockNavController.navigate(Routes.HOME, builder = any())
        }
    }


    @Test
    fun `getBackNavigationAction returns popBackStack for other routes`() {
        every { mockNavController.popBackStack() } returns true

        val backAction = Routes.getBackNavigationAction(Routes.CREATE_INCIDENT, mockNavController)
        backAction.invoke()

        verify { mockNavController.popBackStack() }
    }

    @Test
    fun `getTitleForRoute returns correct title resource ID for known routes`() {
        assertEquals(R.string.requests_title, Routes.getTitleForRoute(Routes.HOME))
        assertEquals(R.string.account_title, Routes.getTitleForRoute(Routes.ACCOUNT))
        assertEquals(R.string.create_incident, Routes.getTitleForRoute(Routes.CREATE_INCIDENT))
        assertEquals(R.string.incident_detail, Routes.getTitleForRoute(Routes.INCIDENT_DETAIL))
    }

    @Test
    fun `getTitleForRoute returns null for unknown route`() {
        assertNull(Routes.getTitleForRoute("unknown_route"))
    }
}

