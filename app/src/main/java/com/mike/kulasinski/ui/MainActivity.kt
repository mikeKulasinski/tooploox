package com.mike.kulasinski.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mike.kulasinski.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val INDEX_LOADING = 0
        const val INDEX_DATA = 1
    }

    private val adapter = Adapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.bind(
            listOf(
                SongModel("title", "artist", "233"),
                SongModel("title", "artist", "2333")
            )
        )

        viewFlipper.displayedChild = INDEX_DATA
    }
}
