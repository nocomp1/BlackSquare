package com.example.blacksquare.Models

sealed class Quantize {

    object SixTenthNote : Quantize()
    object ThirtyTwoNote : Quantize()
    object EightNote : Quantize()
    object QuarterNote : Quantize()
    object SixteenthTriplet : Quantize()
    object Dotted : Quantize()

}