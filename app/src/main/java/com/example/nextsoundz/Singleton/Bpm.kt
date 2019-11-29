package com.example.nextsoundz.Singleton

object Bpm {

    private var userBpm: Long = 0L

    var secondsInAminute = 60000L

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
}