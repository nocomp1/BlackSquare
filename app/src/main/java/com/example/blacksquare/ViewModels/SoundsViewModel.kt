package com.example.blacksquare.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blacksquare.Managers.DrumPadSoundPool
import com.example.blacksquare.Objects.NoteRepeat
import com.example.blacksquare.Objects.PadSequenceTimeStamp

class SoundsViewModel : ViewModel() {
    val drumPadSequenceNoteList = MutableLiveData<ArrayList<ArrayList<PadSequenceTimeStamp>>>()
    val noteRepeatInterval = MutableLiveData<NoteRepeat>()
}