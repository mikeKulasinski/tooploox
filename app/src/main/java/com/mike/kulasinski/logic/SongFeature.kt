package com.mike.kulasinski.logic

import com.mike.kulasinski.logic.SongEvent.SelectSource.*
import com.mike.kulasinski.logic.SongEvent.Start
import com.mike.kulasinski.logic.base.Actor
import com.mike.kulasinski.logic.base.BaseFeature
import io.reactivex.Observable

class SongFeature(
    actor: Actor<SongAction>,
    states: Observable<SongState>,
    events: Observable<SongEvent>?
) : BaseFeature<SongEvent, SongAction, SongState>(
    actor = actor,
    states = states,
    events = events,
    eventToAction = {
        Observable.just(
            when (it) {
                Start -> SongAction.Start
                Remote -> SongAction.SelectSource.Remote
                Local -> SongAction.SelectSource.Local
                Both -> SongAction.SelectSource.All
            }
        )
    }
)