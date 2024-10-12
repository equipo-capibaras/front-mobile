package io.capibaras.abcall.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import io.capibaras.abcall.BuildConfig
import io.capibaras.abcall.data.TokenManager
import io.capibaras.abcall.data.database.ABCallDB
import io.capibaras.abcall.data.network.services.AuthService
import io.capibaras.abcall.data.network.services.CompanyService
import io.capibaras.abcall.data.repositories.AuthRepository
import io.capibaras.abcall.data.repositories.CompanyRepository
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
        androidContext().getSharedPreferences("abcall_prefs", Context.MODE_PRIVATE)
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

    single { TokenManager(get()) }

    single { AuthRepository(get()) }
    single { CompanyRepository(get(), get(), get()) }

    viewModel { MainActivityViewModel(get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { LoginViewModel(get(), get()) }
}
