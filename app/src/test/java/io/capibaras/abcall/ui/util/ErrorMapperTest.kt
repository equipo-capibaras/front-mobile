package io.capibaras.abcall.ui.util

import io.capibaras.abcall.R
import io.capibaras.abcall.data.repositories.IncidentsRepositoryError
import io.capibaras.abcall.data.repositories.RepositoryError
import org.junit.Assert.assertEquals
import org.junit.Test

class MapErrorToMessageTest {
    @Test
    fun `should return network error message`() {
        val error = RepositoryError.NetworkError
        val result = mapErrorToMessage(error)
        assertEquals(ErrorMessage.Res(R.string.error_network), result)
    }

    @Test
    fun `should return server error message`() {
        val error = RepositoryError.ServerError
        val result = mapErrorToMessage(error)
        assertEquals(ErrorMessage.Res(R.string.error_server), result)
    }

    @Test
    fun `should return custom error message`() {
        val customMessage = "Custom error occurred"
        val error = RepositoryError.CustomError(customMessage)
        val result = mapErrorToMessage(error)
        assertEquals(ErrorMessage.Text(customMessage), result)
    }

    @Test
    fun `should return incidents error message`() {
        val error = IncidentsRepositoryError.GetIncidentsError
        val result = mapErrorToMessage(error)
        assertEquals(ErrorMessage.Res(R.string.error_getting_incidents), result)
    }

    @Test
    fun `should return unknown error message`() {
        val error = Exception("Unknown error")
        val result = mapErrorToMessage(error)
        assertEquals(ErrorMessage.Res(R.string.unknown_error), result)
    }
}
