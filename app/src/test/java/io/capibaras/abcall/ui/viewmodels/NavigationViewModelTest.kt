package io.capibaras.abcall.ui.viewmodels

import io.capibaras.abcall.R
import io.capibaras.abcall.data.LogoutManager
import io.capibaras.abcall.data.LogoutState
import io.capibaras.abcall.data.TokenManager
import io.capibaras.abcall.data.repositories.UsersRepository
import io.capibaras.abcall.ui.util.StateMediator
import io.capibaras.abcall.ui.viewmodels.utils.ErrorUIState
import io.capibaras.abcall.ui.viewmodels.utils.SuccessUIState
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
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

    @MockK
    private lateinit var stateMediator: StateMediator

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

        every { stateMediator.isLoading } returns false
        every { stateMediator.setLoadingState(any()) } just runs
        every { stateMediator.errorUIState } returns ErrorUIState.NoError
        every { stateMediator.setErrorState(any()) } just runs
        every { stateMediator.successUIState } returns SuccessUIState.NoSuccess
        every { stateMediator.setSuccessState(any()) } just runs
    }

    @Test
    fun `test user is logged in when token is available`() = runTest {
        coEvery { tokenManager.getAuthToken() } returns "valid_token"

        viewModel =
            NavigationViewModel(stateMediator, usersRepository, tokenManager, logoutManager)

        advanceUntilIdle()

        assertEquals(true, viewModel.isUserLoggedIn)
        assertEquals(true, viewModel.isSessionChecked)

        verify(exactly = 1) { tokenManager.getAuthToken() }
    }

    @Test
    fun `test user is not logged in when token is null or empty`() = runTest {
        coEvery { tokenManager.getAuthToken() } returns ""

        viewModel =
            NavigationViewModel(stateMediator, usersRepository, tokenManager, logoutManager)

        advanceUntilIdle()

        assertEquals(false, viewModel.isUserLoggedIn)
        assertEquals(true, viewModel.isSessionChecked)

        verify(exactly = 1) { tokenManager.getAuthToken() }
    }

    @Test
    fun `test user is not logged in when exception occurs`() = runTest {
        coEvery { tokenManager.getAuthToken() } throws Exception("Error getting token")

        viewModel =
            NavigationViewModel(stateMediator, usersRepository, tokenManager, logoutManager)

        advanceUntilIdle()

        assertEquals(false, viewModel.isUserLoggedIn)
        assertEquals(true, viewModel.isSessionChecked)

        verify(exactly = 1) { tokenManager.getAuthToken() }
    }

    @Test
    fun `test logout event triggers user data deletion`() = runTest {
        viewModel =
            NavigationViewModel(stateMediator, usersRepository, tokenManager, logoutManager)

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
        every { stateMediator.successUIState } returns SuccessUIState.Success(R.string.success_logout)

        viewModel =
            NavigationViewModel(stateMediator, usersRepository, tokenManager, logoutManager)

        logoutStateFlow.emit(
            LogoutState(
                isLoggedOut = true,
                isExpiredToken = false,
                isManualLogout = true
            )
        )

        advanceUntilIdle()

        assertEquals(
            SuccessUIState.Success(R.string.success_logout),
            stateMediator.successUIState
        )
    }

    @Test
    fun `test expired token triggers error state`() = runTest {
        viewModel =
            NavigationViewModel(stateMediator, usersRepository, tokenManager, logoutManager)

        logoutStateFlow.emit(
            LogoutState(
                isLoggedOut = true,
                isExpiredToken = true,
                isManualLogout = false
            )
        )

        advanceUntilIdle()

        verify { stateMediator.setErrorState(ErrorUIState.Error(R.string.expired_token)) }
    }

}
