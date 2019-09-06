package com.mike.kulasinski.ui

import com.mike.kulasinski.logic.Song
import com.mike.kulasinski.logic.SongState
import com.mike.kulasinski.logic.SongState.LoadStatus.*
import com.mike.kulasinski.logic.SongState.SourceType.LOCAL
import com.mike.kulasinski.logic.SongState.SourceType.REMOTE
import com.mike.kulasinski.ui.ViewModel.SelectedSource.*

object ViewModelTransformer : (SongState) -> List<ViewModel> {
    override fun invoke(state: SongState) =
        when {
            state.isError() -> listOf(ViewModel.LoadingProblem)
            state.isAllNone() -> emptyList()
            else -> mutableListOf<ViewModel>()
                .apply { add(state.toContent()) }
                .apply { addAll(state.toInformativeError()) }
        }
            .toMutableList()
            .apply {
                add(state.toSource())
            }

    private fun SongState.isError() = currentSource
        .map { loadStatus.getValue(it) }
        .all { it == PROBLEM }

    private fun SongState.toInformativeError() = currentSource
        .filter { loadStatus.getValue(it) == PROBLEM }
        .map { ViewModel.Informative.ProblemWithOneOfSources }

    private fun SongState.toContent() =
        if (isLoading()) ViewModel.Loading
        else ViewModel.SongsArrived(toListOfSongs())

    private fun SongState.isLoading() = currentSource
        .any { loadStatus.getValue(it) == LOADING }

    private fun SongState.isAllNone() = currentSource
        .all { loadStatus.getValue(it) == NONE }

    private fun SongState.toListOfSongs() = currentSource
        .filter { loadStatus.getValue(it) == SUCCESS }
        .mapNotNull { songs[it]?.map { song -> song.toSongModel() } }
        .flatten()
        .toList()

    private fun Song.toSongModel() = SongModel(
        title = title,
        artist = artist,
        releaseYear = releaseYear
    )

    private fun SongState.toSource() = when {
        currentSource.size > 1 -> Both
        else -> when (currentSource[0]) {
            LOCAL -> Local
            REMOTE -> Remote
        }
    }
}