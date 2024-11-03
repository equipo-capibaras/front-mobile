package io.capibaras.abcall.ui.viewmodels

import io.capibaras.abcall.R
import io.capibaras.abcall.data.network.models.Channel
import io.capibaras.abcall.data.network.models.CreateIncidentResponse
import io.capibaras.abcall.data.repositories.IncidentsRepository
import io.capibaras.abcall.data.repositories.RepositoryError
import io.capibaras.abcall.ui.util.StateMediator
import io.capibaras.abcall.ui.viewmodels.utils.ErrorUIState
import io.capibaras.abcall.ui.viewmodels.utils.SuccessUIState
import io.capibaras.abcall.ui.viewmodels.utils.ValidationUIState
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateIncidentViewModelTest {

    private lateinit var viewModel: CreateIncidentViewModel

    @MockK
    private lateinit var incidentsRepository: IncidentsRepository

    @MockK
    private lateinit var stateMediator: StateMediator

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        every { stateMediator.isLoading } returns false
        every { stateMediator.setLoadingState(any()) } just runs
        every { stateMediator.setErrorState(any()) } just runs
        every { stateMediator.setSuccessState(any()) } just runs
        every { stateMediator.clearErrorUIState() } just runs

        viewModel = CreateIncidentViewModel(stateMediator, incidentsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `validateFields should return false if name is empty`() {
        viewModel.name = ""
        viewModel.description = "Valid Description"

        val isValid = viewModel.validateFields()

        assertFalse(isValid)
        val validationState = viewModel.nameValidationState as ValidationUIState.Error
        assertEquals(R.string.form_required, validationState.resourceId)
    }

    @Test
    fun `validateFields should return false if description exceeds limit`() {
        viewModel.name = "Valid Name"
        viewModel.description = "D".repeat(1001)

        val isValid = viewModel.validateFields()

        assertFalse(isValid)
        val validationDescriptionState =
            viewModel.descriptionValidationState as ValidationUIState.Error
        assertEquals(R.string.form_1000_length, validationDescriptionState.resourceId)
    }


    @Test
    fun `validateFields should return true if name and description are valid`() {
        viewModel.name = "Valid Name"
        viewModel.description = "Valid Description"

        val isValid = viewModel.validateFields()

        assertTrue(isValid)
        assertTrue(viewModel.nameValidationState is ValidationUIState.NoError)
        assertTrue(viewModel.descriptionValidationState is ValidationUIState.NoError)
    }

    @Test
    fun `createIncident should set success state on successful incident creation`() = runTest {
        val mockResponse = CreateIncidentResponse(
            "1",
            "clientId",
            "name",
            Channel.MOBILE,
            "reportedBy",
            "createdBy",
            "assignedTo"
        )
        coEvery { incidentsRepository.createIncident(any(), any()) } returns Result.success(
            mockResponse
        )

        var callbackIncidentId: String? = null
        viewModel.createIncident { id -> callbackIncidentId = id }
        advanceUntilIdle()

        assertEquals("1", callbackIncidentId)
        coVerify { stateMediator.clearErrorUIState() }
        coVerify { stateMediator.setSuccessState(SuccessUIState.Success(R.string.create_incident_success)) }
    }

    @Test
    fun `createIncident should set error state on failure`() = runTest {
        val customErrorMessage = "Custom error message"
        val customError = RepositoryError.CustomError(customErrorMessage)

        coEvery { incidentsRepository.createIncident(any(), any()) } returns Result.failure(
            customError
        )

        viewModel.createIncident { fail("Callback should not be called on failure") }
        advanceUntilIdle()

        coVerify {
            stateMediator.setErrorState(
                ErrorUIState.Error(message = customErrorMessage)
            )
        }
    }

}
