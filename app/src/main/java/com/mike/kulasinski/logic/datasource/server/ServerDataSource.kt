package com.mike.kulasinski.logic.datasource.server

import com.mike.kulasinski.logic.Song
import com.mike.kulasinski.logic.SongDataSource
import com.mike.kulasinski.logic.SongDataSource.Request
import com.mike.kulasinski.logic.SongDataSource.Response
import com.mike.kulasinski.logic.SongDataSource.Response.Success
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ServerDataSource(private val songService: SongService) : SongDataSource {

    override fun invoke(request: Request): Observable<Response> =
        when (request) {
            Request.Load -> songService
                .loadSongs()
                .subscribeOn(Schedulers.io())
                .map { it.toResponse() }
                .cast(Response::class.java)
                .onErrorReturnItem(Response.Problem)
                .observeOn(AndroidSchedulers.mainThread())
        }

    private fun ResponseJson.toResponse() = Success(
        songs = this
            .results
            .filter { it.releaseYear != null }
            .map {
                Song(
                    title = it.title,
                    artist = it.artist,
                    releaseYear = convertToYear(it.releaseYear!!)
                )
            }
    )

    private fun convertToYear(date: String) = date.substring(0, 4)
}