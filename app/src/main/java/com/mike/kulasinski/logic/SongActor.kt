package com.mike.kulasinski.logic

import com.mike.kulasinski.logic.SongAction.SelectSource.*
import com.mike.kulasinski.logic.SongAction.Start
import com.mike.kulasinski.logic.SongDataSource.Request
import com.mike.kulasinski.logic.SongDataSource.Response
import com.mike.kulasinski.logic.SongEffect.LoadSource
import com.mike.kulasinski.logic.SongState.SourceType
import com.mike.kulasinski.logic.SongState.SourceType.REMOTE
import com.mike.kulasinski.logic.base.Actor
import com.mike.kulasinski.logic.base.ReducerWrapper
import io.reactivex.Completable
import io.reactivex.Observable

class SongActor(
    private val dataSource: Map<SourceType, SongDataSource>,
    private val reducer: ReducerWrapper<SongState, SongEffect>,
    private val state: Observable<SongState>
) : Actor<SongAction> {
    override fun invoke(action: SongAction): Completable = when (action) {
        Start -> start()
        Remote -> load(source = REMOTE, dataSource = dataSource.getValue(REMOTE))
        Local -> load(source = REMOTE, dataSource = dataSource.getValue(REMOTE))
        All -> loadAll()
    }

    private fun start() = state
        .firstOrError()
        .toObservable()
        .flatMapIterable { it.currentSource }
        .flatMapCompletable { load(source = it, dataSource = dataSource.getValue(it)) }

    private fun loadAll() = Observable.fromIterable(SourceType.values().toList())
        .flatMapCompletable { load(source = it, dataSource = dataSource.getValue(it)) }


    private fun load(source: SourceType, dataSource: SongDataSource) = state
        .firstOrError()
        .flatMapObservable { startLoadingFromSource(it, source, dataSource) }
        .map { mapResponseToEffect(it, source) }
        .flatMap { reducer(it) }
        .ignoreElements()

    private fun startLoadingFromSource(
        songState: SongState,
        source: SourceType,
        dataSource: SongDataSource
    ) =
        if (songState.loadStatus[source] == SongState.LoadStatus.LOADING) Observable.empty()
        else reducer
            .invoke(LoadSource.Started(source))
            .switchMap { dataSource.invoke(Request.Load) }

    private fun mapResponseToEffect(it: Response, source: SourceType): LoadSource {
        return when (it) {
            is Response.Success -> LoadSource.Success(source, it.songs)
            Response.Problem -> LoadSource.Problem(source)
        }
    }
}