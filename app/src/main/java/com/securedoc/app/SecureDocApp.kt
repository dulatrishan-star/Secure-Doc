package com.securedoc.app

import android.app.Application
import com.securedoc.app.data.AppDatabase

class SecureDocApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.initialize(this)
    }
}
