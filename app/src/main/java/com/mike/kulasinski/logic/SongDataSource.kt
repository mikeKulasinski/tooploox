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