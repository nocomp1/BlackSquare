package com.example.nextsoundz.Managers

import com.example.nextsoundz.R

class SoundResManager {

    companion object {
        fun getDefaultKitFilesIds() = mapOf (
            0 to R.raw.sound2,
            1 to R.raw.sound3,
            2 to R.raw.sound1,
            3 to R.raw.ding,
            4 to R.raw.wood,
            5 to R.raw.click,
            6 to R.raw.beep
        )
    }


}