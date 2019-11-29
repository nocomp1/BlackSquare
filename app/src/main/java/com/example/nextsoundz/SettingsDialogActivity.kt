package com.example.nextsoundz

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.example.nextsoundz.Singleton.ApplicationState
import com.example.nextsoundz.Singleton.Bpm
import com.example.nextsoundz.Singleton.Metronome
import kotlinx.android.synthetic.main.activity_dialog_settings_layout.*


class SettingsDialogActivity : AppCompatActivity() {

    private var bpm: Long = 0L
    private var isMetronomeOn = true
    private var previousClickTime = 0L
    private lateinit var mainActivity :MainActivity
    //60 seconds in a minute
    private val seconds = 60000L


    lateinit var sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_settings_layout)


        mainActivity = MainActivity()

        sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return

        tempo_value.text = Bpm.getProjectTempo().toString()

        /// Setting our  switch state
        metronome.isChecked = Metronome.isActive()

        ////// Metronome switch Listener
        metronome.setOnCheckedChangeListener { metronomeView, isChecked ->
            setMetronomeState(metronomeView,isChecked)
        }


    }




    private fun setMetronomeState(metronomeView :View,isActive: Boolean) {

        when (isActive) {
            true -> {

                Metronome.setState(isActive)
//                //set the state of the metronome inside shared preference
//                with(sharedPref.edit()) {
//                    putBoolean(getString(R.string.isMetronomeOn), isActive)
//                    commit()
//                }

            }

            false -> {


                Metronome.setState(isActive)
//                //set the state of the metronome inside shared preference
//                with(sharedPref.edit()) {
//                    putBoolean(getString(R.string.isMetronomeOn), isActive)
//                    commit()
//                }



            }

        }

    }



    fun tapInTemp(v: View) {

        var timeBetweenClicks = 0L

        val temp = System.currentTimeMillis()

        if (previousClickTime != 0L) {
            Log.i("MyView", "Time Between Clicks=" + (temp - previousClickTime))

            timeBetweenClicks = (temp - previousClickTime)

            // calculate bpm from user taps
            bpm = (seconds / timeBetweenClicks)

            //set the global bpm
            Bpm.tempoToBeatPerMilliSec(bpm)
            //update Ui
            tempo_value.text = "${bpm.toString()} ${getString(R.string.bpm)}"

            //reset previousClick time and bpm
            previousClickTime = 0L



            Log.i("MyView", "prevclick ${previousClickTime}")

        } else {
            Log.i("MyView", "First Click")
            previousClickTime = temp
        }

    }







    override fun onDestroy() {


        super.onDestroy()
    }
}