package com.example.alphabetlauncher

import android.app.Application
import com.example.alphabetlauncher.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AlphabetLauncherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AlphabetLauncherApp)
            modules(appModule)
        }
    }
}
