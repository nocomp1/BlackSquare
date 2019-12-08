package com.example.nextsoundz.Singleton

object Bpm {

    private var userBpm: Long = 0L

    var secondsInAminute = 60000L
    var targetNoteRepeatInterval = -1L
    var currentEngineStartTime = -1L

    fun getConvertedBeatPerMilliSec(): Long {

        //milliseconds per beat
        val milliSecPerBeat = secondsInAminute / userBpm
        val finalBpm = milliSecPerBeat

        return finalBpm

    }

    fun tempoToBeatPerMilliSec(tempo: Long){
        userBpm = tempo
    }

    fun getProjectTempo(): Long{
       return userBpm }


    fun getNoteRepeatInterval(selectedNoteRepeat : Int): Long?{
        var quarterNoteEquation = 60000L/ getProjectTempo()
        var eightNoteEquation = 30000L/ getProjectTempo()
        var sixtenthNoteEquation = 15000L/ getProjectTempo()
        var sixtyFourNoteEquation = 60000L/ getProjectTempo()
        var thirtyTwoNoteEquation = 60000L/ getProjectTempo()
        var quarterNoteTripletEquation = 40000L/ getProjectTempo()
        var eightNoteTripletEquation = 20000L/ getProjectTempo()
        var sixtenthNoteTripletEquation = 10000L/ getProjectTempo()
        var dottedEightNoteEquation = 45000L/ getProjectTempo()

        var noteRepeatInerval : Long? = null

        when(selectedNoteRepeat){

            Definitions.QUARTER_NOTE ->  noteRepeatInerval= quarterNoteEquation
            Definitions.EIGHT_NOTE ->  noteRepeatInerval= eightNoteEquation
            Definitions.SIXTENTH_NOTE->  noteRepeatInerval= sixtenthNoteEquation
            Definitions.THIRTY_TWO_NOTE ->  noteRepeatInerval= thirtyTwoNoteEquation
            Definitions.SIXTY_FOUR_NOTE ->  noteRepeatInerval= sixtyFourNoteEquation
            Definitions.QUARTER_NOTE_TRIPLET->  noteRepeatInerval= quarterNoteTripletEquation
            Definitions.EIGHT_NOTE_TRIPLET ->  noteRepeatInerval= eightNoteTripletEquation
            Definitions.SIXTENTH_NOTE_TRIPLET ->  noteRepeatInerval= sixtenthNoteTripletEquation
            Definitions.DOTTED_EIGHT_NOTE ->  noteRepeatInerval= dottedEightNoteEquation
        }


        return noteRepeatInerval
    }


}