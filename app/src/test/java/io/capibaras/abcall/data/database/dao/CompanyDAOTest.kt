package io.capibaras.abcall.data.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.capibaras.abcall.data.database.ABCallDB
import io.capibaras.abcall.data.database.models.Company
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
@Config(manifest=Config.NONE)
class CompanyDAOTest {
    private lateinit var database: ABCallDB
    private lateinit var companyDAO: CompanyDAO
    private lateinit var faker: Faker

    @Before
    fun setup() {
        faker = Faker()
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ABCallDB::class.java
        ).allowMainThreadQueries().build()
        companyDAO = database.companyDAO()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `test refresh replaces the old list of clients with the new one`() = runBlocking {
        val oldCompanies = List(3) {
            Company(
                id = faker.random.nextUUID(),
                name = faker.company.name()
            )
        }

        val newCompanies = List(3) {
            Company(
                id = faker.random.nextUUID(),
                name = faker.company.name()
            )
        }

        companyDAO.insertCompanies(oldCompanies)
        assertEquals(oldCompanies, companyDAO.getAllCompanies())

        companyDAO.refreshCompanies(newCompanies)
        assertEquals(newCompanies, companyDAO.getAllCompanies())
    }
}