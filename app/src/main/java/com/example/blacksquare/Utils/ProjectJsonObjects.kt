package com.example.blacksquare.Utils

class ProjectJsonObjects {

    data class Project(

        var projectList: ArrayList<ProjectDetails> = ArrayList()
    )

    data class ProjectDetails(

        var name: String = "",
        var pad1: Pad = Pad()
    )

    data class Pad(
        var panRange: String = "",
        var soundLocation : String = "",
        var volumeLevel: String = "",
        var pitchRange: String = "",
        var reverbLevel: String = "",
        var mute: Boolean = false,
        var solo: Boolean = false

        )
}