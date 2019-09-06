package com.mike.kulasinski.logic

import io.reactivex.Observable
import io.reactivex.Observable.just
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

interface SongDataSource : (SongDataSource.Request) -> Observable<SongDataSource.Response> {
    sealed class Request {
        object Load : Request()
    }

    sealed class Response {
        data class Success(val songs: List<Song>) : Response()
        object Problem : Response()
    }
}

class TestRemote : SongDataSource {
    override fun invoke(request: SongDataSource.Request) = just(
        SongDataSource.Response.Success(
            listOf(Song("local", "ar", "123"))
        )
    ).cast(SongDataSource.Response::class.java)!!
}

class TestRemoteLong : SongDataSource {
    override fun invoke(request: SongDataSource.Request) = just(
        SongDataSource.Response.Success(
            listOf(Song("remote", "ar", "321"))
        )
    )
        .delay(5, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .cast(SongDataSource.Response::class.java)!!
}