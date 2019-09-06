package com.mike.kulasinski.ui

import com.mike.kulasinski.logic.SongState

sealed class ViewModel {
    sealed class SelectedSource : ViewModel() {
        object Remote : SelectedSource()
        object Local : SelectedSource()
        object Both : SelectedSource()
    }
    object Loading : ViewModel()
    // all sources failed
    object LoadingProblem : ViewModel()

    sealed class Informative : ViewModel() {
        object ProblemWithOneOfSources : Informative()
    }

    data class SongsArrived(val songs: List<SongModel>) : ViewModel()
}