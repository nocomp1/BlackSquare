package com.example.blacksquare.Singleton

class ApplicationState {

companion object {


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




//fun setSelectedBar(bar : Int){
//
//     selectedBarMeasure = bar
//}
//fun getSelectedBar (): Int{
//
//    return selectedBarMeasure
//}


//
//
//
//    fun setProjectMeasureCount(bar : Int,patternNumber : Int){
//
//        projectMeasure = bar
//
//        when (patternNumber){
//
//            1 ->  pattern1Measure = bar
//            2 ->  pattern2Measure = bar
//            3 ->  pattern3Measure = bar
//            4 ->  pattern4Measure = bar
//            5 ->  pattern5Measure = bar
//            6 ->  pattern6Measure = bar
//            7 ->  pattern7Measure = bar
//            8 ->  pattern8Measure = bar
//            9 ->  pattern9Measure = bar
//            10 ->  pattern10Measure = bar
//            11 ->  pattern11Measure = bar
//            12 ->  pattern12Measure = bar
//
//        }
//
//
//    }
}
}