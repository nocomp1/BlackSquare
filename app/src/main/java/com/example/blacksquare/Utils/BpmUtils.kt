package com.example.blacksquare.Utils

import android.util.Log
import com.example.blacksquare.Objects.Note
import com.example.blacksquare.Objects.Quantize
import com.example.blacksquare.Singleton.ApplicationState
import com.example.blacksquare.Singleton.Definitions
import com.example.blacksquare.Utils.Kotlin.exhaustive

object BpmUtils {

    private var userBpm: Long = 0L
    const val millisecondsInAminute = 60000L


    //to get 16th note quantize intervals take the sum of sixtenthNoteEquation
    // To get how many notes will fit in a seqence divide that sum by getSequenceTimeInMilliSecs
    //

    fun quantizeNote(
        quantize: Quantize,
        soundPlayTimeStamp: Long
    ): Long {
        when (quantize) {
            is Quantize.SixTenthNote -> {
                return calculateQuantize(soundPlayTimeStamp, getNoteEquation(Note.SixTeen))
            }
            is Quantize.ThirtyTwoNote -> TODO()
            is Quantize.EightNote -> TODO()
            is Quantize.QuarterNote -> TODO()
        }.exhaustive

    }

    //this method calculates where a given note should be quantized based on note timestamp
    //Example: if we want a note quantized to the 16th note we figure out how long is the
    // sequence (2,000 milliseconds) - Find out how many note will fit in that sequence
    // - 1 bar will give us 16 notes within the 2,000 millisecond. Then we find out the
    // millisecond for each note (0,125,250,375,500,625 ...2000). - Then we figure out
    // the rang between each of the sets of notes (0...125) calculate what note(0 or 125)
    // is the provided noteTimeStamp is closet to then return the sum.
    private fun calculateQuantize(note: Long, quantizationInterval: Long): Long {
        Log.d("calculateQuantize", " quantizationInterval= ${quantizationInterval}")
        Log.d("calculateQuantize", " getSequenceTimeInMilliSecs= ${getSequenceTimeInMilliSecs()}")

        // how many notes at a given interval is in the sequence
        val notesInSequence = getSequenceTimeInMilliSecs().div(quantizationInterval)
        Log.d("calculateQuantize", " notesInSequence= ${notesInSequence}")

        //create a rang to loop through
        val range = IntRange(0, notesInSequence.toInt())

        range.forEachIndexed { index, i ->

            // Log.d("calculateQuantize", " RangeInclusive= ${range.endInclusive}")

            Log.d("calculateQuantize", " = ${i.times(quantizationInterval)}")

            if (i < range.last) {
                //round to the nearest
                //create range of first set exp: (0...125)
                val rangStart = i.times(quantizationInterval)
                val rangeEnd = rangStart.plus(quantizationInterval)

                //check if note is within the new range
                if ((note > rangStart) && (note < rangeEnd)) {

                    Log.d("calculateQuantize", " RangeStart= $rangStart RangeEnd $rangeEnd")
                    Log.d("calculateQuantize", " original Note= $note")

                    //calculate what value is closes
                    val rangStartSum = note.minus(rangStart)
                    val rangEndSum = rangeEnd.minus(note)

                    return if (rangStartSum < rangEndSum){

                        rangStart
                    }else{

                        rangeEnd
                    }

                }

            }
        }

        //
        return note

    }

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
//
//    val quarterNoteEquation = 60000L / getProjectTempo()
//    val eightNoteEquation = 30000L / getProjectTempo()
//    val sixtenthNoteEquation = 15000L / getProjectTempo()
//    val sixtyFourNoteEquation = (60000L / getProjectTempo()) * (4) / 64
//    val thirtyTwoNoteEquation = (60000L / getProjectTempo()) * (4) / 32
//    val quarterNoteTripletEquation = 40000L / getProjectTempo()
//    val eightNoteTripletEquation = 20000L / getProjectTempo()
//    val sixtenthNoteTripletEquation = 10000L / getProjectTempo()
//    val dottedEightNoteEquation = 45000L / getProjectTempo()
    private fun getNoteEquation(note: Note): Long {

        (return when (note) {
            Note.Quarter -> 60000L / getProjectTempo()
            Note.Eight -> 30000L / getProjectTempo()
            Note.SixTeen -> 15000L / getProjectTempo()
            Note.ThirtyTwo -> (60000L / getProjectTempo()) * (4) / 32
            Note.SixtyFour -> (60000L / getProjectTempo()) * (4) / 64
            Note.QuarterTriplets -> 40000L / getProjectTempo()
            Note.EightTriplets -> 20000L / getProjectTempo()
            Note.SixTeenTriplets -> 10000L / getProjectTempo()
            Note.DottedEight -> 45000L / getProjectTempo()
        }).exhaustive
    }

    fun getBeatPerMilliSeconds(): Long {

        //milliseconds per beat
        //example: if the bpm is 120 then its 500 milliseconds per beat
        val milliSecPerBeat = millisecondsInAminute / userBpm
        val finalBpm = milliSecPerBeat

        return finalBpm

    }

    fun getSequenceTimeInMilliSecs(): Long {
        //example: if we have 500 milliseconds per beat times 1 bar times 4 = 2,000 milliseconds
        //1 bar = 4beats
        return (getBeatPerMilliSeconds() * ApplicationState.selectedBarMeasure) * 4
    }

    fun getProjectTempo(): Long {
        return userBpm
    }


    fun getNoteRepeatInterval(selectedNoteRepeat: Int): Long? {
        Log.d("bpm", "$selectedNoteRepeat")


        var noteRepeatInerval: Long? = null

        when (selectedNoteRepeat) {

            Definitions.QUARTER_NOTE -> noteRepeatInerval =
                getNoteEquation(Note.Quarter)
            Definitions.EIGHT_NOTE -> noteRepeatInerval =
                getNoteEquation(Note.Eight)
            Definitions.SIXTENTH_NOTE -> noteRepeatInerval =
                getNoteEquation(Note.SixTeen)
            Definitions.THIRTY_TWO_NOTE -> noteRepeatInerval =
                getNoteEquation(Note.ThirtyTwo)
            Definitions.SIXTY_FOUR_NOTE -> noteRepeatInerval =
                getNoteEquation(Note.SixtyFour)
            Definitions.QUARTER_NOTE_TRIPLET -> noteRepeatInerval =
                getNoteEquation(Note.QuarterTriplets)
            Definitions.EIGHT_NOTE_TRIPLET -> noteRepeatInerval =
                getNoteEquation(Note.EightTriplets)
            Definitions.SIXTENTH_NOTE_TRIPLET -> noteRepeatInerval =
                getNoteEquation(Note.SixTeenTriplets)
            Definitions.DOTTED_EIGHT_NOTE -> noteRepeatInerval =
                getNoteEquation(Note.DottedEight)
        }


        return noteRepeatInerval
    }


}