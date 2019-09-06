package com.mike.kulasinski.ui

import com.mike.kulasinski.logic.Song
import com.mike.kulasinski.logic.SongState
import com.mike.kulasinski.logic.SongState.LoadStatus.PROBLEM
import com.mike.kulasinski.logic.SongState.LoadStatus.SUCCESS

object ViewModelTransformer : (SongState) -> List<ViewModel> {
    override fun invoke(state: SongState) =
        if (state.isError()) listOf(ViewModel.LoadingProblem)
        else listOf<ViewModel>(state.toListOfSongs())
            .toMutableList()
            .apply { addAll(state.toInformativeError()) }

    private fun SongState.isError() = currentSource
        .map { loadStatus.getValue(it) }
        .all { it == PROBLEM }

    private fun SongState.toInformativeError() = currentSource
        .filter { loadStatus.getValue(it) == PROBLEM }
        .map { ViewModel.Informative.ProblemWithOneOfSources(it) }

    private fun SongState.toListOfSongs() = currentSource
        .filter { loadStatus.getValue(it) == SUCCESS }
        .map { songs.getValue(it).map { song -> song.toSongModel() } }
        .flatten()
        .toList()
        .let { ViewModel.SongsArrived(it) }

    private fun Song.toSongModel() = SongModel(
        title = title,
        artist = artist,
        releaseYear = releaseYear
    )
}