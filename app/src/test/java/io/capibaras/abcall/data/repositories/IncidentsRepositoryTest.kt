package io.capibaras.abcall.data.repositories

import io.capibaras.abcall.data.database.dao.IncidentDAO
import io.capibaras.abcall.data.database.models.History
import io.capibaras.abcall.data.database.models.Incident
import io.capibaras.abcall.data.network.services.IncidentsService
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class IncidentsRepositoryTest {
    @MockK
    private lateinit var incidentDAO: IncidentDAO

    @MockK
    private lateinit var incidentsService: IncidentsService
    private lateinit var incidentsRepository: IncidentsRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        incidentsRepository = IncidentsRepository(incidentDAO, incidentsService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should return incidents from API and update local database`() = runBlocking {
        val mockHistory = listOf(
            History(1, "2024-01-01", "filed", "Filed the incident"),
            History(2, "2024-01-02", "escalated", "Escalated the incident")
        )

        val mockIncidentList = listOf(
            Incident(
                "1",
                "Incident Test",
                "email",
                mockHistory,
                "2024-01-01",
                "2024-01-02",
                null,
                false
            )
        )

        val mockResponse = mockk<Response<List<Incident>>> {
            every { isSuccessful } returns true
            every { body() } returns mockIncidentList
        }

        coEvery { incidentsService.getIncidents() } returns mockResponse
        coEvery { incidentDAO.getIncident("1") } returns null
        coEvery { incidentDAO.refreshIncidents(mockIncidentList) } just Runs

        val result = incidentsRepository.getIncidents()

        assertEquals(Result.success(mockIncidentList), result)
        coVerify { incidentDAO.refreshIncidents(mockIncidentList) }
    }


    @Test
    fun `should return local incidents if API fails`() = runBlocking {
        val mockLocalIncidentList = listOf(
            Incident(
                id = "1",
                name = "Local Incident",
                channel = "email",
                history = emptyList(),
                filedDate = "2024-01-01",
                escalatedDate = null,
                closedDate = null,
                recentlyUpdated = false
            )
        )

        val mockResponse = mockk<Response<List<Incident>>> {
            every { isSuccessful } returns false
        }

        coEvery { incidentsService.getIncidents() } returns mockResponse
        coEvery { incidentDAO.getAllIncidents() } returns mockLocalIncidentList

        val result = incidentsRepository.getIncidents()

        assertEquals(Result.success(mockLocalIncidentList), result)
        coVerify { incidentDAO.getAllIncidents() }
        coVerify(exactly = 0) { incidentDAO.refreshIncidents(any()) }
    }


    @Test
    fun `should throw error if no local data and API fails`() = runBlocking {
        coEvery { incidentsService.getIncidents() } throws IOException()
        coEvery { incidentDAO.getAllIncidents() } returns emptyList()

        val result = incidentsRepository.getIncidents()

        assert(result.isFailure)
        assertEquals(IncidentsRepositoryError.GetIncidentsError, result.exceptionOrNull())
    }

    @Test
    fun `should return custom error message when API fails with message`() = runBlocking {
        val errorMsg = "Custom error message"
        val errorBody = ResponseBody.create(null, """{"message": "$errorMsg"}""")
        val mockResponse = mockk<Response<List<Incident>>> {
            every { isSuccessful } returns false
            every { errorBody() } returns errorBody
        }

        mockkConstructor(JSONObject::class)
        every { anyConstructed<JSONObject>().getString(any()) } returns errorMsg

        coEvery { incidentsService.getIncidents() } returns mockResponse
        coEvery { incidentDAO.getAllIncidents() } returns emptyList()

        val result = incidentsRepository.getIncidents()

        assert(result.isFailure)
        assertEquals(errorMsg, (result.exceptionOrNull() as RepositoryError.CustomError).message)

        coVerify(exactly = 0) { incidentDAO.refreshIncidents(any()) }
    }

    @Test
    fun `should return CustomError when an exception with a message occurs`() = runBlocking {
        val errorMessage = "Unexpected error"

        coEvery { incidentsService.getIncidents() } throws Exception(errorMessage)

        val result = incidentsRepository.getIncidents()

        assert(result.isFailure)
        val error = result.exceptionOrNull() as RepositoryError.CustomError
        assertEquals(errorMessage, error.message)
    }

    @Test
    fun `should return UnknownError when an exception without message occurs`() = runBlocking {
        coEvery { incidentsService.getIncidents() } throws Exception()

        val result = incidentsRepository.getIncidents()

        assert(result.isFailure)
        assertEquals(RepositoryError.UnknownError, result.exceptionOrNull())
    }
}
