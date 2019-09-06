package com.mike.kulasinski.logic.datasource.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.mike.kulasinski.logic.Song
import com.mike.kulasinski.logic.SongDataSource
import com.mike.kulasinski.logic.SongDataSource.Request.Load
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.InputStreamReader

data class SongInfoJson(
    @SerializedName("Song Clean")
    val title: String,
    @SerializedName("ARTIST CLEAN")
    val artist: String,
    @SerializedName("Release Year")
    val releaseYear: String?
)

class LocalDataSource(private val context: Context) : SongDataSource {
    override fun invoke(request: SongDataSource.Request): Observable<SongDataSource.Response> =
        when (request) {
            Load -> Observable
                .just("local.json")
                .observeOn(Schedulers.io())
                .map {
                    Gson()
                        .fromJson(
                            InputStreamReader(context.resources.assets.open(it)),
                            Array<SongInfoJson>::class.java
                        )
                        .toList()
                        .map {
                            Song(
                                title = it.title,
                                artist = it.artist,
                                releaseYear = it.releaseYear.toString()
                            )
                        }
                }
                .map { SongDataSource.Response.Success(it) }
                .observeOn(AndroidSchedulers.mainThread())
//                                .onErrorReturnItem(Response.Problem)
                .cast(SongDataSource.Response::class.java)
        }
}