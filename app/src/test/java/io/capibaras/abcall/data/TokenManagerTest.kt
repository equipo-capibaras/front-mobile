package io.capibaras.abcall.data

import androidx.test.core.app.ApplicationProvider
import io.github.serpro69.kfaker.Faker
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
class TokenManagerTest {
    private lateinit var tokenManager: TokenManager
    private lateinit var faker: Faker

    @Before
    fun setUp() {
        faker = Faker()
        tokenManager = TokenManager(
            ApplicationProvider.getApplicationContext(),
            "key"
        )
    }

    @Test
    fun `should save the token`() {
        val token = faker.random.randomString()
        tokenManager.saveAuthToken(token)

        assertEquals(token, tokenManager.getAuthToken())
    }

    @Test
    fun `should return null if no taken has been saved`() {
        assertNull(tokenManager.getAuthToken())
    }

    @Test
    fun `clear authAuthToken should delete the token`() {
        val token = faker.random.randomString()
        tokenManager.saveAuthToken(token)
        assertEquals(token, tokenManager.getAuthToken())

        tokenManager.clearAuthToken()
        assertNull(tokenManager.getAuthToken())
    }
}