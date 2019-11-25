package com.example.nextsoundz.Singleton

object Metronome {

    private var active = false
    private var soundId = 0

    fun setState(state: Boolean) {
        active = state
    }

    fun isActive(): Boolean {
        return active
    }

    fun setSoundId(soundId: Int) {
        this.soundId = soundId
    }

    fun getSoundId(): Int {
        return soundId
    }

}