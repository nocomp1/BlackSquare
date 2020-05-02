package com.example.blacksquare

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.NumberPicker
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.example.blacksquare.Singleton.ApplicationState
import com.example.blacksquare.Utils.BpmUtils
import com.example.blacksquare.Singleton.Metronome
import com.example.blacksquare.Views.ToggleButtonGroupExtensions.getRadioBtnGroupIds
import com.example.blacksquare.Views.ToggleButtonGroupExtensions.getRadioBtnText
import com.example.blacksquare.Views.ToggleButtonGroupExtensions.setRadioBtnSelection
import kotlinx.android.synthetic.main.activity_dialog_settings_layout.*


class SettingsDialogActivity : AppCompatActivity() {

    private var bpm: Long = 0L
    private var isMetronomeOn = true
    private var previousClickTime = 0L
    private lateinit var mainActivity: MainActivity

    //60 seconds in a minute
    private val seconds = 60000L


    lateinit var sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_settings_layout)

        mainActivity = MainActivity()

        sharedPref = this.getSharedPreferences(
            getString(R.string.application_shared_prefs),
            Context.MODE_PRIVATE
        )

        ///////Setting the bpm/tempo min/max values to choose
        ///////and setting the bpm/temp for the project
        tempo_value.minValue = 40
        tempo_value.maxValue = 180
        tempo_value.value = BpmUtils.getProjectTempo().toInt()

        /// Setting our  switch state
        metronome.isChecked = Metronome.isActive()

        ////// Metronome switch Listener
        metronome.setOnCheckedChangeListener { metronomeView, isChecked ->
            setMetronomeState(metronomeView, isChecked)
        }

        ////// Tempo number picker Listener
        tempo_value.setOnValueChangedListener(NumberPicker.OnValueChangeListener { picker, oldVal, newVal ->

            //set the global bpm
            BpmUtils.setProjectTempo(newVal.toLong())

            ApplicationState.tempoHasChanged = true
        })

        // sharedPref.edit().clear().commit()

        //Log.i("MyView",  ApplicationState.selectedBarMeasure.toString())

        ///Set up the pattern bar choice
        setUpBarCountChoice()
        ///Set up chosen pattern
        setUpPatternChoice()
        ///Set up Instrument track choice
        setUpInstrumentTrackChoice()
        ///Set up Drum Bank choice
        setUpDrumBankChoice()


    }

    private fun setUpDrumBankChoice() {
        var selectedChoice = ApplicationState.selectedDrumBankRadioButtonId

        if (selectedChoice != -1) {
            findViewById<RadioButton>(selectedChoice).isChecked = true
        } else {

            ///Set pattern 1 (p1) default for pattern
            val patternList = drum_bank_radio_group.getRadioBtnGroupIds()
            findViewById<RadioButton>(patternList[0]).isChecked = true
        }

    }

    private fun setUpInstrumentTrackChoice() {

        var selectedChoice = ApplicationState.selectedInstrumentTrackRadioButtonId

        if (selectedChoice != -1) {
            findViewById<RadioButton>(selectedChoice).isChecked = true
        } else {

            ///Set pattern 1 (p1) default for pattern
            val patternList =
                instrument_tracks_radio_group.getRadioBtnGroupIds()
            findViewById<RadioButton>(patternList[0]).isChecked = true
        }

    }

    private fun setUpPatternChoice() {
        val choice = sharedPref.getString(
            getString(R.string.shared_prefs_pattern_selected),
            getString(R.string.p1)
        )
        pattern_radio_group.setRadioBtnSelection(choice)
     //   val barChoice = sharedPref.getString(choice,getString(R.string.one_bar_measure_text))
      //  bar_measure_radio_group.setRadioBtnSelection(barChoice)


    }





    private fun setUpBarCountChoice() {




    }


    private fun setMetronomeState(metronomeView: View, isActive: Boolean) {

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

            }

        }

    }


    fun tapInTemp(v: View) {

        val currentTempo = BpmUtils.getProjectTempo()

        var timeBetweenClicks = 0L

        val temp = System.currentTimeMillis()

        if (previousClickTime != 0L) {
            Log.i("MyView", "Time Between Clicks=" + (temp - previousClickTime))

            timeBetweenClicks = (temp - previousClickTime)

            // calculate bpm from user taps
            bpm = (seconds / timeBetweenClicks)

            //set the global bpm
            BpmUtils.setProjectTempo(bpm)
            //update Ui
            tempo_value.value = bpm.toInt()

            //reset previousClick time and bpm
            previousClickTime = 0L


            if (currentTempo != BpmUtils.getProjectTempo()) {
                ApplicationState.tempoHasChanged = true
            }

            // Log.i("MyView", "prevclick ${previousClickTime}")

        } else {
            // Log.i("MyView", "First Click")
            previousClickTime = temp
        }

    }

    override fun onPause() {
        var barSelection: RadioButton? = null
        if (bar_measure_radio_group.checkedRadioButtonId != -1) {
            ApplicationState.selectedBarMeasureRadioButtonId =
                bar_measure_radio_group.checkedRadioButtonId


            barSelection = findViewById(bar_measure_radio_group.checkedRadioButtonId)
            if (barSelection != null) {
                ApplicationState.selectedBarMeasure = (barSelection.text).toString().toInt()
            }
        }

        if (pattern_radio_group.checkedRadioButtonId != -1) {
           // ApplicationState.selectedPatternRadioButtonId = pattern_radio_group.checkedRadioButtonId
            val patternSelected =
                findViewById<RadioButton>(pattern_radio_group.checkedRadioButtonId)

            sharedPref.edit()
                .putString(
                    getString(R.string.shared_prefs_pattern_selected),
                    pattern_radio_group.getRadioBtnText()
                ).apply()
            Log.d("mainPattern", "radioBtnTextSettingDialog =${patternSelected.text}")

            barSelection?.let {
                //Save the pattern bar selection to shared prefs


                when (patternSelected.text) {

                    getString(R.string.p1) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p1), (barSelection.text).toString())
                            .apply()
                    }
                    getString(R.string.p2) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p2), (barSelection.text).toString())
                            .apply()
                    }

                    getString(R.string.p3) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p3), (barSelection.text).toString())
                            .apply()
                    }
                    getString(R.string.p4) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p4), (barSelection.text).toString())
                            .apply()
                    }
                    getString(R.string.p5) -> {

                        sharedPref.edit()
                            .putString(getString(R.string.p5), (barSelection.text).toString())
                            .apply()
                    }
                    getString(R.string.p6) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p6), (barSelection.text).toString())
                            .apply()
                    }
                    getString(R.string.p7) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p7), (barSelection.text).toString())
                            .apply()
                    }
                    getString(R.string.p8) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p8), (barSelection.text).toString())
                            .apply()
                    }
                    getString(R.string.p9) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p9), (barSelection.text).toString())
                            .apply()
                    }
                    getString(R.string.p10) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p10), (barSelection.text).toString())
                            .apply()
                    }
                    getString(R.string.p11) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p11), (barSelection.text).toString())
                            .apply()
                    }
                    getString(R.string.p12) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p12), (barSelection.text).toString())
                            .apply()
                    }

                    else -> {
                    }
                }
            }
        }

        if (instrument_tracks_radio_group.checkedRadioButtonId != -1)
            ApplicationState.selectedInstrumentTrackRadioButtonId =
                instrument_tracks_radio_group.checkedRadioButtonId
        if (drum_bank_radio_group.checkedRadioButtonId != -1)
            ApplicationState.selectedDrumBankRadioButtonId =
                drum_bank_radio_group.checkedRadioButtonId


        super.onPause()
    }

    override fun onDestroy() {


        super.onDestroy()
    }

    fun onBarSelected(view: View) {

        // Log.i("MyView", "hello")
    }
}