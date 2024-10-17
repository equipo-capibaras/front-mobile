package io.capibaras.abcall

import io.capibaras.abcall.data.LogoutManager
import io.capibaras.abcall.data.LogoutState
import io.capibaras.abcall.data.TokenManager
import io.capibaras.abcall.data.repositories.UsersRepository
import io.capibaras.abcall.ui.viewmodels.ErrorUIState
import io.capibaras.abcall.ui.viewmodels.SuccessUIState
import io.capibaras.abcall.viewmodels.NavigationViewModel
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationViewModelTest {
    private lateinit var viewModel: NavigationViewModel
    private val logoutStateFlow = MutableStateFlow(
        LogoutState(
            isLoggedOut = false,
            isExpiredToken = false,
            isManualLogout = false
        )
    )

    @MockK
    private lateinit var tokenManager: TokenManager

    @MockK
    private lateinit var logoutManager: LogoutManager

    @MockK
    private lateinit var usersRepository: UsersRepository

    private val testDispatcher = StandardTestDispatcher()


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        coEvery { logoutManager.logoutState } returns logoutStateFlow
        coEvery { tokenManager.clearAuthToken() } just Runs
        coEvery { usersRepository.deleteUsers() } just Runs
        coEvery { logoutManager.resetLogoutState() } just Runs
    }

    @Test
    fun `test user is logged in when token is available`() = runTest {
        coEvery { tokenManager.getAuthToken() } returns "valid_token"

        viewModel = NavigationViewModel(usersRepository, tokenManager, logoutManager)

        advanceUntilIdle()

        assertEquals(true, viewModel.isUserLoggedIn)
        assertEquals(true, viewModel.isSessionChecked)

        verify(exactly = 1) { tokenManager.getAuthToken() }
    }

    @Test
    fun `test user is not logged in when token is null or empty`() = runTest {
        coEvery { tokenManager.getAuthToken() } returns ""

        viewModel = NavigationViewModel(usersRepository, tokenManager, logoutManager)

        advanceUntilIdle()

        assertEquals(false, viewModel.isUserLoggedIn)
        assertEquals(true, viewModel.isSessionChecked)

        verify(exactly = 1) { tokenManager.getAuthToken() }
    }

    @Test
    fun `test user is not logged in when exception occurs`() = runTest {
        coEvery { tokenManager.getAuthToken() } throws Exception("Error getting token")

        viewModel = NavigationViewModel(usersRepository, tokenManager, logoutManager)

        advanceUntilIdle()

        assertEquals(false, viewModel.isUserLoggedIn)
        assertEquals(true, viewModel.isSessionChecked)

        verify(exactly = 1) { tokenManager.getAuthToken() }
    }

    @Test
    fun `test logout event triggers user data deletion`() = runTest {
        viewModel = NavigationViewModel(usersRepository, tokenManager, logoutManager)

        // Emit a logout event
        logoutStateFlow.emit(
            LogoutState(
                isLoggedOut = true,
                isExpiredToken = false,
                isManualLogout = false
            )
        )

        advanceUntilIdle()

        coVerify { tokenManager.clearAuthToken() }
        coVerify { usersRepository.deleteUsers() }
        coVerify { logoutManager.resetLogoutState() }

        assertEquals(true, viewModel.redirectToLogin)
    }

    @Test
    fun `test manual logout triggers success state`() = runTest {
        viewModel = NavigationViewModel(usersRepository, tokenManager, logoutManager)

        logoutStateFlow.emit(
            LogoutState(
                isLoggedOut = true,
                isExpiredToken = false,
                isManualLogout = true
            )
        )

        advanceUntilIdle()

        assertEquals(SuccessUIState.Success(R.string.success_logout), viewModel.successUIState)
    }

    @Test
    fun `test clearErrorUIState after token expiration error`() = runTest {
        coEvery { logoutManager.logoutState } returns MutableStateFlow(
            LogoutState(isLoggedOut = false, isExpiredToken = true, isManualLogout = false)
        )

        viewModel = NavigationViewModel(usersRepository, tokenManager, logoutManager)

        advanceUntilIdle()

        assertEquals(ErrorUIState.Error(R.string.expired_token), viewModel.errorUIState)
        viewModel.clearErrorUIState()
        assertEquals(ErrorUIState.NoError, viewModel.errorUIState)
    }

    @Test
    fun `test clearSuccessUIState after successful manual logout`() = runTest {
        coEvery { logoutManager.logoutState } returns MutableStateFlow(
            LogoutState(isLoggedOut = true, isExpiredToken = false, isManualLogout = true)
        )

        viewModel = NavigationViewModel(usersRepository, tokenManager, logoutManager)

        advanceUntilIdle()

        assertEquals(SuccessUIState.Success(R.string.success_logout), viewModel.successUIState)
        viewModel.clearSuccessUIState()
        assertEquals(SuccessUIState.NoSuccess, viewModel.successUIState)
    }
}
