package io.capibaras.abcall.data.repositories

import org.json.JSONObject

fun <T> handleErrorResponse(localData: T?, errorBody: String?): Result<T> {
    return if ((localData is List<*> && localData.isNotEmpty()) || localData != null) {
        Result.success(localData)
    } else {
        val message = errorBody?.let {
            JSONObject(it).optString("message", "Unknown error")
        } ?: "Unknown error"
        Result.failure(RepositoryError.CustomError(message))
    }
}

fun <T> handleNetworkAndLocalDBFailure(
    localData: T?,
    defaultError: Throwable
): Result<T> {
    return if ((localData is List<*> && localData.isNotEmpty()) || localData != null)  {
        Result.success(localData)
    } else {
        Result.failure(defaultError)
    }
}
