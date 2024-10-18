package io.capibaras.abcall.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import io.capibaras.abcall.BuildConfig

class TokenManager(
    context: Context,
    masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
    isDebug: Boolean = BuildConfig.DEBUG,
) {
    private val sharedPreferences = if (isDebug) {
        context.getSharedPreferences("test_prefs", Context.MODE_PRIVATE)
    } else {
        EncryptedSharedPreferences.create(
            "secure_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveAuthToken(token: String) {
        sharedPreferences.edit().putString("auth_token", token).apply()
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }

    fun clearAuthToken() {
        sharedPreferences.edit().remove("auth_token").apply()
    }
}
