package io.capibaras.abcall.di

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.GsonBuilder
import io.capibaras.abcall.BuildConfig
import io.capibaras.abcall.data.LogoutManager
import io.capibaras.abcall.data.TokenManager
import io.capibaras.abcall.data.database.ABCallDB
import io.capibaras.abcall.data.database.dao.CompanyDAO
import io.capibaras.abcall.data.database.dao.IncidentDAO
import io.capibaras.abcall.data.database.dao.UserDAO
import io.capibaras.abcall.data.network.interceptors.AuthInterceptor
import io.capibaras.abcall.data.network.services.AuthService
import io.capibaras.abcall.data.network.services.CompanyService
import io.capibaras.abcall.data.network.services.IncidentsService
import io.capibaras.abcall.data.network.services.UsersService
import io.capibaras.abcall.data.repositories.AuthRepository
import io.capibaras.abcall.data.repositories.CompanyRepository
import io.capibaras.abcall.data.repositories.IncidentsRepository
import io.capibaras.abcall.data.repositories.UsersRepository
import io.capibaras.abcall.ui.util.StateMediator
import io.capibaras.abcall.ui.viewmodels.AccountViewModel
import io.capibaras.abcall.ui.viewmodels.IncidentViewModel
import io.capibaras.abcall.ui.viewmodels.LoginViewModel
import io.capibaras.abcall.ui.viewmodels.NavigationViewModel
import io.capibaras.abcall.ui.viewmodels.SignUpViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
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
    single { get<ABCallDB>().userDAO() }
    single { get<ABCallDB>().incidentDAO() }

    single { TokenManager(get()) }
    single { LogoutManager() }

    single { Dispatchers.IO }

    single {
        AuthInterceptor(
            get<TokenManager>(),
            get<LogoutManager>(),
            get<CoroutineDispatcher>()
        )
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(get()))
            .client(get<OkHttpClient>())
            .build()

    }

    single { get<Retrofit>().create(AuthService::class.java) }
    single { get<Retrofit>().create(CompanyService::class.java) }
    single { get<Retrofit>().create(UsersService::class.java) }
    single { get<Retrofit>().create(IncidentsService::class.java) }

    single { AuthRepository(get<AuthService>()) }
    single { CompanyRepository(get<CompanyDAO>(), get<CompanyService>()) }
    single { UsersRepository(get<UsersService>(), get<UserDAO>(), get<CompanyRepository>()) }
    single { IncidentsRepository(get<IncidentDAO>(), get<IncidentsService>()) }

    single { StateMediator() }

    viewModel {
        NavigationViewModel(
            get<StateMediator>(),
            get<UsersRepository>(),
            get<TokenManager>(),
            get<LogoutManager>()
        )
    }
    viewModel {
        SignUpViewModel(
            get<StateMediator>(),
            get<CompanyRepository>(),
            get<UsersRepository>()
        )
    }
    viewModel { LoginViewModel(get<StateMediator>(), get<TokenManager>(), get<AuthRepository>()) }
    viewModel {
        AccountViewModel(
            get<StateMediator>(),
            get<LogoutManager>(),
            get<UsersRepository>()
        )
    }
    viewModel {
        IncidentViewModel(
            get<IncidentsRepository>(),
            get<StateMediator>(),
        )
    }

}
