package com.example.alphabetlauncher.di

import com.example.alphabetlauncher.data.AppRepository
import com.example.alphabetlauncher.data.DefaultAppRepository
import com.example.alphabetlauncher.ui.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<AppRepository> { DefaultAppRepository(get()) }
    viewModel { HomeViewModel(get()) }
}
