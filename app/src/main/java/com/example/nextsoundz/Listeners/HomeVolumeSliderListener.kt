package com.example.nextsoundz.Listeners

import android.content.Context
import android.view.MotionEvent
import android.view.View

class HomeVolumeSliderListener : View.OnTouchListener {

    internal lateinit var callback: SliderListener

    interface SliderListener {

        fun homeVoulumeSliderOntouch(v: View?, event: MotionEvent?)
    }


    fun setHomeVoulumeSliderListener(callback: SliderListener) {
        this.callback = callback
    }


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        callback.homeVoulumeSliderOntouch(v,event)

        return true
    }
}