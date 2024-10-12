package io.capibaras.abcall

import io.capibaras.abcall.data.TokenManager
import io.capibaras.abcall.viewmodels.MainActivityViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainActivityViewModelTest {
    private lateinit var viewModel: MainActivityViewModel

    @MockK
    private lateinit var tokenManager: TokenManager

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `test user is logged in when token is available`() = runTest {
        coEvery { tokenManager.getAuthToken() } returns "valid_token"

        viewModel = MainActivityViewModel(tokenManager)

        advanceUntilIdle()

        assertEquals(true, viewModel.isUserLoggedIn)
        assertEquals(true, viewModel.isSessionChecked)

        verify(exactly = 1) { tokenManager.getAuthToken() }
    }

    @Test
    fun `test user is not logged in when token is null or empty`() = runTest {
        coEvery { tokenManager.getAuthToken() } returns ""

        viewModel = MainActivityViewModel(tokenManager)

        advanceUntilIdle()

        assertEquals(false, viewModel.isUserLoggedIn)
        assertEquals(true, viewModel.isSessionChecked)

        verify(exactly = 1) { tokenManager.getAuthToken() }
    }

    @Test
    fun `test user is not logged in when exception occurs`() = runTest {
        coEvery { tokenManager.getAuthToken() } throws Exception("Error getting token")

        viewModel = MainActivityViewModel(tokenManager)

        advanceUntilIdle()

        assertEquals(false, viewModel.isUserLoggedIn)
        assertEquals(true, viewModel.isSessionChecked)

        verify(exactly = 1) { tokenManager.getAuthToken() }
    }
}
