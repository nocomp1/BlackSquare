package com.example.blacksquare.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blacksquare.Managers.DrumPadSoundPool
import com.example.blacksquare.Objects.NoteRepeat

class SoundsViewModel : ViewModel() {
    val drumPadSoundPool = MutableLiveData<DrumPadSoundPool>()
    val noteRepeatInterval = MutableLiveData<NoteRepeat>()
}