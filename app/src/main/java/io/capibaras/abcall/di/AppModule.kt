package io.capibaras.abcall.di

import com.google.gson.GsonBuilder
import io.capibaras.abcall.BuildConfig
import io.capibaras.abcall.data.TokenManager
import io.capibaras.abcall.data.network.services.AuthService
import io.capibaras.abcall.data.repositories.AuthRepository
import io.capibaras.abcall.viewmodels.LoginViewModel
import io.capibaras.abcall.viewmodels.SignUpViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val appModule = module {
    single {
        GsonBuilder().create()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }

    single { get<Retrofit>().create(AuthService::class.java) }

    single { TokenManager(get()) }

    single { AuthRepository(get()) }

    viewModel { SignUpViewModel() }
    viewModel { LoginViewModel(get(), get()) }
}
