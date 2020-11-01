package com.example.blacksquare.Models

import android.util.ArrayMap

data class ProjectDetails(

    var projectId : Long = -1L,
    var name: String,
    var tempo : Long,
    var pads: ArrayList<Pad> = ArrayList(),
    var patterns: ArrayList<Pattern> = ArrayList(),
    var instrumentTracks : ArrayList<Instrument> = ArrayList()
)

data class Pad(
    val padId : Long? = null,
    val panRange: String? = null,
    val soundLocation: String? = null,
    val volumeLevel: String ? = null,
    val pitchRange: String? = null,
    val reverbLevel: String? = null,
    val lowPassFilter: Double = 0.0,
    val highPassFilter: Double = 0.0,
    val automation: PadAutomation? = null,
    val mute: Boolean = false,
    val solo: Boolean = false,
    val timeLineHit: ArrayMap<Long, PadSequenceTimeStamp> =
        ArrayMap(),
    val padPositionX : Int? = null,
    val padPositionY : Int? = null,
    val selected : Boolean =false

)

data class PadAutomation(
    var patternId : Int,
    var panAutomation: ArrayMap<Long, Double> = ArrayMap<Long, Double>(),
    var volumeAutomation: ArrayMap<Long, Double> = ArrayMap<Long, Double>(),
    var pitchAutomation: ArrayMap<Long, Double> = ArrayMap<Long, Double>(),
    var reverbAutomation: ArrayMap<Long, Double> = ArrayMap<Long, Double>(),
    var lowPassFilterAutomation: ArrayMap<Long, Double> = ArrayMap<Long, Double>(),
    var highPassFilterAutomation: ArrayMap<Long, Double> = ArrayMap<Long, Double>()
)
data class Instrument(
    var instrumentId : Long

)
data class Pattern(
    val bar : Int,
    val patterId : Int,
    val sequence : ArrayList<ArrayMap<Long, PadSequenceTimeStamp>>

)