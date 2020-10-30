package com.example.blacksquare.Models

import com.example.blacksquare.R

class PopUpMainEditMenu {

    data class MainEditRotaryKnob(
        val value: Int = -1,
        val type: RotaryKnobType = RotaryKnobType.Volume()
    )

    sealed class RotaryKnobType {
        data class Volume(val nameResource : Int = R.string.volume): RotaryKnobType()
        data class Pan(val nameResource : Int = R.string.pan) : RotaryKnobType()
        data class Pitch(val nameResource : Int = R.string.pitch) : RotaryKnobType()
        data class HiPassFilter(val nameResource : Int = R.string.high_pass_filter) : RotaryKnobType()
        data class LowPassFilter(val nameResource : Int = R.string.low_pass_filter) : RotaryKnobType()
        data class Reverb (val nameResource : Int = R.string.reverb): RotaryKnobType()
        data class Delay (val nameResource : Int = R.string.delay): RotaryKnobType()
    }
}