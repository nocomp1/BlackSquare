package com.example.nextsoundz.Singleton

object Bpm {

    private var userBpm: Long = 0L

    var secondsInAminute = 60000L

    fun getBpm(): Long {

        //milliseconds per beat
        val milliSecPerBeat = secondsInAminute / userBpm
        val finalBpm = milliSecPerBeat

        return finalBpm

    }

    fun setBpm(bmp: Long){
        userBpm = bmp

    }
}