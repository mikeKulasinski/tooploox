package com.mike.kulasinski.ui

import com.mike.kulasinski.logic.SongState

sealed class ViewModel {
    object Loading : ViewModel()
    // all sources failed
    object LoadingProblem : ViewModel()

    sealed class Informative : ViewModel() {
        data class ProblemWithOneOfSources(val sourceType: SongState.SourceType) : Informative()
    }

    data class SongsArrived(val songs: List<SongModel>) : ViewModel()
}