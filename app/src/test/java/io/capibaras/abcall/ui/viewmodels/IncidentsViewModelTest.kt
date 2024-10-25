package io.capibaras.abcall.ui.viewmodels

import io.capibaras.abcall.data.database.models.History
import io.capibaras.abcall.data.database.models.Incident
import io.capibaras.abcall.data.repositories.IncidentsRepository
import io.capibaras.abcall.data.repositories.RepositoryError
import io.capibaras.abcall.ui.util.StateMediator
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IncidentsViewModelTest {
    private lateinit var viewModel: IncidentViewModel

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
        every { stateMediator.clearErrorUIState() } just runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test getIncidents success`() = runTest {
        val mockIncidentList = createMockIncidentList()

        coEvery { incidentsRepository.getIncidents() } returns Result.success(mockIncidentList)

        viewModel = IncidentViewModel(incidentsRepository, stateMediator)
        advanceUntilIdle()

        assertEquals(mockIncidentList, viewModel.incidents)
        coVerify { stateMediator.clearErrorUIState() }
        coVerify { stateMediator.setLoadingState(false) }
    }

    @Test
    fun `test getIncidents failure`() = runTest {
        coEvery { incidentsRepository.getIncidents() } returns Result.failure(Exception("Error"))

        viewModel = IncidentViewModel(incidentsRepository, stateMediator)
        advanceUntilIdle()

        coVerify { stateMediator.setErrorState(any()) }
        coVerify { stateMediator.setLoadingState(false) }
    }

    @Test
    fun `test getIncidents with custom error message`() = runTest {
        val customErrorMessage = "Custom error message"
        val customError = RepositoryError.CustomError(customErrorMessage)

        coEvery { incidentsRepository.getIncidents() } returns Result.failure(customError)

        viewModel = IncidentViewModel(incidentsRepository, stateMediator)
        advanceUntilIdle()

        coVerify {
            stateMediator.setErrorState(
                ErrorUIState.Error(message = customErrorMessage)
            )
        }
    }

    @Test
    fun `test getIncidents loading state`() = runTest {
        every { stateMediator.isLoading } returns true

        viewModel = IncidentViewModel(incidentsRepository, stateMediator)
        advanceUntilIdle()

        coVerify(exactly = 0) { incidentsRepository.getIncidents() }
        coVerify(exactly = 0) { stateMediator.setLoadingState(true) }
    }

    @Test
    fun `test onRefresh with success`() = runTest {
        val mockIncidentList = createMockIncidentList()

        coEvery { incidentsRepository.getIncidents() } returns Result.success(mockIncidentList)
        viewModel = IncidentViewModel(incidentsRepository, stateMediator)

        viewModel.onRefresh()
        advanceUntilIdle()

        assertEquals(mockIncidentList, viewModel.incidents)
        assertEquals(false, viewModel.isRefreshing)
        coVerify { stateMediator.clearErrorUIState() }
    }

    @Test
    fun `test onRefresh with failure`() = runTest {
        val customErrorMessage = "Custom error message"
        val customError = RepositoryError.CustomError(customErrorMessage)

        coEvery { incidentsRepository.getIncidents() } returns Result.failure(customError)
        viewModel = IncidentViewModel(incidentsRepository, stateMediator)

        viewModel.onRefresh()
        advanceUntilIdle()

        assertEquals(false, viewModel.isRefreshing)
        coVerify { stateMediator.setErrorState(ErrorUIState.Error(message = customErrorMessage)) }
    }

    @Test
    fun `test init calls getIncidents`() = runTest {
        val mockIncidentList = createMockIncidentList()
        coEvery { incidentsRepository.getIncidents() } returns Result.success(mockIncidentList)

        viewModel = IncidentViewModel(incidentsRepository, stateMediator)
        advanceUntilIdle()

        assertEquals(mockIncidentList, viewModel.incidents)
        coVerify { incidentsRepository.getIncidents() }
    }

    private fun createMockIncidentList(): List<Incident> {
        val mockHistory = listOf(
            History(
                seq = 1,
                date = "2024-01-01",
                action = "filed",
                description = "Filed the incident"
            ),
            History(
                seq = 2,
                date = "2024-01-02",
                action = "escalated",
                description = "Escalated the incident"
            )
        )
        return listOf(
            Incident(
                id = "1",
                name = "Incident Test",
                channel = "email",
                history = mockHistory,
                filedDate = "2024-01-01",
                escalatedDate = "2024-01-02",
                closedDate = null,
                recentlyUpdated = true
            )
        )
    }
}