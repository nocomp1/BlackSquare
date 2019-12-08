package com.example.nextsoundz.Singleton

object ApplicationState {



    val defaultVolume = 0.75f

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


    var hasLoadedASound: Boolean =false
    var hasLoadedAKit: Boolean = true
    var isPlaying : Boolean =false
    var isRecording : Boolean =false
    var isArmedToRecord : Boolean =false
    var noteRepeatActive : Boolean = false
    var pad1Selected :Boolean=false
    var pad2Selected :Boolean=false
    //default bar count measure

    var selectedBarMeasure = -1
    var selectedPattern = -1
    var selectedInstrumentTrack = -1
    var selectedDrumBank = -1
    var projectMeasure :Int = 2
    var selectedPadId :Int? = null

    var selectedNoteRepeat = 2



//fun setSelectedBar(bar : Int){
//
//     selectedBarMeasure = bar
//}
//fun getSelectedBar (): Int{
//
//    return selectedBarMeasure
//}

//    var pattern1Measure :Int = 2
//    var pattern2Measure :Int = 2
//    var pattern3Measure :Int = 2
//    var pattern4Measure :Int = 2
//    var pattern5Measure :Int = 2
//    var pattern6Measure :Int = 2
//    var pattern7Measure :Int = 2
//    var pattern8Measure :Int = 2
//    var pattern9Measure :Int = 2
//    var pattern10Measure :Int = 2
//    var pattern11Measure :Int = 2
//    var pattern12Measure :Int = 2
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