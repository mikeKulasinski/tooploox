package com.mike.kulasinski.common

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.Subject

interface GlobalEffect

interface Reducer<State, Effect> : (State, Effect) -> State

class ReducerWrapper<State, Effect>(
    private val reducer: Reducer<State, Effect>,
    private val stateStore: Subject<State>
) : (Effect) -> Observable<State> {


    override fun invoke(action: Effect): Observable<State> {
        return stateStore
            .firstElement()
            .map { reducer(it, action) }
            .doOnSuccess { stateStore.onNext(it) }
            .toObservable()
    }
}

interface Actor<Action> : (Action) -> Completable

open class BaseFeature<Event, Action, State>(
    private val actor: Actor<Action>,
    private val states: Observable<State>,
    events: Observable<Event>? = null,
    private val eventToAction: ((Event) -> Observable<Action>)? = null,
    effects: Subject<GlobalEffect>? = null,
    private val externalSource: ((GlobalEffect) -> Observable<Action>)? = null
) {
    private val disposable = CompositeDisposable()

    init {
        disposable.add(
            Observable.merge(
                events
                    ?.flatMap { eventToAction?.invoke(it) ?: Observable.empty() }
                    ?: Observable.empty(),
                effects
                    ?.flatMap { externalSource?.invoke(it) ?: Observable.empty() }
                    ?: Observable.empty()
            )
                .flatMapCompletable { actor.invoke(it) }
                .subscribe()
        )
    }

    fun states() = states.share()!!
}