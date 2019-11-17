package com.example.nextsoundz.Util

class Bpm(private val userBpm: Long) {

val beatsPerMeasure = 4

    var secondsInAminute = 60000L

    fun getTempo(): Long{

        //milliseconds per beat
        val milliSecPerBeat = secondsInAminute / userBpm
        val finalBpm =milliSecPerBeat

        return finalBpm

    }
}