package io.capibaras.abcall

import io.capibaras.abcall.data.TokenManager
import io.capibaras.abcall.data.network.models.LoginResponse
import io.capibaras.abcall.data.repositories.AuthRepository
import io.capibaras.abcall.ui.viewmodels.ErrorUIState
import io.capibaras.abcall.ui.viewmodels.ValidationUIState
import io.capibaras.abcall.viewmodels.LoginViewModel
import io.capibaras.abcall.viewmodels.SharedViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkConstructor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private lateinit var viewModel: LoginViewModel
    private lateinit var sharedViewModel: SharedViewModel

    @MockK
    private lateinit var authRepository: AuthRepository

    @MockK
    private lateinit var tokenManager: TokenManager

    private val testDispatcher = StandardTestDispatcher()


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        sharedViewModel = SharedViewModel()
        viewModel = LoginViewModel(
            sharedViewModel = sharedViewModel,
            tokenManager = tokenManager,
            authRepository = authRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test empty fields return false`() {
        val isValid = viewModel.validateFields()

        assertFalse(isValid)
        assertEquals(
            ValidationUIState.Error(R.string.form_required),
            viewModel.emailValidationState
        )
        assertEquals(
            ValidationUIState.Error(R.string.form_required),
            viewModel.passwordValidationState
        )
    }

    @Test
    fun `test valid fields return true`() {

        viewModel.email = "johndoe@gmail.com"
        viewModel.password = "password123"

        val isValid = viewModel.validateFields()

        assertTrue(isValid)
        assertEquals(ValidationUIState.NoError, viewModel.emailValidationState)
        assertEquals(ValidationUIState.NoError, viewModel.passwordValidationState)
    }

    @Test
    fun `test login success`() = runTest {
        val mockResponse = mockk<Response<LoginResponse>> {
            every { isSuccessful } returns true
            every { body() } returns LoginResponse(token = "fake-token")
        }

        coEvery { authRepository.login("johndoe@gmail.com", "password123") } returns mockResponse
        coEvery { tokenManager.saveAuthToken("fake-token") } returns Unit

        viewModel.email = "johndoe@gmail.com"
        viewModel.password = "password123"

        var resultToken: String? = null

        viewModel.loginUser { token ->
            resultToken = token
        }

        advanceUntilIdle()

        coVerify(exactly = 1) { tokenManager.saveAuthToken("fake-token") }

        assertEquals("fake-token", resultToken)
        assertEquals(ErrorUIState.NoError, sharedViewModel.errorUIState)
    }

    @Test
    fun `test login failure with 401`() = runTest {
        val mockResponse = mockk<Response<LoginResponse>> {
            every { isSuccessful } returns false
            every { code() } returns 401
        }

        coEvery { authRepository.login("johndoe@gmail.com", "wrongpassword") } returns mockResponse

        viewModel.email = "johndoe@gmail.com"
        viewModel.password = "wrongpassword"

        var resultToken: String? = null

        viewModel.loginUser { token ->
            resultToken = token
        }

        advanceUntilIdle()

        assertEquals(null, resultToken)
        assertEquals(
            ErrorUIState.Error(R.string.error_incorrect_credentials),
            sharedViewModel.errorUIState
        )
    }

    @Test
    fun `test login failure with other status code`() = runTest {
        val mockResponse = mockk<Response<LoginResponse>> {
            every { isSuccessful } returns false
            every { code() } returns 500
            every { errorBody() } returns mockk {
                every { string() } returns "{\"message\": \"Server error occurred\"}"
            }
        }

        coEvery { authRepository.login("johndoe@gmail.com", "password123") } returns mockResponse

        viewModel.email = "johndoe@gmail.com"
        viewModel.password = "password123"

        var resultToken: String? = null

        mockkConstructor(JSONObject::class)
        every { anyConstructed<JSONObject>().getString(any()) } returns "Server error occurred"

        viewModel.loginUser { token ->
            resultToken = token
        }

        advanceUntilIdle()

        assertEquals(null, resultToken)
        assertEquals(
            ErrorUIState.Error(message = "Server error occurred"),
            sharedViewModel.errorUIState
        )
    }

    @Test
    fun `test login failure with null error body`() = runTest {
        val mockResponse = mockk<Response<LoginResponse>> {
            every { isSuccessful } returns false
            every { code() } returns 500
            every { errorBody() } returns null
        }

        coEvery { authRepository.login("johndoe@gmail.com", "password123") } returns mockResponse

        viewModel.email = "johndoe@gmail.com"
        viewModel.password = "password123"

        var resultToken: String? = null

        viewModel.loginUser { token ->
            resultToken = token
        }

        advanceUntilIdle()

        assertEquals(null, resultToken)
        assertEquals(ErrorUIState.Error(R.string.error_authenticate), sharedViewModel.errorUIState)
    }

    @Test
    fun `test login network failure`() = runTest {
        coEvery { authRepository.login(any(), any()) } throws IOException("Network error")

        viewModel.email = "johndoe@gmail.com"
        viewModel.password = "password123"

        var resultToken: String? = null

        viewModel.loginUser { token ->
            resultToken = token
        }

        advanceUntilIdle()

        assertEquals(null, resultToken)
        assertEquals(ErrorUIState.Error(R.string.error_network), sharedViewModel.errorUIState)
    }
}
