package com.mike.kulasinski

import android.app.Application
import com.mike.kulasinski.ui.DaggerMainComponent
import com.mike.kulasinski.ui.MainComponent

class App : Application() {

    lateinit var mainComponent: MainComponent

    override fun onCreate() {
        super.onCreate()
        mainComponent = DaggerMainComponent
            .builder()
            .application(this)
            .build()
    }
}