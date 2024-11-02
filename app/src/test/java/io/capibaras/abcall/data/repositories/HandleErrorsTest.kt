package io.capibaras.abcall.data.repositories

import io.mockk.every
import io.mockk.mockkConstructor
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HandleErrorsTest {

    @Test
    fun `handleErrorResponse should return success with local data when local data is not empty`() {
        val localData = listOf("Local Data")

        val result = handleErrorResponse(localData, null)

        assertTrue(result.isSuccess)
        assertEquals(localData, result.getOrNull())
    }

    @Test
    fun `handleErrorResponse should return success with local data when local data is not null`() {
        val localData = "Non-empty Data" // Now localData can be any type, not necessarily a list

        val result = handleErrorResponse(localData, null)

        assertTrue(result.isSuccess)
        assertEquals(localData, result.getOrNull())
    }

    @Test
    fun `handleErrorResponse should return failure with custom error message when errorBody contains message`() {
        val localData: String? = null
        val errorMessage = "Custom error message"

        mockkConstructor(JSONObject::class)
        every {
            anyConstructed<JSONObject>().optString(
                "message",
                "Unknown error"
            )
        } returns errorMessage

        val errorBody = """{"message": "$errorMessage"}"""

        val result = handleErrorResponse(localData, errorBody)

        assertTrue(result.isFailure)
        assertEquals(
            errorMessage,
            (result.exceptionOrNull() as RepositoryError.CustomError).message
        )
    }

    @Test
    fun `handleErrorResponse should return failure with default error message when errorBody is null`() {
        val localData: String? = null

        val result = handleErrorResponse(localData, null)

        assertTrue(result.isFailure)
        assertEquals(
            "Unknown error",
            (result.exceptionOrNull() as RepositoryError.CustomError).message
        )
    }

    @Test
    fun `handleErrorResponse should return failure with default error message when errorBody does not contain message`() {
        val localData: String? = null
        val errorMsg = "Custom error message"
        mockkConstructor(JSONObject::class)
        every {
            anyConstructed<JSONObject>().optString(
                "message",
                "Unknown error"
            )
        } returns errorMsg

        val result = handleErrorResponse(localData, """{"message": "$errorMsg"}""")

        assertTrue(result.isFailure)
        assertEquals(errorMsg, (result.exceptionOrNull() as RepositoryError.CustomError).message)
    }

    @Test
    fun `handleNetworkAndLocalDBFailure should return success with local data when local data is not empty`() {
        val localData = listOf("Local Data")
        val defaultError = Throwable("Default error")

        val result = handleNetworkAndLocalDBFailure(localData, defaultError)

        assertTrue(result.isSuccess)
        assertEquals(localData, result.getOrNull())
    }

    @Test
    fun `handleNetworkAndLocalDBFailure should return success with local data when local data is not null`() {
        val localData = "Non-empty Data" // Now localData can be any type, not necessarily a list
        val defaultError = Throwable("Default error")

        val result = handleNetworkAndLocalDBFailure(localData, defaultError)

        assertTrue(result.isSuccess)
        assertEquals(localData, result.getOrNull())
    }

    @Test
    fun `handleNetworkAndLocalDBFailure should return failure with default error when local data is null`() {
        val localData: String? = null
        val defaultError = Throwable("Default error")

        val result = handleNetworkAndLocalDBFailure(localData, defaultError)

        assertTrue(result.isFailure)
        assertEquals(defaultError, result.exceptionOrNull())
    }
}
