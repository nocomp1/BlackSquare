package com.example.blacksquare.Objects

sealed class Note {
    object Quarter : Note()
    object Eight : Note()
    object SixTeen : Note()
    object ThirtyTwo : Note()
    object SixtyFour : Note()
    object QuarterTriplets : Note()
    object EightTriplets : Note()
    object SixTeenTriplets : Note()
    object DottedEight : Note()

}