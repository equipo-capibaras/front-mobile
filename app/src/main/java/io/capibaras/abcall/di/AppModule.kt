package io.capibaras.abcall.di

import io.capibaras.abcall.viewmodels.LoginViewModel
import io.capibaras.abcall.viewmodels.SignUpViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { SignUpViewModel() }
    viewModel { LoginViewModel() }
}
