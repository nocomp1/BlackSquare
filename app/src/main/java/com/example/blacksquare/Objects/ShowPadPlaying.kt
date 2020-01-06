package com.example.blacksquare.Objects

import android.widget.Button

class ShowPadPlaying {

    fun showPadPlaying(pad: Button) {


        pad.isPressed = true
        pad.invalidate()

        pad.postDelayed(
            Runnable //delay button
            {
                pad.setPressed(false)
                pad.invalidate()
                //any other associated action
            }, 100
        )

    }
}