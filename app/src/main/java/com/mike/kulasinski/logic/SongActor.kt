package com.mike.kulasinski.logic

import com.mike.kulasinski.logic.SongAction.SelectSource.*
import com.mike.kulasinski.logic.SongAction.Start
import com.mike.kulasinski.logic.SongDataSource.Request
import com.mike.kulasinski.logic.SongDataSource.Response
import com.mike.kulasinski.logic.SongEffect.LoadSource
import com.mike.kulasinski.logic.SongState.LoadStatus.LOADING
import com.mike.kulasinski.logic.SongState.LoadStatus.SUCCESS
import com.mike.kulasinski.logic.SongState.SourceType
import com.mike.kulasinski.logic.SongState.SourceType.LOCAL
import com.mike.kulasinski.logic.SongState.SourceType.REMOTE
import com.mike.kulasinski.logic.base.Actor
import com.mike.kulasinski.logic.base.ReducerWrapper
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Observable.empty

class SongActor(
    private val dataSource: Map<SourceType, SongDataSource>,
    private val reducer: ReducerWrapper<SongState, SongEffect>,
    private val state: Observable<SongState>
) : Actor<SongAction> {
    override fun invoke(action: SongAction): Completable = when (action) {
        Start -> start()
        Remote -> requestLoadSingle(source = REMOTE)
        Local -> requestLoadSingle(source = LOCAL)
        All -> requestLoadAll()
    }

    private fun start() = state
        .firstOrError()
        .toObservable()
        .flatMapIterable { it.currentSource }
        .flatMapCompletable { requestLoadSingle(source = it) }

    private fun requestLoadAll() = reducer(SongEffect.SourceSelected.All)
        .flatMapIterable { it.currentSource }
        .flatMapCompletable { performLoading(source = it, dataSource = dataSource.getValue(it)) }

    private fun requestLoadSingle(source: SourceType) = reducer(source.toSourceEffect())
        .switchMapCompletable { performLoading(source, dataSource.getValue(source)) }

    private fun performLoading(source: SourceType, dataSource: SongDataSource) = state
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
        when {
            songState.loadStatus[source] == LOADING -> empty()
            songState.loadStatus[source] == SUCCESS -> empty()
            else -> reducer(LoadSource.Started(source))
                .switchMap { dataSource.invoke(Request.Load) }
        }

    private fun mapResponseToEffect(it: Response, source: SourceType): LoadSource {
        return when (it) {
            is Response.Success -> LoadSource.Success(source, it.songs)
            Response.Problem -> LoadSource.Problem(source)
        }
    }

    private fun SourceType.toSourceEffect() = when (this) {
        LOCAL -> SongEffect.SourceSelected.Local
        REMOTE -> SongEffect.SourceSelected.Remote
    }
}