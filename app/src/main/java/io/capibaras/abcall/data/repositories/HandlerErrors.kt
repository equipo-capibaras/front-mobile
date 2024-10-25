package io.capibaras.abcall.data.repositories

import org.json.JSONObject

fun <T> handleErrorResponse(localData: List<T>, errorBody: String?): Result<List<T>> {
    return if (localData.isNotEmpty()) {
        Result.success(localData)
    } else {
        val message = errorBody?.let {
            JSONObject(it).optString("message", "Unknown error")
        } ?: "Unknown error"
        Result.failure(RepositoryError.CustomError(message))
    }
}

fun <T> handleNetworkAndLocalDBFailure(
    localData: List<T>,
    defaultError: Throwable
): Result<List<T>> {
    return if (localData.isNotEmpty()) {
        Result.success(localData)
    } else {
        Result.failure(defaultError)
    }
}
