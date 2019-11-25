package com.example.nextsoundz

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.example.nextsoundz.Singleton.Bpm
import com.example.nextsoundz.Singleton.Metronome
import kotlinx.android.synthetic.main.activity_dialog_settings_layout.*


class SettingsDialogActivity : AppCompatActivity() {

    private var bpm: Long = 0L
    private var metronomeIsOn = true

    lateinit var sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_settings_layout)


        sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return

        metronomeIsOn = sharedPref.getBoolean(getString(R.string.isMetronomeOn), true)

        metronome.isChecked = metronomeIsOn
        Metronome.setState(metronomeIsOn)

        metronome.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> Metronome.setState(true)
                false -> Metronome.setState(false)

            }

            //set the state of the metronome
            with(sharedPref.edit()) {
                putBoolean(getString(R.string.isMetronomeOn), isChecked)
                commit()
            }
            Metronome.setState(isChecked)

        }

    }

    var previousClickTime = 0L
    fun tapInTemp(v: View) {

        var timeBetweenClicks = 0L

        //60 seconds in a minute
        val seconds = 60000L

        val temp = System.currentTimeMillis()

        if (previousClickTime != 0L) {
            Log.i("MyView", "Time Between Clicks=" + (temp - previousClickTime))

            timeBetweenClicks = (temp - previousClickTime)

            // calculate bpm from user taps
            bpm = (seconds / timeBetweenClicks)

            //set the global bpm
            Bpm.setBpm(bpm)
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