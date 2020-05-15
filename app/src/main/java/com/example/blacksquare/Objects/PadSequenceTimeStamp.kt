package com.example.blacksquare.Objects

data class PadSequenceTimeStamp(
    val soundId: Int?,
    val padId: Int,
    var soundPlayTimeStamp: Long?,
    val padLftVolume: Float,
    val padRftVolume: Float
)