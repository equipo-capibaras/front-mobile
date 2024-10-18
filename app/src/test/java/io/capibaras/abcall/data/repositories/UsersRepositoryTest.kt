package io.capibaras.abcall.data.repositories

import io.capibaras.abcall.data.database.dao.UserDAO
import io.capibaras.abcall.data.database.models.Company
import io.capibaras.abcall.data.database.models.User
import io.capibaras.abcall.data.network.models.CreateUserRequest
import io.capibaras.abcall.data.network.services.UsersService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UsersRepositoryTest {
    private lateinit var usersService: UsersService
    private lateinit var userDAO: UserDAO
    private lateinit var companyRepository: CompanyRepository
    private lateinit var usersRepository: UsersRepository

    @Before
    fun setup() {
        usersService = mockk()
        userDAO = mockk(relaxed = true)
        companyRepository = mockk()
        usersRepository = UsersRepository(usersService, userDAO, companyRepository)
    }

    @Test
    fun `should create user via service`() = runBlocking {
        val clientId = "123"
        val name = "John Doe"
        val email = "johndoe@email.com"
        val password = "password"
        val mockResponse = mockk<Response<User>>()
        coEvery { usersService.createUser(any()) } returns mockResponse

        val result = usersRepository.createUser(clientId, name, email, password)

        coVerify { usersService.createUser(CreateUserRequest(clientId, name, email, password)) }
        assertEquals(mockResponse, result)
    }

    @Test
    fun `should return local user if available`() = runBlocking {
        val localUser = mockk<User>()
        coEvery { userDAO.getUserInfo() } returns localUser

        val result = usersRepository.getUserInfo()

        assertEquals(localUser, result)
        coVerify { userDAO.getUserInfo() }
        coVerify(exactly = 0) { usersService.getUserInfo() }
    }

    @Test
    fun `should fetch and cache user from remote if not available locally`() = runBlocking {
        coEvery { userDAO.getUserInfo() } returns null

        val remoteUser = mockk<User>(relaxed = true) {
            every { clientId } returns "123"
        }

        val company = mockk<Company> {
            every { name } returns "Company Name"
        }

        every { remoteUser.copy(clientName = company.name) } returns remoteUser.copy(clientName = company.name)

        val mockResponse = mockk<Response<User>>(relaxed = true) {
            every { isSuccessful } returns true
            every { body() } returns remoteUser
        }

        coEvery { usersService.getUserInfo() } returns mockResponse
        coEvery { companyRepository.getCompany("123") } returns company

        val result = usersRepository.getUserInfo()

        val expectedUser = remoteUser.copy(clientName = company.name)
        coVerify { userDAO.refreshUser(expectedUser) }
        assertEquals(expectedUser, result)
    }


    @Test
    fun `should throw exception if remote user fetch fails with error body`() = runBlocking {
        val errorMsg = "Some other error"
        coEvery { userDAO.getUserInfo() } returns null
        mockk<JSONObject> {
            every { getString("message") } returns errorMsg
        }
        val errorResponse = mockk<Response<User>>(relaxed = true) {
            every { isSuccessful } returns false
            every { errorBody() } returns mockk {
                every { string() } returns "{\"message\": \"$errorMsg\"}"
            }
        }
        mockkConstructor(JSONObject::class)
        every { anyConstructed<JSONObject>().getString(any()) } returns errorMsg
        coEvery { usersService.getUserInfo() } returns errorResponse

        val exception = assertFailsWith<Exception> {
            usersRepository.getUserInfo()
        }

        assertEquals(errorMsg, exception.message)
    }

    @Test
    fun `should throw exception if remote user fetch fails without error body`() = runBlocking {
        coEvery { userDAO.getUserInfo() } returns null
        val errorResponse = mockk<Response<User>>(relaxed = true) {
            every { isSuccessful } returns false
            every { errorBody() } returns null
        }
        coEvery { usersService.getUserInfo() } returns errorResponse

        val exception = assertFailsWith<Exception> {
            usersRepository.getUserInfo()
        }

        assertEquals(null, exception.message)
    }

    @Test
    fun `should delete all users`() = runBlocking {
        usersRepository.deleteUsers()

        coVerify { userDAO.deleteUsers() }
    }
}
