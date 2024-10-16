package io.capibaras.abcall.data.network.interceptors

import io.capibaras.abcall.data.LogoutManager
import io.capibaras.abcall.data.TokenManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject

class AuthInterceptor(
    private val tokenManager: TokenManager,
    private val logoutManager: LogoutManager
) : Interceptor {
    @OptIn(DelicateCoroutinesApi::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestPath = request.url().encodedPath().removePrefix("/api/v1")
        val excludedPaths = listOf("users", "auth/user", "clients")


        val response = if (excludedPaths.any { requestPath.startsWith(it) }) {
            chain.proceed(request)
        } else {
            val token = tokenManager.getAuthToken()
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
                    GlobalScope.launch {
                        logoutManager.logout()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return response
    }
}