package com.mike.kulasinski.ui

import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mike.kulasinski.App
import com.mike.kulasinski.R
import com.mike.kulasinski.logic.SongEvent
import com.mike.kulasinski.logic.SongFeature
import com.mike.kulasinski.ui.ViewModel.*
import com.mike.kulasinski.ui.ViewModel.Informative.ProblemWithOneOfSources
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    companion object {
        const val INDEX_ERROR = 0
        const val INDEX_LOADING = 1
        const val INDEX_DATA = 2
    }

    @Inject
    lateinit var feature: SongFeature
    @Inject
    lateinit var events: Subject<Any>

    private val adapter = Adapter()
    private lateinit var radioGroupBinder: RadioGroupBinder

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).mainComponent.inject(this)

        setContentView(R.layout.activity_main)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        radioGroupBinder = RadioGroupBinder(radioGroup, events)

        disposable.add(
            feature
                .states()
                .map(ViewModelTransformer)
                .subscribe(this::bind)
        )

        events.onNext(SongEvent.Start)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun bind(models: List<ViewModel>) {
        models
            .forEach {
                when (it) {
                    Loading -> viewFlipper.displayedChild = INDEX_LOADING
                    LoadingProblem -> viewFlipper.displayedChild = INDEX_ERROR
                    is ProblemWithOneOfSources -> Toast.makeText(
                        this,
                        R.string.error_partial,
                        Toast.LENGTH_SHORT
                    ).show()
                    is SongsArrived -> {
                        viewFlipper.displayedChild = INDEX_DATA
                        adapter.bind(it.songs)
                    }
                    is SelectedSource -> radioGroupBinder(it)
                }
            }
    }
}

class RadioGroupBinder(
    val radioGroup: RadioGroup,
    val events: Subject<Any>
) : (SelectedSource) -> Unit {

    private val changeListener = ChangeListener()

    init {
        radioGroup.setOnCheckedChangeListener(changeListener)
    }

    override fun invoke(source: SelectedSource) {
        radioGroup.setOnCheckedChangeListener(null)
        radioGroup.check(
            when (source) {
                SelectedSource.Remote -> R.id.radioRemote
                SelectedSource.Local -> R.id.radioLocal
                SelectedSource.Both -> R.id.radioBoth
            }
        )
        radioGroup.setOnCheckedChangeListener(changeListener)
    }

    private inner class ChangeListener : RadioGroup.OnCheckedChangeListener {
        override fun onCheckedChanged(radioGroup: RadioGroup, resource: Int) {
            events.onNext(
                when (resource) {
                    R.id.radioRemote -> SongEvent.SelectSource.Remote
                    R.id.radioLocal -> SongEvent.SelectSource.Local
                    R.id.radioBoth -> SongEvent.SelectSource.Both
                    else -> throw IllegalStateException()
                }
            )
        }
    }
}