package com.mike.kulasinski.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class Adapter : RecyclerView.Adapter<ViewHolder>() {

    private val modelSong: MutableList<SongModel> = emptyList<SongModel>().toMutableList()

    fun bind(model: List<SongModel>) {
        modelSong.clear()
        modelSong.addAll(model)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun getItemCount(): Int = modelSong.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(modelSong[position])
    }
}