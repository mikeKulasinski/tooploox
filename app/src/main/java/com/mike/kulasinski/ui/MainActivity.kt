package com.mike.kulasinski.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mike.kulasinski.App
import com.mike.kulasinski.R
import com.mike.kulasinski.logic.SongEvent
import com.mike.kulasinski.logic.SongFeature
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.Subject
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var feature: SongFeature
    @Inject
    lateinit var events: Subject<Any>

    private val disposable = CompositeDisposable()

    private lateinit var uiBinder: MainUiBinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).mainComponent.inject(this)

        setContentView(R.layout.activity_main)

        uiBinder = MainUiBinder(this, events)

        disposable.add(
            feature
                .states()
                .map(ViewModelTransformer)
                .subscribe(uiBinder)
        )

        events.onNext(SongEvent.Start)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}

