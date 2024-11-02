package io.capibaras.abcall.data.repositories

import io.capibaras.abcall.data.database.dao.IncidentDAO
import io.capibaras.abcall.data.database.models.History
import io.capibaras.abcall.data.database.models.Incident
import io.capibaras.abcall.data.network.models.Channel
import io.capibaras.abcall.data.network.models.CreateIncidentResponse
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
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.json.JSONObject
import org.junit.After
import org.junit.Assert
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

    private fun createMockIncidentDetailResponse(): CreateIncidentResponse {
        return CreateIncidentResponse(
            "1",
            "clientId",
            "name",
            Channel.MOBILE,
            "reportedBy",
            "createdBy",
            "assignedTo"
        )
    }

    private fun createMockIncidentHistory(): List<History> {
        return listOf(
            History(1, "2024-01-01", "filed", "Filed the incident"),
            History(2, "2024-02-01", "escalated", "Escalated the incident"),
            History(3, "2024-03-01", "closed", "Closed the incident")
        )
    }

    private fun createMockIncident(): Incident {
        return Incident(
            "1",
            "Incident Test",
            "email",
            createMockIncidentHistory(),
            "2024-01-01",
            "2024-02-01",
            "2024-03-01",
            false
        )
    }

    @Test
    fun `should return incidents from API and update local database`() = runBlocking {
        val mockIncidentList = listOf(createMockIncident())

        val mockResponse = mockk<Response<List<Incident>>> {
            every { isSuccessful } returns true
            every { body() } returns mockIncidentList
        }

        coEvery { incidentsService.getIncidents() } returns mockResponse
        coEvery { incidentDAO.getIncident("1") } returns null
        coEvery { incidentDAO.getAllIncidents() } returns emptyList()
        coEvery { incidentDAO.refreshIncidents(any()) } just Runs

        val result = incidentsRepository.getIncidents()

        assertEquals(Result.success(mockIncidentList), result)
        coVerify { incidentDAO.refreshIncidents(mockIncidentList) }
    }

    @Test
    fun `should return local incidents if API fails with error response`() = runBlocking {
        val mockLocalIncidentList = listOf(createMockIncident())

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
        val mockLocalIncidentList = listOf(createMockIncident())

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

    @Test
    fun `should assign correct dates based on incident history`() = runBlocking {
        val incident = createMockIncident()

        coEvery { incidentsService.getIncidents() } returns Response.success(listOf(incident))
        coEvery { incidentDAO.getAllIncidents() } returns emptyList()
        coEvery { incidentDAO.refreshIncidents(any()) } just runs
        coEvery { incidentDAO.getIncident(any()) } returns null

        val result = incidentsRepository.getIncidents()

        assertTrue(result.isSuccess)
        val resultIncident = result.getOrNull()?.first()
        assertEquals("2024-01-01", resultIncident?.filedDate)
        assertEquals("2024-02-01", resultIncident?.escalatedDate)
        assertEquals("2024-03-01", resultIncident?.closedDate)
    }

    @Test
    fun `should return failure with custom error when an unexpected exception occurs in getIncidents`() =
        runBlocking {
            val unexpectedError = Exception("Unexpected error")

            coEvery { incidentsService.getIncidents() } throws unexpectedError
            coEvery { incidentDAO.getAllIncidents() } returns emptyList()

            val result = incidentsRepository.getIncidents()

            assertTrue(result.isFailure)
            assertEquals(
                "Unexpected error",
                (result.exceptionOrNull() as RepositoryError.CustomError).message
            )
        }


    @Test
    fun `should return success when incident is successfully created`() = runBlocking {
        val createIncidentResponse = createMockIncidentDetailResponse()
        coEvery { incidentsService.createIncident(any()) } returns Response.success(
            createIncidentResponse
        )

        val result = incidentsRepository.createIncident("Test Name", "Test Description")

        assertTrue(result.isSuccess)
        assertEquals(createIncidentResponse, result.getOrNull())
    }

    @Test
    fun `should return network error when exception occurs during incident creation`() =
        runBlocking {
            coEvery { incidentsService.createIncident(any()) } throws IOException()

            val result = incidentsRepository.createIncident("Test Name", "Test Description")

            assertTrue(result.isFailure)
            assertEquals(RepositoryError.NetworkError, result.exceptionOrNull())
        }

    @Test
    fun `should return failure with custom error when an unexpected exception occurs in createIncident`() =
        runBlocking {
            val unexpectedError = Exception("Unexpected error in createIncident")

            coEvery { incidentsService.createIncident(any()) } throws unexpectedError

            val result = incidentsRepository.createIncident("Test Name", "Test Description")

            assertTrue(result.isFailure)
            assertEquals(
                "Unexpected error in createIncident",
                (result.exceptionOrNull() as RepositoryError.CustomError).message
            )
        }

    @Test
    fun `should set recentlyUpdated to true when incident history size differs from local data`(): Unit =
        runBlocking {
            val mockLocalIncident = createMockIncident().copy(
                history = listOf(
                    History(
                        1,
                        "2024-01-01",
                        "filed",
                        "Filed the incident"
                    )
                )
            )
            val mockIncidentList = listOf(createMockIncident())

            val mockResponse = mockk<Response<List<Incident>>> {
                every { isSuccessful } returns true
                every { body() } returns mockIncidentList
            }

            coEvery { incidentsService.getIncidents() } returns mockResponse
            coEvery { incidentDAO.getIncident("1") } returns mockLocalIncident
            coEvery { incidentDAO.getAllIncidents() } returns mockIncidentList
            coEvery { incidentDAO.refreshIncidents(any()) } just Runs

            val result = incidentsRepository.getIncidents()

            Assert.assertTrue(result.isSuccess)
            Assert.assertTrue(result.getOrNull()?.first()?.recentlyUpdated == true)
        }
}
