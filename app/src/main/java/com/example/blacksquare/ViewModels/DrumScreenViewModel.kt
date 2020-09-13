package com.example.blacksquare.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DrumScreenViewModel : ViewModel() {

    private val _events: MutableLiveData<Events> = MediatorLiveData()
    val event: LiveData<Events> get() = _events

    sealed class Events {
        data class UpdateMainSliderProgress(val progress: Int) : Events()
    }

    fun setEditKnobProgress(valueOne: Float, valueTwo: Float) {

        val progress: Int
        //This logic is for volume
        if (valueOne == valueTwo) {
            progress = (valueTwo * 100).toInt()
            _events.postValue(Events.UpdateMainSliderProgress(progress))
            //volumeSeekBar.progress = progress
        }
    }

}