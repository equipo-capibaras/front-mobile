package io.capibaras.abcall.di

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.GsonBuilder
import io.capibaras.abcall.BuildConfig
import io.capibaras.abcall.data.TokenManager
import io.capibaras.abcall.data.database.ABCallDB
import io.capibaras.abcall.data.network.services.AuthService
import io.capibaras.abcall.data.network.services.CompanyService
import io.capibaras.abcall.data.network.services.UsersService
import io.capibaras.abcall.data.repositories.AuthRepository
import io.capibaras.abcall.data.repositories.CompanyRepository
import io.capibaras.abcall.data.repositories.UsersRepository
import io.capibaras.abcall.viewmodels.LoginViewModel
import io.capibaras.abcall.viewmodels.MainActivityViewModel
import io.capibaras.abcall.viewmodels.SignUpViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val appModule = module {
    single { ABCallDB.getDatabase(androidContext()) }

    single<SharedPreferences> {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            "abcall_prefs",
            masterKeyAlias,
            androidContext(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    single {
        GsonBuilder().create()
    }

    single { get<ABCallDB>().companyDAO() }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()

    }
    single { get<Retrofit>().create(AuthService::class.java) }
    single { get<Retrofit>().create(CompanyService::class.java) }
    single { get<Retrofit>().create(UsersService::class.java) }

    single { TokenManager(get()) }

    single { AuthRepository(get()) }
    single { CompanyRepository(get(), get(), get()) }
    single { UsersRepository(get()) }

    viewModel { MainActivityViewModel(get()) }
    viewModel { SignUpViewModel(get(), get()) }
    viewModel { LoginViewModel(get(), get()) }
}
