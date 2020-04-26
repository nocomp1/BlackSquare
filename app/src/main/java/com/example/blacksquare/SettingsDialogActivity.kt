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
import com.example.blacksquare.Singleton.Bpm
import com.example.blacksquare.Singleton.Metronome
import com.example.blacksquare.Utils.CustomRadioButtonUtils
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
        tempo_value.value = Bpm.getProjectTempo().toInt()

        /// Setting our  switch state
        metronome.isChecked = Metronome.isActive()

        ////// Metronome switch Listener
        metronome.setOnCheckedChangeListener { metronomeView, isChecked ->
            setMetronomeState(metronomeView, isChecked)
        }

        ////// Tempo number picker Listener
        tempo_value.setOnValueChangedListener(NumberPicker.OnValueChangeListener { picker, oldVal, newVal ->

            //set the global bpm
            Bpm.setProjectTempo(newVal.toLong())

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
            val patternList = CustomRadioButtonUtils.getRadioBtnGroupIds(drum_bank_radio_group)
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
                CustomRadioButtonUtils.getRadioBtnGroupIds(instrument_tracks_radio_group)
            findViewById<RadioButton>(patternList[0]).isChecked = true
        }

    }

    private fun setUpPatternChoice() {
        val patternIds = CustomRadioButtonUtils.getRadioBtnGroupIds(pattern_radio_group)
        patternIds.forEach { patternId ->
            //set the choice if there is one
            val choice = sharedPref.getString(
                getString(R.string.shared_prefs_pattern_selected),
                getString(R.string.p1)
            )
            val radioButton = findViewById<RadioButton>(patternId)
            radioButton.isChecked = false

            when (radioButton.text) {
                choice -> {
                    radioButton.isChecked = true
                }
                else -> Log.d("mainPattern", "Could not select default pattern")
            }
        }

//        var selectedChoice = ApplicationState.selectedPatternRadioButtonId
//
//        if (selectedChoice != -1) {
//            findViewById<RadioButton>(selectedChoice).isChecked = true
//        } else {
//
//            ///Set pattern 1 (p1) default for pattern
//            val patternList = CustomRadioButtonUtils.getRadioBtnGroupIds(pattern_radio_group)
//            findViewById<RadioButton>(patternList[0]).isChecked = true
//        }

    }

    private fun setUpBarCountChoice() {

        val barId = CustomRadioButtonUtils.getRadioBtnGroupIds(bar_measure_radio_group)
        barId.forEach { id ->
            //set the choice if there is one
            // val choice = sharedPref.getString(getString(R.string.shared_prefs_pattern_selected),getString(R.string.p1))

            val patternChoice = sharedPref.getString(
                getString(R.string.shared_prefs_pattern_selected),
                getString(R.string.p1)
            )

            val radioButton = findViewById<RadioButton>(id)
            radioButton.isChecked = false

            when (patternChoice) {

                getString(R.string.p1) -> {

                    val barChoice = sharedPref.getString(
                        getString(R.string.shared_prefs_pattern_selected),
                        getString(R.string.p1)
                    )

                    radioButton.isChecked = true
                }
                getString(R.string.p2) -> {
                    radioButton.isChecked = true
                }

                getString(R.string.p3) -> {
                    radioButton.isChecked = true
                }
                getString(R.string.p4) -> {
                    radioButton.isChecked = true
                }
                getString(R.string.p5) -> {

                    radioButton.isChecked = true
                }
                getString(R.string.p6) -> {
                    radioButton.isChecked = true
                }
                getString(R.string.p7) -> {
                    radioButton.isChecked = true
                }
                getString(R.string.p8) -> {
                    radioButton.isChecked = true
                }
                getString(R.string.p9) -> {
                    radioButton.isChecked = true
                }
                getString(R.string.p10) -> {
                    radioButton.isChecked = true
                }
                getString(R.string.p11) -> {
                    radioButton.isChecked = true
                }
                getString(R.string.p12) -> {
                    radioButton.isChecked = true
                }

                else -> {
                }
            }

        }
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

        val currentTempo = Bpm.getProjectTempo()

        var timeBetweenClicks = 0L

        val temp = System.currentTimeMillis()

        if (previousClickTime != 0L) {
            Log.i("MyView", "Time Between Clicks=" + (temp - previousClickTime))

            timeBetweenClicks = (temp - previousClickTime)

            // calculate bpm from user taps
            bpm = (seconds / timeBetweenClicks)

            //set the global bpm
            Bpm.setProjectTempo(bpm)
            //update Ui
            tempo_value.value = bpm.toInt()

            //reset previousClick time and bpm
            previousClickTime = 0L


            if (currentTempo != Bpm.getProjectTempo()) {
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
            ApplicationState.selectedPatternRadioButtonId = pattern_radio_group.checkedRadioButtonId
            val patternSelected =
                findViewById<RadioButton>(pattern_radio_group.checkedRadioButtonId)

            sharedPref.edit()
                .putString(
                    getString(R.string.shared_prefs_pattern_selected),
                    patternSelected.text.toString()
                ).apply()
            Log.d("mainPattern", "radioBtnTextSettingDialog =${patternSelected.text}")

            barSelection?.let {
                //Save the pattern bar selection to shared prefs


                when (patternSelected.text) {

                    getString(R.string.p1) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p1_bar), (barSelection.text).toString())
                            .apply()
                    }
                    getString(R.string.p2) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p2_bar), (barSelection.text).toString())
                            .apply()
                    }

                    getString(R.string.p3) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p3_bar), (barSelection.text).toString())
                            .apply()
                    }
                    getString(R.string.p4) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p4_bar), (barSelection.text).toString())
                            .apply()
                    }
                    getString(R.string.p5) -> {

                        sharedPref.edit()
                            .putString(getString(R.string.p5_bar), (barSelection.text).toString())
                            .apply()
                    }
                    getString(R.string.p6) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p6_bar), (barSelection.text).toString())
                            .apply()
                    }
                    getString(R.string.p7) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p7_bar), (barSelection.text).toString())
                            .apply()
                    }
                    getString(R.string.p8) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p8_bar), (barSelection.text).toString())
                            .apply()
                    }
                    getString(R.string.p9) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p9_bar), (barSelection.text).toString())
                            .apply()
                    }
                    getString(R.string.p10) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p10_bar), (barSelection.text).toString())
                            .apply()
                    }
                    getString(R.string.p11) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p11_bar), (barSelection.text).toString())
                            .apply()
                    }
                    getString(R.string.p12) -> {
                        sharedPref.edit()
                            .putString(getString(R.string.p12_bar), (barSelection.text).toString())
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