package com.mike.kulasinski.logic

import com.mike.kulasinski.logic.SongState.SourceType.LOCAL
import com.mike.kulasinski.logic.SongState.SourceType.REMOTE
import com.mike.kulasinski.logic.base.ReducerWrapper
import dagger.Module
import dagger.Provides
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Singleton

@Module
class LogicModule {
    @Provides
    @Singleton
    fun provideEvents(): Subject<Any> = PublishSubject.create<Any>()

    @Provides
    @Singleton
    fun provideSongState(): Subject<SongState> = BehaviorSubject.createDefault(SongState())

    @Provides
    @Singleton
    fun provideSongFeature(
        stateStore: Subject<SongState>,
        events: Subject<Any>
    ) = SongFeature(
        actor = SongActor(
            dataSource = mapOf(
                REMOTE to TestRemoteLong(),
                LOCAL to TestRemote()
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