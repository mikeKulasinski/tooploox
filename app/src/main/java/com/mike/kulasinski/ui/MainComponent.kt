package com.mike.kulasinski.ui

import android.app.Application
import com.mike.kulasinski.logic.LogicModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [LogicModule::class])
interface MainComponent {
    fun inject(context: MainActivity)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): MainComponent
    }
}