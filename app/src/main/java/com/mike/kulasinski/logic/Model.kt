package com.mike.kulasinski.logic

sealed class SongEvent {
    object Start : SongEvent()
    sealed class SelectSource : SongEvent() {
        object Remote : SelectSource()
        object Local : SelectSource()
        object Both : SelectSource()
    }
}

sealed class SongAction {
    object Start : SongAction()
    sealed class SelectSource : SongAction() {
        object Remote : SelectSource()
        object Local : SelectSource()
        object All : SelectSource()
    }
}

sealed class SongEffect {

    sealed class SourceSelected : SongEffect() {
        object Remote : SourceSelected()
        object Local : SourceSelected()
        object Both : SourceSelected()
    }

    sealed class LoadSource : SongEffect() {
        data class Started(val source: SongState.SourceType) : LoadSource()
        data class Problem(val source: SongState.SourceType) : LoadSource()
        data class Success(
            val source: SongState.SourceType,
            val songs: List<Song>
        ) : LoadSource()
    }
}

data class Song(
    val title: String,
    val artist: String,
    val releaseYear: String
)

data class SongState(
    val currentSource: List<SourceType> = listOf(SourceType.LOCAL),
    val loadStatus: Map<SourceType, LoadStatus> = mapOf(
        SourceType.LOCAL to LoadStatus.NONE,
        SourceType.REMOTE to LoadStatus.NONE
    ),
    val songs: Map<SourceType, List<Song>> = emptyMap()
) {
    enum class LoadStatus {
        NONE,
        LOADING,
        SUCCESS,
        PROBLEM
    }

    enum class SourceType {
        LOCAL,
        REMOTE
    }
}

