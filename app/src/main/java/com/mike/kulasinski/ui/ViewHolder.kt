package com.mike.kulasinski.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mike.kulasinski.R

class ViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(view.context).inflate(R.layout.song_item, view, false)
) {
    private val titleView: TextView = itemView.findViewById(R.id.title)
    private val artistView: TextView = itemView.findViewById(R.id.artist)
    private val releaseYearView: TextView = itemView.findViewById(R.id.releaseYear)

    fun bind(model: SongModel) {
        titleView.text = model.title
        artistView.text = model.artist
        releaseYearView.text = model.releaseYear
    }
}