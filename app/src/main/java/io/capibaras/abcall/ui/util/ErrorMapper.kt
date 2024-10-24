package io.capibaras.abcall.ui.util

import io.capibaras.abcall.R
import io.capibaras.abcall.data.repositories.IncidentsRepositoryError
import io.capibaras.abcall.data.repositories.RepositoryError


sealed class ErrorMessage {
    data class Res(val resId: Int) : ErrorMessage()
    data class Text(val message: String) : ErrorMessage()
}

fun mapErrorToMessage(error: Throwable): ErrorMessage {
    return when (error) {
        is RepositoryError.NetworkError -> ErrorMessage.Res(R.string.error_network)
        is RepositoryError.ServerError -> ErrorMessage.Res(R.string.error_server)
        is RepositoryError.CustomError -> ErrorMessage.Text(error.message)
        is IncidentsRepositoryError.GetIncidentsError -> ErrorMessage.Res(R.string.error_getting_incidents)
        else -> ErrorMessage.Res(R.string.unknown_error)
    }
}