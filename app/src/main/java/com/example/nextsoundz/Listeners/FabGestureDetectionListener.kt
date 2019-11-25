package com.example.nextsoundz.Listeners

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent

class FabGestureDetectionListener : GestureDetector.SimpleOnGestureListener() {

    internal lateinit var callback: FabGestureListener

    //use this interface to pass our events to be handled by implementing activity
    interface FabGestureListener {

        fun onFabFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        )

        fun onFabSingleTapConfirmed(e: MotionEvent?)
        fun onFabDoubleTap(e: MotionEvent?)
        fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean

    }


    var GESTURETAGBUTTON = "MAINACTIVITYTOUCHMEBUTTON"


    fun setFabGestureListener(callback: FabGestureListener) {
        this.callback = callback
    }


    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        Log.d(GESTURETAGBUTTON, "single tap up")
        return super.onSingleTapUp(e)
    }

    override fun onDown(e: MotionEvent?): Boolean {


        return super.onDown(e)
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {

        callback.onFabFling(e1, e2, velocityX, velocityY)
        return true
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        //  Log.d(GESTURETAGBUTTON, "double  tap ")

        callback.onFabDoubleTap(e)
        return super.onDoubleTap(e)
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        callback.onScroll(e1, e2, distanceX, distanceY)
       // Log.d(GESTURETAGBUTTON, "on scroll\t   \t\t" + distanceX + "\t\tveloxity y\t\t" + distanceY)
        //handle the values here
        return super.onScroll(e1, e2, distanceX, distanceY)

    }

    override fun onContextClick(e: MotionEvent?): Boolean {
        return super.onContextClick(e)
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {

        callback.onFabSingleTapConfirmed(e)

        return super.onSingleTapConfirmed(e)
    }

    override fun onShowPress(e: MotionEvent?) {
        super.onShowPress(e)
        Log.d(GESTURETAGBUTTON, "on show press")
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        Log.d(GESTURETAGBUTTON, "on double tap")
        return super.onDoubleTapEvent(e)
    }

    override fun onLongPress(e: MotionEvent?) {
        super.onLongPress(e)
        Log.d(GESTURETAGBUTTON, "on long press")

    }
}