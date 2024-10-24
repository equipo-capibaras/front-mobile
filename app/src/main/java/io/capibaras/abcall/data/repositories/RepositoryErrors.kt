package io.capibaras.abcall.data.repositories

sealed class RepositoryError : Exception() {
    data object NetworkError : RepositoryError() {
        private fun readResolve(): Any = NetworkError
    }

    data object ServerError : RepositoryError() {
        private fun readResolve(): Any = ServerError
    }

    data object UnknownError : RepositoryError() {
        private fun readResolve(): Any = UnknownError
    }

    data class CustomError(override val message: String) : RepositoryError()
}
