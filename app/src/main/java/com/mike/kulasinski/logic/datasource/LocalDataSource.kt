package com.mike.kulasinski.logic.datasource

import com.mike.kulasinski.logic.SongDataSource
import io.reactivex.Observable

class LocalDataSource : SongDataSource {
    override fun invoke(request: SongDataSource.Request): Observable<SongDataSource.Response> {
        TODO()
    }
}