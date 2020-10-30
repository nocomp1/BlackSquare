package com.example.blacksquare.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blacksquare.Models.PopUpMainEditMenu.RotaryKnobType


class DrumScreenViewModel : ViewModel() {

    private val _events: MutableLiveData<Events> = MediatorLiveData()
    val event: LiveData<Events> get() = _events

    sealed class Events {
        data class SetEditRotaryKnobPosition(val value: Int, val type: RotaryKnobType) : Events()
    }

    fun setEditKnobProgress(valueOne: Float, valueTwo: Float) {

        val progress: Int
        //This logic is for volume
        if (valueOne == valueTwo) {
            progress = (valueTwo * 100).toInt()
            _events.postValue(
                Events.SetEditRotaryKnobPosition(
                    progress,
                    RotaryKnobType.Volume()
                )
            )
            //volumeSeekBar.progress = progress
        }
    }

}