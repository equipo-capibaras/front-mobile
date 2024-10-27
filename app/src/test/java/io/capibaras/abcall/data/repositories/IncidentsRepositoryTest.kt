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
import kotlin.test.assertTrue

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
        coEvery { incidentDAO.getAllIncidents() } returns emptyList()
        coEvery { incidentDAO.refreshIncidents(mockIncidentList) } just Runs

        val result = incidentsRepository.getIncidents()

        assertEquals(Result.success(mockIncidentList), result)
        coVerify { incidentDAO.refreshIncidents(mockIncidentList) }
    }

    @Test
    fun `should return local incidents if API fails with error response`() = runBlocking {
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

        val errorMessage = "Custom error message"
        val errorBody = ResponseBody.create(null, """{"message": "$errorMessage"}""")
        val mockResponse = mockk<Response<List<Incident>>> {
            every { isSuccessful } returns false
            every { errorBody() } returns errorBody
        }

        mockkConstructor(JSONObject::class)
        every {
            anyConstructed<JSONObject>().optString(
                "message",
                "Unknown error"
            )
        } returns errorMessage

        coEvery { incidentsService.getIncidents() } returns mockResponse
        coEvery { incidentDAO.getAllIncidents() } returns mockLocalIncidentList

        val result = incidentsRepository.getIncidents()

        assertTrue(result.isSuccess)
        assertEquals(Result.success(mockLocalIncidentList), result)
    }

    @Test
    fun `should return local data if network exception occurs`() = runBlocking {
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

        coEvery { incidentsService.getIncidents() } throws IOException()
        coEvery { incidentDAO.getAllIncidents() } returns mockLocalIncidentList

        val result = incidentsRepository.getIncidents()

        assertEquals(Result.success(mockLocalIncidentList), result)
        coVerify { incidentDAO.getAllIncidents() }
    }

    @Test
    fun `should return failure when no local data and network exception occurs`() = runBlocking {
        coEvery { incidentsService.getIncidents() } throws IOException()
        coEvery { incidentDAO.getAllIncidents() } returns emptyList()

        val result = incidentsRepository.getIncidents()

        assertTrue(result.isFailure)
        assertEquals(IncidentsRepositoryError.GetIncidentsError, result.exceptionOrNull())
    }
}
