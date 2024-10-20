package io.capibaras.abcall.data.network.interceptors

import io.capibaras.abcall.data.LogoutManager
import io.capibaras.abcall.data.TokenManager
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import org.junit.Before
import org.junit.Test

class AuthInterceptorTest {
    @MockK
    private lateinit var tokenManager: TokenManager

    @MockK
    private lateinit var logoutManager: LogoutManager

    private lateinit var interceptor: AuthInterceptor
    private lateinit var response: Response
    private lateinit var request: Request
    private lateinit var chain: Interceptor.Chain

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        interceptor = AuthInterceptor(tokenManager, logoutManager, Dispatchers.Unconfined)
        coEvery { logoutManager.logout(isExpiredToken = true) } just runs
        val request = Request.Builder()
            .url("http://localhost/api/v1/test")
            .build()
        response = mockk<Response> {
            every { code() } returns 200
        }
        chain = mockk {
            every { request() } returns request
            every { proceed(any()) } returns response
        }
    }

    @Test
    fun `should add token to request if token is present and path is not excluded`() = runTest {
        val token = "test-token"
        every { tokenManager.getAuthToken() } returns token

        interceptor.intercept(chain)

        verify { chain.proceed(withArg { assert(it.header("Authorization") == "Bearer $token") }) }
    }

    @Test
    fun `should not add token to request if path is excluded`() = runTest {
        every { tokenManager.getAuthToken() } returns "test-token"
        val request = Request.Builder()
            .url("http://localhost/api/v1/users")
            .build()
        response = mockk<Response> {
            every { code() } returns 200
        }
        chain = mockk {
            every { request() } returns request
            every { proceed(any()) } returns response
        }

        interceptor.intercept(chain)

        verify(exactly = 1) { chain.proceed(request) }
        verify { chain.proceed(withArg { assert(it.header("Authorization") == null) }) }
    }

    @Test
    fun `should call logout when token is not present and path is not excluded`() = runTest {
        every { tokenManager.getAuthToken() } returns null

        interceptor.intercept(chain)

        coVerify { logoutManager.logout(isExpiredToken = true) }
    }

    @Test
    fun `should call logout when server responds with 401 and Jwt expired message`() = runTest {
        val errorMsg = "Jwt is expired"
        every { tokenManager.getAuthToken() } returns "valid-token"
        mockk<JSONObject> {
            every { getString("message") } returns errorMsg
        }
        val request = Request.Builder()
            .url("http://localhost/api/v1/test")
            .build()
        response = mockk<Response> {
            every { code() } returns 401
            every { peekBody(Long.MAX_VALUE).string() } returns "{\"message\": \"$errorMsg\"}"
        }

        mockkConstructor(JSONObject::class)
        every { anyConstructed<JSONObject>().getString(any()) } returns errorMsg

        chain = mockk {
            every { request() } returns request
            every { proceed(any()) } returns response
        }

        interceptor.intercept(chain)

        coVerify { logoutManager.logout(isExpiredToken = true) }
    }

    @Test
    fun `should not call logout when server responds with 401 but no Jwt expired message`() =
        runTest {
            val errorMsg = "Some other error"
            every { tokenManager.getAuthToken() } returns "valid-token"
            mockk<JSONObject> {
                every { getString("message") } returns errorMsg
            }
            val request = Request.Builder()
                .url("http://localhost/api/v1/test")
                .build()
            response = mockk<Response> {
                every { code() } returns 401
                every { peekBody(Long.MAX_VALUE).string() } returns "{\"message\": \"$errorMsg\"}"
            }

            mockkConstructor(JSONObject::class)
            every { anyConstructed<JSONObject>().getString(any()) } returns errorMsg

            chain = mockk {
                every { request() } returns request
                every { proceed(any()) } returns response
            }

            interceptor.intercept(chain)

            coVerify(exactly = 0) { logoutManager.logout(any()) }
        }
}
