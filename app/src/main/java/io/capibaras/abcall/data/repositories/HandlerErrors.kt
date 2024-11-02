package io.capibaras.abcall.data.repositories

import org.json.JSONObject

private fun <T> isValidData(data: T?): Boolean =
    when (data) {
        is List<*> -> data.isNotEmpty()
        else -> data != null
    }

fun <T> handleErrorResponse(localData: T?, errorBody: String?): Result<T> {
    return if (isValidData(localData)) {
        Result.success(localData!!)
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
    return if (isValidData(localData)) {
        Result.success(localData!!)
    } else {
        Result.failure(defaultError)
    }
}
