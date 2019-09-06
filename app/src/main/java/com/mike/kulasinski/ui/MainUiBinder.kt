package com.mike.kulasinski.ui

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.mike.kulasinski.R
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_main.*

class MainUiBinder(
    private val activity: MainActivity,
    events: Subject<Any>
) : (List<ViewModel>) -> Unit {

    companion object {
        const val INDEX_ERROR = 0
        const val INDEX_LOADING = 1
        const val INDEX_DATA = 2
    }

    private val adapter = Adapter()
    private val radioGroupBinder =
        RadioGroupBinder(activity.radioGroup, events)

    init {
        with(activity) {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }

    override fun invoke(models: List<ViewModel>) {
        models
            .forEach {
                when (it) {
                    ViewModel.Loading -> activity.viewFlipper.displayedChild = INDEX_LOADING
                    ViewModel.LoadingProblem -> activity.viewFlipper.displayedChild = INDEX_ERROR
                    is ViewModel.Informative.ProblemWithOneOfSources -> Toast.makeText(
                        activity,
                        R.string.error_partial,
                        Toast.LENGTH_SHORT
                    ).show()
                    is ViewModel.SongsArrived -> {
                        activity.viewFlipper.displayedChild = INDEX_DATA
                        adapter.bind(it.songs)
                    }
                    is ViewModel.SelectedSource -> radioGroupBinder(it)
                }
            }
    }

}