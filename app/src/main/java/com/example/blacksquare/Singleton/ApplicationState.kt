package com.example.blacksquare.Singleton

import android.util.ArrayMap
import com.example.blacksquare.Objects.PadSequenceTimeStamp

class ApplicationState {

companion object {

    /**
     *
     * Application boolean flags
     */
    var drumNoteHasBeenRecorded: Boolean=false
    var undoLastSequence: Boolean = false
    var isMillisecondClockPlaying: Boolean = false
    var noteRepeatHasChanged: Boolean = false
    var tempoHasChanged: Boolean = false
    val defaultVolume = 0.75f

    /**
     * Drum pad volume
     */
    var pad1LftVolume: Float = defaultVolume
    var pad1RftVolume: Float = defaultVolume

    var pad2LftVolume: Float = defaultVolume
    var pad2RftVolume: Float = defaultVolume

    var pad3LftVolume: Float = defaultVolume
    var pad3RftVolume: Float = defaultVolume

    var pad4LftVolume: Float = defaultVolume
    var pad4RftVolume: Float = defaultVolume

    var pad5LftVolume: Float = defaultVolume
    var pad5RftVolume: Float = defaultVolume

    var pad6LftVolume: Float = defaultVolume
    var pad6RftVolume: Float = defaultVolume

    var pad7LftVolume: Float = defaultVolume
    var pad7RftVolume: Float = defaultVolume

    var pad8LftVolume: Float = defaultVolume
    var pad8RftVolume: Float = defaultVolume

    var pad9LftVolume: Float = defaultVolume
    var pad9RftVolume: Float = defaultVolume

    var pad10LftVolume: Float = defaultVolume
    var pad10RftVolume: Float = defaultVolume

    /**
     * Metronome volume
     */
    var metronomeRightVolume : Float = 1.0f
    var metronomeLeftVolume : Float = 1.0f



    var hasLoadedASound: Boolean = false
    var hasLoadedAKit: Boolean = true
    var isPlaying: Boolean = false
    var isRecording: Boolean = false
    var isArmedToRecord: Boolean = false
    var noteRepeatActive: Boolean = false
    var pad1Selected: Boolean = false
    var pad2Selected: Boolean = false
    //default bar count measure


    /**
     * Settings screen view ids
     */
    var selectedBarMeasureRadioButtonId = -1
    var selectedPatternRadioButtonId = -1
    var selectedInstrumentTrackRadioButtonId = -1
    var selectedDrumBankRadioButtonId = -1
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
    var uiSequenceMillisecCounter : Long = 0L
    var uiProgressBarMillisecCounter : Long = 0L
    var millisecSequenceIndexCunter = 0L

    /**
     * Arraylist of notes triggered for each pad
     */
    var padHitSequenceArrayList : ArrayList<ArrayMap<Long,PadSequenceTimeStamp>>? = ArrayList()
    var pad1HitTimeStampList:ArrayMap<Long,PadSequenceTimeStamp>? = ArrayMap()
    var pad2HitTimeStampList:ArrayMap<Long,PadSequenceTimeStamp>? = ArrayMap()
    var pad3HitTimeStampList:ArrayMap<Long,PadSequenceTimeStamp>? = ArrayMap()
    var pad4HitTimeStampList:ArrayMap<Long,PadSequenceTimeStamp>? = ArrayMap()

    /**
     * Sequence Arraylist for each pad for undo action
     */
    var padHitUndoSequenceList : ArrayList<ArrayList<ArrayMap<Long,PadSequenceTimeStamp>>>? = ArrayList()
    var undoPad1HitTimeStampList : ArrayList<ArrayMap<Long,PadSequenceTimeStamp>>? = ArrayList()
    var undoPad2HitTimeStampList : ArrayList<ArrayMap<Long,PadSequenceTimeStamp>>? = ArrayList()
    var undoPad3HitTimeStampList : ArrayList<ArrayMap<Long,PadSequenceTimeStamp>>? = ArrayList()
    var undoPad4HitTimeStampList : ArrayList<ArrayMap<Long,PadSequenceTimeStamp>>? = ArrayList()

}
}