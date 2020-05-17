package com.example.blacksquare.Models

data class PadSequenceTimeStamp(
    val soundId: Int?,
    val padId: Int,
    var soundPlayTimeStamp: Long?,
    val padLftVolume: Float,
    val padRftVolume: Float
)