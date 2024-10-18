package io.capibaras.abcall

import io.capibaras.abcall.data.LogoutManager
import io.capibaras.abcall.data.database.models.User
import io.capibaras.abcall.data.repositories.UsersRepository
import io.capibaras.abcall.ui.viewmodels.ErrorUIState
import io.capibaras.abcall.viewmodels.AccountViewModel
import io.capibaras.abcall.viewmodels.SharedViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
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
class AccountViewModelTest {
    @MockK
    private lateinit var logoutManager: LogoutManager

    @MockK
    private lateinit var usersRepository: UsersRepository

    private lateinit var viewModel: AccountViewModel
    private lateinit var sharedViewModel: SharedViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        sharedViewModel = SharedViewModel()
    }

    @Test
    fun `test getUserInfo sets user when repository returns user`() = runTest {
        val mockUser = User("user-id", "client-id", "Test User", "test@test.com", null)
        coEvery { usersRepository.getUserInfo() } returns mockUser

        viewModel = AccountViewModel(sharedViewModel, logoutManager, usersRepository)

        advanceUntilIdle()

        assertEquals(mockUser, viewModel.user)
        assertEquals(false, sharedViewModel.isLoading)

        coVerify(exactly = 1) { usersRepository.getUserInfo() }
    }

    @Test
    fun `test getUserInfo sets error when exception occurs`() = runTest {
        coEvery { usersRepository.getUserInfo() } throws Exception("Error fetching user info")

        viewModel = AccountViewModel(sharedViewModel, logoutManager, usersRepository)

        advanceUntilIdle()

        assertEquals(
            ErrorUIState.Error(message = "Error fetching user info"),
            sharedViewModel.errorUIState
        )
        assertEquals(false, sharedViewModel.isLoading)

        coVerify(exactly = 1) { usersRepository.getUserInfo() }
    }

    @Test
    fun `test logout calls logoutManager`() = runTest {
        coEvery { logoutManager.logout(isManual = true) } just runs

        viewModel = AccountViewModel(sharedViewModel, logoutManager, usersRepository)

        viewModel.logout()

        advanceUntilIdle()

        coVerify(exactly = 1) { logoutManager.logout(isManual = true) }
    }
}
