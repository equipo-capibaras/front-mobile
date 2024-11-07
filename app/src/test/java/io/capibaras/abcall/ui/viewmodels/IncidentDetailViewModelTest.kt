package io.capibaras.abcall.ui.viewmodels

import io.capibaras.abcall.data.database.models.History
import io.capibaras.abcall.data.database.models.Incident
import io.capibaras.abcall.data.repositories.IncidentsRepository
import io.capibaras.abcall.data.repositories.RepositoryError
import io.capibaras.abcall.ui.util.StateMediator
import io.capibaras.abcall.ui.viewmodels.utils.ErrorUIState
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
class IncidentDetailViewModelTest {
    @MockK
    private lateinit var incidentsRepository: IncidentsRepository

    @MockK
    private lateinit var stateMediator: StateMediator

    private lateinit var viewModel: IncidentDetailViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        every { stateMediator.isLoading } returns false
        every { stateMediator.setLoadingState(any()) } just runs
        every { stateMediator.clearErrorUIState() } just runs
        every { stateMediator.setErrorState(any()) } just runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createMockIncident(): Incident {
        return Incident(
            id = "1",
            name = "Incident Test",
            channel = "email",
            history = createMockHistoryList(),
            filedDate = "2024-01-01",
            escalatedDate = "2024-01-02",
            closedDate = null,
            recentlyUpdated = true
        )

    }

    private fun createMockHistoryList(): List<History> {
        return listOf(
            History(
                seq = 1,
                date = "2024-01-01",
                action = "created",
                description = "Incident created"
            ),
            History(
                seq = 2,
                date = "2024-01-02",
                action = "updated",
                description = "Incident updated"
            )
        )
    }

    @Test
    fun `getIncident success`() = runTest {
        val mockIncident = createMockIncident()

        coEvery { incidentsRepository.getIncident("1") } returns Result.success(mockIncident)

        viewModel = IncidentDetailViewModel(incidentsRepository, stateMediator)
        viewModel.getIncident("1")
        advanceUntilIdle()

        assertEquals(mockIncident, viewModel.incident)
        coVerify { stateMediator.clearErrorUIState() }
        coVerify { stateMediator.setLoadingState(false) }
    }

    @Test
    fun `getIncident failure with default error`() = runTest {
        val customErrorMessage = "Error fetching incident"
        val customError = RepositoryError.CustomError(customErrorMessage)

        coEvery { incidentsRepository.getIncident("1") } returns Result.failure(customError)

        viewModel = IncidentDetailViewModel(incidentsRepository, stateMediator)
        viewModel.getIncident("1")
        advanceUntilIdle()

        coVerify {
            stateMediator.setErrorState(
                ErrorUIState.Error(message = customErrorMessage)
            )
        }
        coVerify { stateMediator.setLoadingState(false) }
    }

    @Test
    fun `getIncident skips loading when already loading`() = runTest {
        every { stateMediator.isLoading } returns true

        viewModel = IncidentDetailViewModel(incidentsRepository, stateMediator)
        viewModel.getIncident("1")
        advanceUntilIdle()

        coVerify(exactly = 0) { incidentsRepository.getIncident("1") }
    }

    @Test
    fun `markIncidentAsViewed calls repository`() = runTest {
        coEvery { incidentsRepository.markAsViewed("1") } just runs

        viewModel = IncidentDetailViewModel(incidentsRepository, stateMediator)
        viewModel.markIncidentAsViewed("1")
        advanceUntilIdle()

        coVerify { incidentsRepository.markAsViewed("1") }
    }

    @Test
    fun `onRefresh success`() = runTest {
        val mockIncident = createMockIncident()

        coEvery { incidentsRepository.getIncident("1") } returns Result.success(mockIncident)

        viewModel = IncidentDetailViewModel(incidentsRepository, stateMediator)
        viewModel.onRefresh("1")
        advanceUntilIdle()

        assertEquals(false, viewModel.isRefreshing)
        assertEquals(mockIncident, viewModel.incident)
        coVerify { stateMediator.clearErrorUIState() }
    }

    @Test
    fun `onRefresh failure`() = runTest {
        val customErrorMessage = "Error refreshing incident"
        val customError = RepositoryError.CustomError(customErrorMessage)

        coEvery { incidentsRepository.getIncident("1") } returns Result.failure(customError)

        viewModel = IncidentDetailViewModel(incidentsRepository, stateMediator)
        viewModel.onRefresh("1")
        advanceUntilIdle()

        assertEquals(false, viewModel.isRefreshing)
        coVerify { stateMediator.setErrorState(ErrorUIState.Error(message = customErrorMessage)) }
    }
}


