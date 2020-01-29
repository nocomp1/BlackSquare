package com.example.blacksquare.ViewModels

import android.content.Context
import android.content.res.Configuration
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.ArrayMap
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.blacksquare.Fragments.DrumScreenHomeFragment
import com.example.blacksquare.Managers.DrumPadPlayBack
import com.example.blacksquare.R
import com.example.blacksquare.Singleton.ApplicationState
import com.example.blacksquare.Singleton.Bpm
import com.example.blacksquare.Singleton.Metronome
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivityViewModel(private var applicationContext: Context) : ViewModel() {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    /**
     * SoundPool (Metronome) variables
     */
    private lateinit var soundPool: SoundPool
    private var sound = 0
    /**
     * UI clock variables
     */
    private var beatCount = 0




    init {
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(applicationContext)



    }

    companion object {
        const val DARK_THEME_ENABLED_USER_PROPERTY = "UsesDarkMode"
        const val GESTURETAGBUTTON = "MAINACTIVITYTOUCHMEBUTTON"
        const val DARK_THEME_ENABLED_VALUE = "true"
        const val DARK_THEME_DISABLED_VALUE = "false"
        const val SETTINGS_REQUEST_CODE: Int = 200
        const val LOAD_SOUND_REQUEST_CODE: Int = 300

    }
    /**
     * Metronome
     * call this method after changing the sound
     */

    fun loadMetronomeSound() {
        sound = soundPool.load(applicationContext, Metronome.getSoundId(), 1)
    }

    fun playMetronomeSound() {


        if (Metronome.isActive()) {
            if ((ApplicationState.metronomeMillisecCounter == Bpm.getBeatPerMilliSeconds()) || (ApplicationState.metronomeMillisecCounter == 0L)) {

                //Play the sound
                soundPool.play(sound, 1.0f, 1.0f, 10, 0, 1.0f)

                //reset the counter
                ApplicationState.metronomeMillisecCounter = 0L

            }
        } else {
            ApplicationState.metronomeMillisecCounter = 0L
        }

        ApplicationState.metronomeMillisecCounter++
    }

     fun setUpMetronomeSoundPool() {
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .build()
    }

    /**
     * UICLOCK
     */

    fun updateUiClockEveryMilliSec(): SpannableStringBuilder {

        //For sequence time reached reset counter
        if (ApplicationState.uiSequenceMillisecCounter == Bpm.getPatternTimeInMilliSecs()) {

            // Log.d("sequencetime","sequencetime= ${ApplicationState.uiSequenceMillisecCounter}")
            // Log.d(" sequenceTime", Bpm.getPatternTimeInMilliSecs().toString())
            ApplicationState.uiSequenceMillisecCounter = 0
        }

        // start our clock and beat count at 1 and increment from there
        if ((ApplicationState.uiClockMillisecCounter == Bpm.getBeatPerMilliSeconds()) || (ApplicationState.uiClockMillisecCounter == 0L)) {
            beatCount++
            ApplicationState.uiClockMillisecCounter = 0L
        }

        if (beatCount > (ApplicationState.selectedBarMeasure * 4)) {
            //resetting the beat count
            beatCount = 1

            //always updating progress every beat
           // updateProgressBar()

        }


        val milliSecPerBeat = String.format("%04d", ApplicationState.uiClockMillisecCounter)
        val beats = String.format("%02d", beatCount)
        val uIClock = SpannableStringBuilder(beats)
        uIClock.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.colorAccent)),
            0, 2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        uIClock.append(" : ")
        //uIClock.append(oneHundmillis)
        uIClock.append(milliSecPerBeat)


        //update counters
        ApplicationState.uiClockMillisecCounter++
        ApplicationState.uiSequenceMillisecCounter++

        return uIClock
    }

    fun resetUiClock() :SpannableStringBuilder{
        beatCount = 0
        ApplicationState.uiClockMillisecCounter = 0L
        val uIClock = SpannableStringBuilder("00")
        uIClock.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.colorAccent)),
            0, 2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        uIClock.append(" : ")
        uIClock.append("0000")
        return uIClock
    }


        fun undoLastSequence() {
        if (DrumPadPlayBack.padHitUndoSequenceList!!.size != 0) {
            //loop through all pads
            var padIndexCounter1 = 0
            while (padIndexCounter1 < ApplicationState.padHitSequenceArrayList!!.size) {

                // if (DrumPadPlayBack.padHitUndoSequenceList!![padIndexCounter1].isNotEmpty()) {
                //remove last pattern
                DrumPadPlayBack.padHitUndoSequenceList!![padIndexCounter1].removeAt(
                    DrumPadPlayBack.padHitUndoSequenceList[padIndexCounter1].size - 1
                )
                DrumPadPlayBack.padHitUndoSequenceList[padIndexCounter1].trimToSize()

                //now check if the list is not empty since we remove pattern first
                if (DrumPadPlayBack.padHitUndoSequenceList!![padIndexCounter1].isNotEmpty()) {
                    //get the last pattern
                    val previousPattern =
                        ArrayMap(DrumPadPlayBack.padHitUndoSequenceList[padIndexCounter1].last())

                    //set the last pattern to the original list
                    DrumScreenHomeFragment.padTimeStampArrayMapList.set(
                        padIndexCounter1,
                        previousPattern
                    )
                    ApplicationState.padHitSequenceArrayList!!.set(
                        padIndexCounter1,
                        previousPattern
                    )
                } else {


                }
                //  }
                padIndexCounter1++
            }
        }

        if (DrumPadPlayBack.padHitUndoSequenceList.isEmpty()) {
            //Return to the original state when we first started
            DrumScreenHomeFragment().initPadTimeStampArrayList()
        }

    }


    private fun isPhoneSetToDarkTheme() {

        val mode = applicationContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
        when (mode) {
            Configuration.UI_MODE_NIGHT_YES -> {
                //  Toast.makeText(this, stringFromJNI().toString(), Toast.LENGTH_SHORT).show()
                val bundle = Bundle()
                bundle.putInt("dark_theme_enabled_count", 1)
                mFirebaseAnalytics?.logEvent("app_dark_theme_is_enabled", bundle)

                //Logging for Audience
                mFirebaseAnalytics?.setUserProperty(
                    DARK_THEME_ENABLED_USER_PROPERTY,
                    DARK_THEME_ENABLED_VALUE
                )
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                //Toast.makeText(this, "UI_MODE_NIGHT_NO", Toast.LENGTH_SHORT).show()
                val bundle = Bundle()
                bundle.putInt("dark_theme_disabled_count", 1)
                mFirebaseAnalytics?.logEvent("app_dark_theme_is_not_enabled", bundle)

                //Logging for Audience
                mFirebaseAnalytics?.setUserProperty(
                    DARK_THEME_ENABLED_USER_PROPERTY,
                    DARK_THEME_DISABLED_VALUE
                )
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                // Toast.makeText(this, "UI_MODE_NIGHT_UNDEFINED", Toast.LENGTH_SHORT).show()
            }
        }


    }


}
