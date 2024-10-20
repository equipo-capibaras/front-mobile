package io.capibaras.abcall.data.network.interceptors

import io.capibaras.abcall.data.LogoutManager
import io.capibaras.abcall.data.TokenManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject

class AuthInterceptor(
    private val tokenManager: TokenManager,
    private val logoutManager: LogoutManager,
    private val dispatcher: CoroutineDispatcher
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestPath = request.url().encodedPath().removePrefix("/api/v1")
        val excludedPaths = listOf("/users", "/auth/user", "/clients")
        val token = tokenManager.getAuthToken()

        if (token.isNullOrEmpty() && !excludedPaths.any { requestPath == it }) {
            CoroutineScope(dispatcher).launch {
                logoutManager.logout(isExpiredToken = true)
            }
        }

        val response = if (excludedPaths.any { requestPath == it }) {
            chain.proceed(request)
        } else {
            val newRequest = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        }

        if (response.code() == 401) {
            val errorBody = response.peekBody(Long.MAX_VALUE).string()
            try {
                val jsonObject = JSONObject(errorBody)
                val message = jsonObject.getString("message")

                if (message.contains("Jwt is expired")) {
                    CoroutineScope(dispatcher).launch {
                        logoutManager.logout(isExpiredToken = true)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return response
    }
}