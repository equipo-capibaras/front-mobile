package io.capibaras.abcall.ui.viewmodels

import io.capibaras.abcall.R
import io.capibaras.abcall.data.database.models.Company
import io.capibaras.abcall.data.database.models.User
import io.capibaras.abcall.data.repositories.CompanyRepository
import io.capibaras.abcall.data.repositories.UsersRepository
import io.capibaras.abcall.ui.util.StateMediator
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.runs
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
class SignUpViewModelTest {
    private lateinit var viewModel: SignUpViewModel

    @MockK
    private lateinit var stateMediator: StateMediator

    @MockK
    private lateinit var companyRepository: CompanyRepository

    @MockK
    private lateinit var usersRepository: UsersRepository

    private val testDispatcher = StandardTestDispatcher()
    private val companies = listOf(
        Company(
            name = "Company XYZ",
            id = "Company_XYZ"
        )
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        coEvery { companyRepository.getCompanies(any()) } returns companies
        every { stateMediator.isLoading } returns false
        every { stateMediator.setLoadingState(any()) } just runs
        every { stateMediator.errorUIState } returns ErrorUIState.NoError
        every { stateMediator.setErrorState(any()) } just runs
        every { stateMediator.successUIState } returns SuccessUIState.NoSuccess
        every { stateMediator.setSuccessState(any()) } just runs

        viewModel = SignUpViewModel(stateMediator, companyRepository, usersRepository)
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
            viewModel.nameValidationState
        )
        assertEquals(
            ValidationUIState.Error(R.string.form_required),
            viewModel.emailValidationState
        )
        assertEquals(
            ValidationUIState.Error(R.string.form_required),
            viewModel.companyValidationState
        )
        assertEquals(
            ValidationUIState.Error(R.string.form_required),
            viewModel.passwordValidationState
        )
        assertEquals(
            ValidationUIState.Error(R.string.form_required),
            viewModel.confirmPasswordValidationState
        )
    }

    @Test
    fun `test name too long returns false`() {
        viewModel.name = "Maximilian Alexander Benedict Archibald Montgomery the Fourth"

        val isValid = viewModel.validateFields()

        assertFalse(isValid)
        assertEquals(
            ValidationUIState.Error(R.string.form_60_length),
            viewModel.nameValidationState
        )
    }

    @Test
    fun `test invalid email returns false`() {
        viewModel.email = "invalid-email"

        val isValid = viewModel.validateFields()

        assertFalse(isValid)
        assertEquals(
            ValidationUIState.Error(R.string.form_invalid_email),
            viewModel.emailValidationState
        )
    }

    @Test
    fun `test email too long returns false`() {
        viewModel.email = "very.long.email.address.with.lots.of.words.for.testing.a@example.com"

        val isValid = viewModel.validateFields()

        assertFalse(isValid)
        assertEquals(
            ValidationUIState.Error(R.string.form_60_length),
            viewModel.emailValidationState
        )
    }

    @Test
    fun `test password too short returns false`() {
        viewModel.password = "short"
        viewModel.confirmPassword = "short"

        val isValid = viewModel.validateFields()

        assertFalse(isValid)
        assertEquals(
            ValidationUIState.Error(R.string.form_password_length),
            viewModel.passwordValidationState
        )
    }

    @Test
    fun `test passwords do not match returns false`() {
        viewModel.password = "password123"
        viewModel.confirmPassword = "password456"

        val isValid = viewModel.validateFields()

        assertFalse(isValid)
        assertEquals(
            ValidationUIState.Error(R.string.form_confirm_password_invalid),
            viewModel.confirmPasswordValidationState
        )
    }


    @Test
    fun `test valid fields return true`() = runTest {
        advanceUntilIdle()

        viewModel.name = "John Doe"
        viewModel.email = "johndoe@gmail.com"
        viewModel.password = "password123"
        viewModel.confirmPassword = "password123"
        viewModel.company = companies[0].name

        val isValid = viewModel.validateFields()

        assertTrue(isValid)
        assertEquals(ValidationUIState.NoError, viewModel.nameValidationState)
        assertEquals(ValidationUIState.NoError, viewModel.emailValidationState)
        assertEquals(ValidationUIState.NoError, viewModel.companyValidationState)
        assertEquals(ValidationUIState.NoError, viewModel.passwordValidationState)
        assertEquals(ValidationUIState.NoError, viewModel.confirmPasswordValidationState)
    }

    @Test
    fun `test getCompanies success`() = runTest {
        advanceUntilIdle()
        assertFalse(stateMediator.isLoading)
        assertEquals(companies, viewModel.companies)
    }

    @Test
    fun `test getCompanies network failure`() = runTest {
        coEvery { companyRepository.getCompanies() } throws IOException("Network error")
        every { stateMediator.errorUIState } returns ErrorUIState.Error(R.string.error_network)

        viewModel = SignUpViewModel(stateMediator, companyRepository, usersRepository)

        advanceUntilIdle()

        assertEquals(ErrorUIState.Error(R.string.error_network), stateMediator.errorUIState)
    }

    @Test
    fun `test getCompanies unknown failure`() = runTest {
        coEvery { companyRepository.getCompanies() } throws Exception("Error")
        every { stateMediator.errorUIState } returns ErrorUIState.Error(R.string.error_get_companies)

        viewModel = SignUpViewModel(stateMediator, companyRepository, usersRepository)

        advanceUntilIdle()

        assertEquals(ErrorUIState.Error(R.string.error_get_companies), stateMediator.errorUIState)
    }

    @Test
    fun `test createUser success`() = runTest {
        val mockResponse = mockk<Response<User>> {
            every { isSuccessful } returns true
            every { body() } returns User(
                id = "user-id",
                clientId = companies[0].id,
                name = "Juan",
                email = "email@gmail.com",
                clientName = null
            )
        }

        coEvery {
            usersRepository.createUser(
                any(), any(), any(), any()
            )
        } returns mockResponse
        every { stateMediator.successUIState } returns SuccessUIState.Success(R.string.success_create_user)

        viewModel.name = "Juan"
        viewModel.email = "email@gmail.com"
        viewModel.password = "password123"
        viewModel.company = companies[0].name

        viewModel.createUser {}

        advanceUntilIdle()

        assertEquals(ErrorUIState.NoError, stateMediator.errorUIState)
        assertEquals(
            SuccessUIState.Success(R.string.success_create_user),
            stateMediator.successUIState
        )
    }


    @Test
    fun `test createUser network failure`() = runTest {
        coEvery {
            usersRepository.createUser(any(), any(), any(), any())
        } throws IOException("Network error")
        every { stateMediator.errorUIState } returns ErrorUIState.Error(R.string.error_network)

        viewModel.name = "Juan"
        viewModel.email = "email@gmail.com"
        viewModel.password = "password123"
        viewModel.company = companies[0].name

        viewModel.createUser {}

        advanceUntilIdle()

        assertEquals(ErrorUIState.Error(R.string.error_network), stateMediator.errorUIState)
    }

    @Test
    fun `test createUser email already exists`() = runTest {
        val mockResponse = mockk<Response<User>> {
            every { isSuccessful } returns false
            every { code() } returns 409
        }

        coEvery {
            usersRepository.createUser(
                any(), any(), any(), any()
            )
        } returns mockResponse
        every { stateMediator.errorUIState } returns ErrorUIState.Error(R.string.error_email_exist)

        viewModel.name = "Juan"
        viewModel.email = "email@gmail.com"
        viewModel.password = "password123"
        viewModel.company = companies[0].name

        viewModel.createUser {}

        advanceUntilIdle()

        assertEquals(ErrorUIState.Error(R.string.error_email_exist), stateMediator.errorUIState)
    }

    @Test
    fun `test createUser  failure with other status code`() = runTest {
        val mockResponse = mockk<Response<User>> {
            every { isSuccessful } returns false
            every { code() } returns 500
            every { errorBody() } returns mockk {
                every { string() } returns "{\"message\": \"Server error occurred\"}"
            }
        }

        coEvery {
            usersRepository.createUser(
                any(), any(), any(), any()
            )
        } returns mockResponse
        every { stateMediator.errorUIState } returns ErrorUIState.Error(message = "Server error occurred")

        viewModel.name = "Juan"
        viewModel.email = "email@gmail.com"
        viewModel.password = "password123"
        viewModel.company = companies[0].name

        mockkConstructor(JSONObject::class)
        every { anyConstructed<JSONObject>().getString(any()) } returns "Server error occurred"

        viewModel.createUser {}

        advanceUntilIdle()

        assertEquals(
            ErrorUIState.Error(message = "Server error occurred"),
            stateMediator.errorUIState
        )
    }

    @Test
    fun `test createUser unknown failure`() = runTest {
        coEvery {
            usersRepository.createUser(any(), any(), any(), any())
        } throws Exception("Error")
        every { stateMediator.errorUIState } returns ErrorUIState.Error(R.string.error_create_user)


        viewModel.name = "Juan"
        viewModel.email = "email@gmail.com"
        viewModel.password = "password123"
        viewModel.company = companies[0].name

        viewModel.createUser {}

        advanceUntilIdle()

        assertEquals(ErrorUIState.Error(R.string.error_create_user), stateMediator.errorUIState)
    }
}
