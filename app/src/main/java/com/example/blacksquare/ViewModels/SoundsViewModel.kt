package com.example.blacksquare.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blacksquare.Models.PadSequenceTimeStamp

class SoundsViewModel : ViewModel() {
    val drumPadSequenceNoteList = MutableLiveData<ArrayList<ArrayList<PadSequenceTimeStamp>>>()
    val playbackPadId = MutableLiveData<Int>()
    val mainSliderValue = MutableLiveData<Int>()
}