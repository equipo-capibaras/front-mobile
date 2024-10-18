package io.capibaras.abcall.data.repositories

import io.capibaras.abcall.data.database.dao.CompanyDAO
import io.capibaras.abcall.data.database.models.Company
import io.capibaras.abcall.data.network.services.CompanyService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CompanyRepositoryTest {
    private lateinit var companyDAO: CompanyDAO
    private lateinit var companyService: CompanyService
    private lateinit var companyRepository: CompanyRepository

    @Before
    fun setup() {
        companyDAO = mockk(relaxed = true)
        companyService = mockk()
        companyRepository = CompanyRepository(companyDAO, companyService)
    }

    @Test
    fun `should return local data if no internet and local data is available`() = runBlocking {
        val localData = listOf(mockk<Company>())
        coEvery { companyDAO.getAllCompanies() } returns localData
        coEvery { companyService.getCompanies() } throws IOException()

        val result = companyRepository.getCompanies()

        assertEquals(localData, result)
        coVerify { companyDAO.getAllCompanies() }
        coVerify(exactly = 0) { companyDAO.refreshCompanies(any()) }
    }

    @Test
    fun `should throw exception if no internet and no local data`() = runBlocking {
        coEvery { companyDAO.getAllCompanies() } returns emptyList()
        coEvery { companyService.getCompanies() } throws IOException()

        assertFailsWith<IOException> {
            companyRepository.getCompanies()
        }

        coVerify { companyDAO.getAllCompanies() }
        coVerify(exactly = 0) { companyDAO.refreshCompanies(any()) }
    }

    @Test
    fun `should refresh companies from remote if internet is available`() = runBlocking {
        val remoteData = listOf(mockk<Company>())
        coEvery { companyDAO.getAllCompanies() } returns emptyList()
        coEvery { companyService.getCompanies() } returns remoteData

        val result = companyRepository.getCompanies()

        assertEquals(remoteData, result)
        coVerify { companyDAO.refreshCompanies(remoteData) }
    }

    @Test
    fun `should return local company if available`() = runBlocking {
        val clientId = "123"
        val localCompany = mockk<Company>()
        coEvery { companyDAO.getCompany(clientId) } returns localCompany

        val result = companyRepository.getCompany(clientId)

        assertEquals(localCompany, result)
        coVerify { companyDAO.getCompany(clientId) }
        coVerify(exactly = 0) { companyService.getCompany(any()) }
    }

    @Test
    fun `should fetch and cache company from remote if not available locally`() = runBlocking {
        val clientId = "123"
        val remoteCompany = mockk<Company>()
        val mockResponse = mockk<retrofit2.Response<Company>>(relaxed = true) {
            every { isSuccessful } returns true
            every { body() } returns remoteCompany
        }
        coEvery { companyDAO.getCompany(clientId) } returns null
        coEvery { companyService.getCompany(clientId) } returns mockResponse

        val result = companyRepository.getCompany(clientId)

        assertEquals(remoteCompany, result)
        coVerify { companyDAO.insertCompany(remoteCompany) }
    }

    @Test
    fun `should throw exception if remote company fetch fails with error body`() = runBlocking {
        val errorMsg = "Some other error"
        val clientId = "123"
        mockk<JSONObject> {
            every { getString("message") } returns errorMsg
        }
        val errorResponse = mockk<retrofit2.Response<Company>>(relaxed = true) {
            every { isSuccessful } returns false
            every { errorBody() } returns mockk {
                every { string() } returns "{\"message\": \"$errorMsg\"}"
            }
        }
        mockkConstructor(JSONObject::class)
        every { anyConstructed<JSONObject>().getString(any()) } returns errorMsg

        coEvery { companyDAO.getCompany(clientId) } returns null
        coEvery { companyService.getCompany(clientId) } returns errorResponse

        val exception = assertFailsWith<Exception> {
            companyRepository.getCompany(clientId)
        }

        assertEquals(errorMsg, exception.message)
    }

    @Test
    fun `should throw exception if remote company fetch fails without error body`() = runBlocking {
        val clientId = "123"
        val errorResponse = mockk<retrofit2.Response<Company>>(relaxed = true) {
            every { isSuccessful } returns false
            every { errorBody() } returns null
        }
        coEvery { companyDAO.getCompany(clientId) } returns null
        coEvery { companyService.getCompany(clientId) } returns errorResponse

        val exception = assertFailsWith<Exception> {
            companyRepository.getCompany(clientId)
        }

        assertEquals(null, exception.message)
    }
}
