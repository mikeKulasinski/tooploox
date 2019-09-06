package com.mike.kulasinski.logic

import android.app.Application
import com.mike.kulasinski.common.ReducerWrapper
import com.mike.kulasinski.logic.SongState.SourceType.LOCAL
import com.mike.kulasinski.logic.SongState.SourceType.REMOTE
import com.mike.kulasinski.logic.datasource.local.LocalDataSource
import com.mike.kulasinski.logic.datasource.server.ServerDataSource
import com.mike.kulasinski.logic.datasource.server.SongService
import dagger.Module
import dagger.Provides
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class LogicModule {
    companion object {
        const val API_BASE_URL = "https://itunes.apple.com"
    }

    @Provides
    @Singleton
    fun provideEvents(): Subject<Any> = PublishSubject.create<Any>()

    @Provides
    @Singleton
    fun provideSongService(): SongService = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(SongService::class.java)

    @Provides
    @Singleton
    fun provideSongState(): Subject<SongState> = BehaviorSubject.createDefault(SongState())

    @Provides
    @Singleton
    fun provideSongFeature(
        stateStore: Subject<SongState>,
        events: Subject<Any>,
        songService: SongService,
        application: Application
    ) = SongFeature(
        actor = SongActor(
            dataSource = mapOf(
                REMOTE to ServerDataSource(songService),
                LOCAL to LocalDataSource(application)
            ),
            reducer = ReducerWrapper(
                reducer = SongReducer(),
                stateStore = stateStore
            ),
            state = stateStore
        ),
        states = stateStore,
        events = events.ofType(SongEvent::class.java)
    )
}