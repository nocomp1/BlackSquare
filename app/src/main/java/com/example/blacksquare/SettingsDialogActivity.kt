package com.example.blacksquare

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.NumberPicker
import android.widget.RadioButton
import android.widget.TableLayout
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import com.example.blacksquare.Singleton.ApplicationState
import com.example.blacksquare.Singleton.Bpm
import com.example.blacksquare.Singleton.Definitions
import com.example.blacksquare.Singleton.Metronome
import com.example.blacksquare.Views.ToggleButtonGroupTableLayout
import kotlinx.android.synthetic.main.activity_dialog_settings_layout.*
import java.util.*


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

        sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return

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

            ApplicationState.tempoHasChanged=true
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

        if ( selectedChoice != -1) {
            findViewById<RadioButton>(selectedChoice).isChecked = true
        } else {

            ///Set pattern 1 (p1) default for pattern
            val patternList = getRadioBtnGroupIds(drum_bank_radio_group)
            findViewById<RadioButton>(patternList[0]).isChecked = true
        }

    }

    private fun setUpInstrumentTrackChoice() {

        var selectedChoice = ApplicationState.selectedInstrumentTrackRadioButtonId

        if ( selectedChoice != -1) {
            findViewById<RadioButton>(selectedChoice).isChecked = true
        } else {

            ///Set pattern 1 (p1) default for pattern
            val patternList = getRadioBtnGroupIds(instrument_tracks_radio_group)
            findViewById<RadioButton>(patternList[0]).isChecked = true
        }

    }

    private fun setUpPatternChoice() {
        var selectedChoice = ApplicationState.selectedPatternRadioButtonId

        if ( selectedChoice != -1) {
            findViewById<RadioButton>(selectedChoice).isChecked = true
        } else {

            ///Set pattern 1 (p1) default for pattern
            val patternList = getRadioBtnGroupIds(pattern_radio_group)
            findViewById<RadioButton>(patternList[0]).isChecked = true
        }

    }

    private fun setUpBarCountChoice() {

        var selectedChoice = ApplicationState.selectedBarMeasureRadioButtonId

        if ( selectedChoice != -1) {
            findViewById<RadioButton>(selectedChoice).isChecked = true
        } else {

            ///Set a default for bar measure
            val barList = getRadioBtnGroupIds(bar_measure_radio_group)
            findViewById<RadioButton>(barList[Definitions.oneBarIndex]).isChecked = true
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


            if (currentTempo != Bpm.getProjectTempo()){
                ApplicationState.tempoHasChanged=true
            }

           // Log.i("MyView", "prevclick ${previousClickTime}")

        } else {
           // Log.i("MyView", "First Click")
            previousClickTime = temp
        }

    }

    private fun getRadioBtnGroupIds(view: ToggleButtonGroupTableLayout): ArrayList<Int> {

        var groupIds: ArrayList<Int> = arrayListOf()

        //The table layout that holds our radio buttons
        val tl = view as (TableLayout)
        val tlRowCount = tl.childCount
        var rowCounter = 0
        var viewCounter = 0

        while (rowCounter < tlRowCount) {
            var tr = tl.getChildAt(rowCounter) as (TableRow)
            var c = tr.childCount
            while (viewCounter < c) {
                ///Get the view that in the table row
                var v = tr.getChildAt(viewCounter)
                if (v is RadioButton) {

                    //get the id
                    groupIds.add(v.id)

                }
                viewCounter++
            }
            rowCounter++
        }

        return groupIds

    }

    override fun onPause() {

        if(bar_measure_radio_group.checkedRadioButtonId != -1) {
            ApplicationState.selectedBarMeasureRadioButtonId =
                bar_measure_radio_group.checkedRadioButtonId
            val rb = findViewById<RadioButton>(bar_measure_radio_group.checkedRadioButtonId)
            if (rb != null) {
                ApplicationState.selectedBarMeasure = (rb.text).toString().toInt()
            }
        }

        if(pattern_radio_group.checkedRadioButtonId != -1)
        ApplicationState.selectedPatternRadioButtonId = pattern_radio_group.checkedRadioButtonId
        if(instrument_tracks_radio_group.checkedRadioButtonId!= -1)
        ApplicationState.selectedInstrumentTrackRadioButtonId =
            instrument_tracks_radio_group.checkedRadioButtonId
        if(drum_bank_radio_group.checkedRadioButtonId != -1)
        ApplicationState.selectedDrumBankRadioButtonId = drum_bank_radio_group.checkedRadioButtonId


        super.onPause()
    }
    override fun onDestroy() {


        super.onDestroy()
    }

    fun onBarSelected(view: View) {

       // Log.i("MyView", "hello")
    }
}