package com.example.sneakershopstorage

import android.app.Application
import com.example.sneakershopstorage.modules.module
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication() : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(module)
        }
    }
}