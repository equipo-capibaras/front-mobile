package io.capibaras.abcall.ui.util

import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.navigation.NavController
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccessibilityUtilsTest {

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var accessibilityManager: AccessibilityManager

    @MockK
    private lateinit var navController: NavController

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        every { context.getSystemService(Context.ACCESSIBILITY_SERVICE) } returns accessibilityManager
        every { navController.navigate(any<String>()) } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `isAccessibilityEnabled should return true when accessibility is enabled`() {
        every { accessibilityManager.isEnabled } returns true

        val result = isAccessibilityEnabled(context)
        assertTrue(result)
    }

    @Test
    fun `navigateWithAccessibilityCheck should delay navigation when accessibility is enabled`() =
        runTest {
            every { accessibilityManager.isEnabled } returns true

            navigateWithAccessibilityCheck(
                navController = navController,
                destination = "destination",
                context = context,
                dispatcher = testDispatcher,
                delayMillis = 1500
            )

            advanceTimeBy(1500)
            advanceUntilIdle()

            val result = isAccessibilityEnabled(context)
            assertTrue(result)

            verify { navController.navigate("destination") }
        }

    @Test
    fun `navigateWithAccessibilityCheck should navigate immediately when accessibility is disabled`() =
        runTest {
            every { accessibilityManager.isEnabled } returns false

            navigateWithAccessibilityCheck(
                navController = navController,
                destination = "destination",
                context = context,
                dispatcher = testDispatcher,
                delayMillis = 1500
            )

            advanceUntilIdle()

            val result = isAccessibilityEnabled(context)
            assertFalse(result)

            verify { navController.navigate("destination") }
        }
}


