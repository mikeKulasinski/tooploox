package com.mike.kulasinski.ui

import android.widget.RadioGroup
import com.mike.kulasinski.R
import com.mike.kulasinski.logic.SongEvent
import io.reactivex.subjects.Subject

class RadioGroupBinder(
    private val radioGroup: RadioGroup,
    private val events: Subject<Any>
) : (ViewModel.SelectedSource) -> Unit {

    private val changeListener = ChangeListener()

    init {
        radioGroup.setOnCheckedChangeListener(changeListener)
    }

    override fun invoke(source: ViewModel.SelectedSource) {
        radioGroup.setOnCheckedChangeListener(null)
        radioGroup.check(
            when (source) {
                ViewModel.SelectedSource.Remote -> R.id.radioRemote
                ViewModel.SelectedSource.Local -> R.id.radioLocal
                ViewModel.SelectedSource.Both -> R.id.radioBoth
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