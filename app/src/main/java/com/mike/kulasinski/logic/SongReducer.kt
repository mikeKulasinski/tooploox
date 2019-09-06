package com.mike.kulasinski.logic

import com.mike.kulasinski.logic.SongEffect.LoadSource.*
import com.mike.kulasinski.logic.SongEffect.SourceSelected.*
import com.mike.kulasinski.logic.SongState.LoadStatus.*
import com.mike.kulasinski.logic.SongState.SourceType.LOCAL
import com.mike.kulasinski.logic.SongState.SourceType.REMOTE
import com.mike.kulasinski.logic.base.Reducer

class SongReducer : Reducer<SongState, SongEffect> {
    override fun invoke(state: SongState, effect: SongEffect): SongState = when (effect) {
        Remote -> state.copy(currentSource = listOf(REMOTE))
        Local -> state.copy(currentSource = listOf(LOCAL))
        Both -> state.copy(currentSource = listOf(REMOTE, LOCAL))
        is Started -> state.copy(
            loadStatus = changeStatus(
                state = state,
                source = effect.source,
                status = LOADING
            )
        )
        is Problem -> state.copy(
            loadStatus = changeStatus(
                state = state,
                source = effect.source,
                status = PROBLEM
            )
        )
        is Success -> state.copy(
            loadStatus = changeStatus(
                state = state,
                source = effect.source,
                status = SUCCESS
            )
        )
    }

    private fun changeStatus(
        state: SongState,
        source: SongState.SourceType,
        status: SongState.LoadStatus
    ): MutableMap<SongState.SourceType, SongState.LoadStatus> {
        return state
            .loadStatus
            .toMutableMap()
            .apply { put(source, status) }
    }
}