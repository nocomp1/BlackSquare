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
import androidx.lifecycle.ViewModelProviders
import com.example.blacksquare.Utils.SharedPrefKeys.APP_SHARED_PREFERENCES
import com.example.blacksquare.Utils.SharedPrefKeys.IS_METRONOME_ON
import com.example.blacksquare.Utils.SharedPrefKeys.MAIN_SLIDER_CONTROL_TEXT_TITLE
import com.example.blacksquare.Utils.SharedPrefKeys.METRONOME_SOUND_ID
import com.example.blacksquare.Utils.SharedPrefKeys.PATTERN_SELECTED
import com.example.blacksquare.Utils.SharedPrefKeys.PATTERN_SELECTED_DEFAULT
import com.example.blacksquare.Utils.SharedPrefKeys.PROJECT_TEMPO
import com.example.blacksquare.Managers.SharedPrefManager
import com.example.blacksquare.Singleton.ApplicationState
import com.example.blacksquare.Singleton.Metronome
import com.example.blacksquare.Utils.BpmUtils
import com.example.blacksquare.Utils.SharedPrefKeys.BAR_MEASURE_SELECTED
import com.example.blacksquare.ViewModels.MainViewModel
import com.example.blacksquare.Views.ToggleButtonGroupExtensions.getRadioBtnText
import com.example.blacksquare.Views.ToggleButtonGroupExtensions.setRadioBtnSelection
import com.example.blacksquare.Views.ToggleButtonGroupTableLayout
import kotlinx.android.synthetic.main.activity_dialog_settings_layout.*


class SettingsDialogActivity : AppCompatActivity(),
    ToggleButtonGroupTableLayout.ToggleButtonListener {

    private var bpm: Long = 0L
    private var isMetronomeOn = true
    private var previousClickTime = 0L
    private lateinit var mainActivity: MainActivity

    //60 seconds in a minute
    private val seconds = 60000L
    private lateinit var sharedViewModel: MainViewModel


    lateinit var sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_settings_layout)

        //  mainActivity = MainActivity()
        this?.let {
            sharedViewModel = ViewModelProviders.of(it).get(MainViewModel::class.java)
        }

        sharedPref = this.getSharedPreferences(
            getString(APP_SHARED_PREFERENCES),
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


        pattern_radio_group.setUpListener(this)
        instrument_tracks_radio_group.setUpListener(this)
        drum_bank_radio_group.setUpListener(this)
        quantize_radio_group.setUpListener(this)
        bar_measure_radio_group.setUpListener(this)


        ///Set up the pattern bar choice
        setUpQuantizeChoice()
        ///Set up chosen pattern
        setUpPatternAndBarChoice()
        ///Set up Instrument track choice
        setUpInstrumentTrackChoice()
        ///Set up Drum Bank choice
        setUpDrumBankChoice()


    }

    override fun onToggleButtonClicked(radioButton: RadioButton?) {

       //Saving selections to shared prefs when clicked
        radioButton?.let {

            //checking if radio button clicked is part of a certain group
            if (radioButton.equals(pattern_radio_group.activeRadioButton)) {
                val patternChoice = pattern_radio_group.getRadioBtnText()

                //save pattern selection
                sharedPref.edit()
                    .putString(
                        getString(PATTERN_SELECTED),
                        pattern_radio_group.getRadioBtnText()
                    ).apply()

                //getting the button text and it is used as the key to get the
                // bar measure for that  pattern-
                // set the saved bar of the pattern
                val barMeasure = sharedPref.getString(
                    radioButton.text.toString(),
                    getString(R.string.one_bar_measure_text)
                )
                bar_measure_radio_group.setRadioBtnSelection(barMeasure)

                val currentBarSelected = bar_measure_radio_group.getRadioBtnText()

                //save the bar measure thats selected

                sharedPref.edit()
                    .putInt(
                        getString(BAR_MEASURE_SELECTED),
                        currentBarSelected.toInt()
                    ).apply()


            }

            //checking if the instrument button was clicked
            if (radioButton.equals(instrument_tracks_radio_group.activeRadioButton)) {
                //save Instrument selection
                sharedPref.edit()
                    .putString(
                        getString(R.string.shared_prefs_instrument_selected),
                        instrument_tracks_radio_group.getRadioBtnText()
                    ).apply()
                Log.d("settingsdialog", "Instrument group")
            }


            //Checking drum bank buttons
            if (radioButton.equals(drum_bank_radio_group.activeRadioButton)) {

                //save drum bank selection
                sharedPref.edit()
                    .putString(
                        getString(R.string.shared_prefs_drum_bank_selected),
                        drum_bank_radio_group.getRadioBtnText()
                    ).apply()
                Log.d("settingsdialog", "drum group")
            }

            //Checking quantize buttons
            if (radioButton.equals(quantize_radio_group.activeRadioButton)) {
                //save quantize selection
                sharedPref.edit()
                    .putString(
                        getString(R.string.shared_prefs_quantize_selected),
                        quantize_radio_group.getRadioBtnText()
                    ).apply()
                Log.d("settingsdialog", "quantize button was clicked group")
            }


            //Checking Bar buttons
            if (radioButton.equals(bar_measure_radio_group.activeRadioButton)) {

                val currentBarSelected = bar_measure_radio_group.getRadioBtnText()

                //save the bar measure selected

                sharedPref.edit()
                    .putInt(
                        getString(BAR_MEASURE_SELECTED),
                        currentBarSelected.toInt()
                    ).apply()




                SharedPrefManager.settingsPatternTitles().map { resourceId ->

                    val patternTitle = getString(resourceId)
                    val currentPatternSelected = pattern_radio_group.getRadioBtnText()

                    if (patternTitle == currentPatternSelected){
                        //now we know what pattern this selection is for
                        sharedPref.edit()
                            .putString(currentPatternSelected, currentBarSelected)
                            .apply()

                    }

                }

                Log.d("settingsdialog", "quantize button was clicked group")
            }


        }
    }

    private fun setUpDrumBankChoice() {
        val choice = sharedPref.getString(
            getString(R.string.shared_prefs_drum_bank_selected),
            getString(R.string.drum_bank_1_title)
        )
        drum_bank_radio_group.setRadioBtnSelection(choice)
    }

    private fun setUpInstrumentTrackChoice() {
        val choice = sharedPref.getString(
            getString(R.string.shared_prefs_instrument_selected),
            getString(R.string.instrument_keys_title)
        )
        instrument_tracks_radio_group.setRadioBtnSelection(choice)
    }

    private fun setUpPatternAndBarChoice() {
        val choice = sharedPref.getString(
            getString(R.string.shared_prefs_pattern_selected),
            getString(R.string.p1)
        )
        pattern_radio_group.setRadioBtnSelection(choice)

        val patternKeyForBar = choice.toString()
        val barChoice =
            sharedPref.getString(patternKeyForBar, getString(R.string.one_bar_measure_text))
        bar_measure_radio_group.setRadioBtnSelection(barChoice)

    }

    private fun setUpQuantizeChoice() {
        val quantizeCheckboxChoice = sharedPref.getBoolean(
            getString(R.string.shared_prefs_quantize_checkbox_selected),
            true
        )

        val choice = sharedPref.getString(
            getString(R.string.shared_prefs_quantize_selected),
            getString(R.string.one_sixtenth_quantize)
        )

        quantize_radio_group.setRadioBtnSelection(choice)
        quantize_checkbox.isChecked = quantizeCheckboxChoice
    }


    private fun setMetronomeState(metronomeView: View, isActive: Boolean) {

        when (isActive) {
            true -> {

                Metronome.setState(isActive)

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
        sharedPref.edit().putBoolean(
            getString(R.string.shared_prefs_quantize_checkbox_selected),
            quantize_checkbox.isChecked
        ).apply()


        super.onPause()
    }

    override fun onDestroy() {


        super.onDestroy()
    }

    fun onBarSelected(view: View) {

        // Log.i("MyView", "hello")
    }


}