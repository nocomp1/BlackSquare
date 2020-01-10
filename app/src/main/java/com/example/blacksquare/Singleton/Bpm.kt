package com.example.blacksquare.Singleton

object Bpm {

    private var userBpm: Long = 0L
    const val secondsInAminute = 60000L

    /**
     * Set methods
     * set this first
     */
    fun setProjectTempo(tempo: Long) {
        userBpm = tempo
    }


    /**
     * Get methods
     */
    fun getBeatPerMilliSeconds(): Long {

        //milliseconds per beat
        val milliSecPerBeat = secondsInAminute / userBpm
        val finalBpm = milliSecPerBeat

        return finalBpm

    }

    fun getPatternTimeInMilliSecs(): Long {
        return (getBeatPerMilliSeconds() * ApplicationState.selectedBarMeasure) * 4
    }

    fun getProjectTempo(): Long {
        return userBpm
    }


    fun getNoteRepeatInterval(selectedNoteRepeat: Int): Long? {
        val quarterNoteEquation = 60000L / getProjectTempo()
        val eightNoteEquation = 30000L / getProjectTempo()
        val sixtenthNoteEquation = 15000L / getProjectTempo()
        val sixtyFourNoteEquation = 90000L / getProjectTempo()
        val thirtyTwoNoteEquation = 45000L / getProjectTempo()
        val quarterNoteTripletEquation = 40000L / getProjectTempo()
        val eightNoteTripletEquation = 20000L / getProjectTempo()
        val sixtenthNoteTripletEquation = 10000L / getProjectTempo()
        val dottedEightNoteEquation = 45000L / getProjectTempo()

        var noteRepeatInerval: Long? = null

        when (selectedNoteRepeat) {

            Definitions.QUARTER_NOTE -> noteRepeatInerval = quarterNoteEquation
            Definitions.EIGHT_NOTE -> noteRepeatInerval = eightNoteEquation
            Definitions.SIXTENTH_NOTE -> noteRepeatInerval = sixtenthNoteEquation
            Definitions.THIRTY_TWO_NOTE -> noteRepeatInerval = thirtyTwoNoteEquation
            Definitions.SIXTY_FOUR_NOTE -> noteRepeatInerval = sixtyFourNoteEquation
            Definitions.QUARTER_NOTE_TRIPLET -> noteRepeatInerval = quarterNoteTripletEquation
            Definitions.EIGHT_NOTE_TRIPLET -> noteRepeatInerval = eightNoteTripletEquation
            Definitions.SIXTENTH_NOTE_TRIPLET -> noteRepeatInerval = sixtenthNoteTripletEquation
            Definitions.DOTTED_EIGHT_NOTE -> noteRepeatInerval = dottedEightNoteEquation
        }


        return noteRepeatInerval
    }


}