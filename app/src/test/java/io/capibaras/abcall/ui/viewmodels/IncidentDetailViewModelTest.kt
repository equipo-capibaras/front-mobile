package io.capibaras.abcall.ui.viewmodels

import io.capibaras.abcall.data.repositories.IncidentsRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IncidentDetailViewModelTest {

    private lateinit var viewModel: IncidentDetailViewModel

    @MockK
    private lateinit var incidentsRepository: IncidentsRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        coEvery { incidentsRepository.markAsViewed(any()) } just runs

        viewModel = IncidentDetailViewModel(incidentsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `markIncidentAsViewed should call markAsViewed on repository`() = runTest {
        val incidentId = "incident-123"

        viewModel.markIncidentAsViewed(incidentId)

        advanceUntilIdle()

        coVerify { incidentsRepository.markAsViewed(incidentId) }
    }
}
