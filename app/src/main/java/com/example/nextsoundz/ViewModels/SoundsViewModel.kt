package com.example.nextsoundz.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nextsoundz.Managers.DrumPadSoundPool
import com.example.nextsoundz.Objects.NoteRepeat

class SoundsViewModel : ViewModel() {
    val drumPadSoundPool = MutableLiveData<DrumPadSoundPool>()
    val noteRepeatInterval = MutableLiveData<NoteRepeat>()
}