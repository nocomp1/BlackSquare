package com.example.blacksquare.Helpers

import android.util.ArrayMap
import com.example.blacksquare.Models.PadSequenceTimeStamp

class ApplicationState {

companion object {

    /**
     *
     * Application boolean flags
     */
    var drumNoteHasBeenRecorded: Boolean=false
    var isMillisecondClockPlaying: Boolean = false
    var noteRepeatHasChanged: Boolean = false
    var tempoHasChanged: Boolean = false
    var hasLoadedASound: Boolean = false
    var hasLoadedAKit: Boolean = true
    var isPlaying: Boolean = false
    var isCountInActivated : Boolean = true
    //this represents a state where recording is armed and play has been pressed
    var isRecording: Boolean = false
    var isArmedToRecord: Boolean = false
    var noteRepeatActive: Boolean = false

    //default bar count measure


    /**
     * Settings screen view ids
     */
    var selectedBarMeasureRadioButtonId = -1
    var selectedPatternRadioButtonId = -1
    var selectedInstrumentTrackRadioButtonId = -1
    var selectedDrumBankRadioButtonId = -1
    //default but get set in the  setting menu
    var countInCountPreference = 4
    var selectedPadId: Int? = null
    var selectedNoteRepeatId = -1
    var selectedNoteRepeat = 0
    var drumScreenInitialLoad = -1
    var selectedBarMeasure = 1

    /**
     *Millisecond counters
     */

    var metronomeMillisecCounter : Long = 0L
    var uiClockMillisecCounter : Long = 0L
    var sequenceMillisecClock : Long = 0L
    var uiProgressBarMillisecCounter : Long = 0L
    var globalMilliSecCounter : Long = 0L


    /**
     * Arraylist of notes triggered for each pad
     *
     */                         //|Pads|   |Timestamp index| |object at index|
    val padHitSequenceArrayList : ArrayList<ArrayMap<Long, PadSequenceTimeStamp>>? = ArrayList()

    /**
     * Sequence Arraylist for each pad for undo action
     */

}
}