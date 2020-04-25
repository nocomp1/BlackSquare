package com.example.blacksquare.Utils

import android.util.ArrayMap
import com.example.blacksquare.Objects.PadSequenceTimeStamp

class ProjectJsonObjects {

    data class Project(

        var projectList: ArrayList<ProjectDetails> = ArrayList()
    )

    data class ProjectDetails(

        val projectId : Long = -1L,
        var name: String,
        var pads: ArrayList<Pad> = ArrayList(),
        var instrumentTracks : ArrayList<Instrument> = ArrayList()
    )

    data class Pad(
        var padId : Long,
        var panRange: String,
        var soundLocation: String = "",
        var volumeLevel: String = "",
        var pitchRange: String = "",
        var reverbLevel: String = "",
        var lowPassFilter: Double = 0.0,
        var highPassFilter: Double = 0.0,
        var automation: PadAutomation = PadAutomation(),
        var mute: Boolean = false,
        var solo: Boolean = false,
        var timeLineHit: ArrayMap<Long, PadSequenceTimeStamp> =
            ArrayMap<Long, PadSequenceTimeStamp>()

    )

    data class PadAutomation(
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

}

