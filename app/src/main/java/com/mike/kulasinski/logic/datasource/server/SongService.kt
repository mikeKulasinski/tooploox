package com.mike.kulasinski.logic.datasource.server

import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import retrofit2.http.GET

data class ResponseJson(
    val results: List<SongInfo>
) {
    data class SongInfo(
        @SerializedName("artistName")
        val title: String,
        @SerializedName("collectionName")
        val artist: String,
        @SerializedName("releaseDate")
        val releaseYear: String?
    )
}

interface SongService {

    @GET("/lookup?id=909253&entity=album")
    fun loadSongs(): Observable<ResponseJson>
}
