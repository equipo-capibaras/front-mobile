package io.capibaras.abcall.data.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.capibaras.abcall.data.database.ABCallDB
import io.capibaras.abcall.data.database.models.History
import io.capibaras.abcall.data.database.models.Incident
import io.github.serpro69.kfaker.Faker
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class IncidentDAOTest {
    private lateinit var database: ABCallDB
    private lateinit var incidentDAO: IncidentDAO
    private lateinit var faker: Faker

    @Before
    fun setup() {
        faker = Faker()
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ABCallDB::class.java
        ).allowMainThreadQueries().build()
        incidentDAO = database.incidentDAO()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `test insert and get all incidents`() = runBlocking {
        val incidents = listOf(
            Incident(
                id = faker.random.nextUUID(),
                name = "Incident 1",
                channel = "email",
                history = listOf(
                    History(1, "2024-01-01", "filed", "Incident filed"),
                    History(2, "2024-01-02", "escalated", "Incident escalated")
                ),
                filedDate = "2024-01-01",
                escalatedDate = "2024-01-02",
                closedDate = null,
                recentlyUpdated = false
            ),
            Incident(
                id = faker.random.nextUUID(),
                name = "Incident 2",
                channel = "phone",
                history = listOf(
                    History(1, "2024-01-03", "filed", "Incident filed"),
                    History(2, "2024-01-04", "closed", "Incident closed")
                ),
                filedDate = "2024-01-03",
                escalatedDate = null,
                closedDate = "2024-01-04",
                recentlyUpdated = true
            )
        )

        incidentDAO.insertIncidents(incidents)

        val allIncidents = incidentDAO.getAllIncidents()

        assertEquals(incidents.size, allIncidents.size)
        assertEquals(incidents[0], allIncidents[0])
        assertEquals(incidents[1], allIncidents[1])
    }

    @Test
    fun `test get incident by id`() = runBlocking {
        val incident = Incident(
            id = faker.random.nextUUID(),
            name = "Incident Test",
            channel = "chat",
            history = listOf(
                History(1, "2024-01-01", "filed", "Incident filed"),
                History(2, "2024-01-02", "escalated", "Incident escalated")
            ),
            filedDate = "2024-01-01",
            escalatedDate = "2024-01-02",
            closedDate = null,
            recentlyUpdated = false
        )

        incidentDAO.insertIncident(incident)

        val retrievedIncident = incidentDAO.getIncident(incident.id)

        assertEquals(incident, retrievedIncident)
    }

    @Test
    fun `test delete all incidents`() = runBlocking {
        val incidents = listOf(
            Incident(
                id = faker.random.nextUUID(),
                name = "Incident 1",
                channel = "email",
                history = emptyList(),
                filedDate = "2024-01-01",
                escalatedDate = null,
                closedDate = null,
                recentlyUpdated = false
            )
        )

        incidentDAO.insertIncidents(incidents)

        incidentDAO.deleteAllIncidents()

        val allIncidents = incidentDAO.getAllIncidents()
        assertEquals(0, allIncidents.size)
    }

    @Test
    fun `test refresh incidents`() = runBlocking {
        val oldIncidents = listOf(
            Incident(
                id = faker.random.nextUUID(),
                name = "Incident Old",
                channel = "email",
                history = emptyList(),
                filedDate = "2024-01-01",
                escalatedDate = null,
                closedDate = null,
                recentlyUpdated = false
            )
        )

        val newIncidents = listOf(
            Incident(
                id = faker.random.nextUUID(),
                name = "Incident New",
                channel = "phone",
                history = emptyList(),
                filedDate = "2024-01-05",
                escalatedDate = null,
                closedDate = null,
                recentlyUpdated = false
            )
        )

        incidentDAO.insertIncidents(oldIncidents)
        assertEquals(oldIncidents, incidentDAO.getAllIncidents())

        incidentDAO.refreshIncidents(newIncidents)

        val allIncidents = incidentDAO.getAllIncidents()
        assertEquals(newIncidents, allIncidents)
    }
}
