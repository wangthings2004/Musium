package com.jcxdc.musium

import android.app.Application
import com.jcxdc.musium.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication :Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@MyApplication)
            modules(databaseModule)
        }
    }
}