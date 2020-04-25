package com.example.blacksquare.ViewModels

import android.util.ArrayMap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blacksquare.Fragments.DrumScreenHomeFragment
import com.example.blacksquare.Managers.DrumPadPlayBack
import com.example.blacksquare.Singleton.ApplicationState
import com.example.blacksquare.Singleton.Bpm
import com.example.blacksquare.Singleton.Definitions
import com.example.blacksquare.Singleton.Metronome
import com.example.blacksquare.Utils.Kotlin.exhaustive
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MainViewModel() : ViewModel() {
    //using interface for precision
    private lateinit var callback: UpdateListener

    interface UpdateListener {
        fun resetProgressBar()
        fun updateMetronomeSound()
        fun updateTimeLineProgress()
        fun updateUiClockEveryMilliSec(
            uIClockMilliSecondCounter: Long,
            beatCount: Long
        )
    }

    fun setListener(activityListener: UpdateListener) {
        callback = activityListener
    }

    private lateinit var playEngineExecutor: ScheduledExecutorService
    private var uiProgressBarMilliSecCounter = 0L
    private var metronomeCounter = 0L
    private var uIClockMilliSecondCounter = 0L
    private var beatCount = 0L

    val playbackPadId = MutableLiveData<Int>()
    val mainSliderValue = MutableLiveData<Int>()
    private val _viewState: MediatorLiveData<ViewState> = MediatorLiveData()
    val viewState: LiveData<ViewState> get() = _viewState


    private val _events: MutableLiveData<Event> = MediatorLiveData()
    val event: LiveData<Event> get() = _events


    data class ViewState(
        val updateTimeLineProgress: Boolean,
        val playMetronome: Boolean
    )

    private fun combineSources() {

    }

    private var mFirebaseAnalytics: FirebaseAnalytics? = null


    // private var padPlayback1: DrumPadPlayBack
    // private var sharedPref: SharedPreferences

    //val playbackPadId = MutableLiveData<Int>()


    init {
        // Obtain the FirebaseAnalytics instance.
        // mFirebaseAnalytics = FirebaseAnalytics.getInstance(applicationContext)
        //  padPlayback1 = DrumPadPlayBack(applicationContext)
        //  sharedPref = applicationContext.getSharedPreferences("MainActivity", Context.MODE_PRIVATE)

    }

    companion object {
        const val DARK_THEME_ENABLED_USER_PROPERTY = "UsesDarkMode"
        const val GESTURETAGBUTTON = "MAINACTIVITYTOUCHMEBUTTON"
        const val DARK_THEME_ENABLED_VALUE = "true"
        const val DARK_THEME_DISABLED_VALUE = "false"
        const val SETTINGS_REQUEST_CODE: Int = 200
        const val LOAD_SOUND_REQUEST_CODE: Int = 300

    }


    fun pad1Playback() {

//        padPlayback1.padPlayback(
//            Definitions.pad1Index,
//            sharedPref.getFloat(Definitions.pad1LftVolume, Definitions.padVolumeDefault)
//            , sharedPref.getFloat(Definitions.pad1RftVolume, Definitions.padVolumeDefault)
//        )
        showPadHitState(Definitions.pad1Index)
    }

    /**
     * Pad playbacks (called every millisecond)
     */
    private fun showPadHitState(padIndex: Int) {

        if (ApplicationState.padHitSequenceArrayList!![padIndex].contains(ApplicationState.uiSequenceMillisecCounter)) {
            Log.d("pad1playback", "pad playback is triggered")
            //show pad being triggered
            playbackPadId.postValue(padIndex)
        }
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

                    _events.postValue(Event.UndoLisEmptyMsg)

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


//    private fun isPhoneSetToDarkTheme() {
//
//        val mode =
//            applicationContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
//        when (mode) {
//            Configuration.UI_MODE_NIGHT_YES -> {
//                //  Toast.makeText(this, stringFromJNI().toString(), Toast.LENGTH_SHORT).show()
//                val bundle = Bundle()
//                bundle.putInt("dark_theme_enabled_count", 1)
//                mFirebaseAnalytics?.logEvent("app_dark_theme_is_enabled", bundle)
//
//                //Logging for Audience
//                mFirebaseAnalytics?.setUserProperty(
//                    DARK_THEME_ENABLED_USER_PROPERTY,
//                    DARK_THEME_ENABLED_VALUE
//                )
//            }
//            Configuration.UI_MODE_NIGHT_NO -> {
//                //Toast.makeText(this, "UI_MODE_NIGHT_NO", Toast.LENGTH_SHORT).show()
//                val bundle = Bundle()
//                bundle.putInt("dark_theme_disabled_count", 1)
//                mFirebaseAnalytics?.logEvent("app_dark_theme_is_not_enabled", bundle)
//
//                //Logging for Audience
//                mFirebaseAnalytics?.setUserProperty(
//                    DARK_THEME_ENABLED_USER_PROPERTY,
//                    DARK_THEME_DISABLED_VALUE
//                )
//            }
//            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
//                // Toast.makeText(this, "UI_MODE_NIGHT_UNDEFINED", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//
//    }


    private fun runEngine() {
        metronomeCounter()
        sequenceClock()
        uIClock()
        timeLineProgress()
    }

    private fun uIClock() {
        uIClockMilliSecondCounter++
        // start our clock and beat count at 1 and increment from there
        if ((uIClockMilliSecondCounter == Bpm.getBeatPerMilliSeconds()) || (uIClockMilliSecondCounter == 0L)) {
            beatCount++
            uIClockMilliSecondCounter = 0L
        }

        if (beatCount > (ApplicationState.selectedBarMeasure * 4)) {
            //resetting the beat count
            beatCount = 1

        }

        callback.updateUiClockEveryMilliSec(uIClockMilliSecondCounter, beatCount)
    }


    private fun metronomeCounter() {
        metronomeCounter++

        if (Metronome.isActive()) {

            if (metronomeCounter == Bpm.getBeatPerMilliSeconds()) {
                callback.updateMetronomeSound()
                //reset counter
                metronomeCounter = 0L
            }
        } else if (metronomeCounter == Bpm.getBeatPerMilliSeconds()) {
            //reset counter
            metronomeCounter = 0L
        }

    }

    private fun sequenceClock() {
        //keeping track of current sequence by milliseconds
        ApplicationState.uiSequenceMillisecCounter++

        //Reset the counter when we reach the end of sequence
        if (ApplicationState.uiSequenceMillisecCounter == Bpm.getPatternTimeInMilliSecs()) {
            ApplicationState.uiSequenceMillisecCounter = 0
        }
    }

    private fun timeLineProgress() {
        uiProgressBarMilliSecCounter++
        if (uiProgressBarMilliSecCounter == Bpm.getBeatPerMilliSeconds()) {
            callback.updateTimeLineProgress()
            //reset counter
            uiProgressBarMilliSecCounter = 0L
        }
    }


    private fun stopPlayEngine() {

        playEngineExecutor.shutdownNow()

        //Reset all clocks/counters
        callback.updateUiClockEveryMilliSec(0L, 0L)
        callback.resetProgressBar()
        uiProgressBarMilliSecCounter = 0L
        metronomeCounter = 0L
        uIClockMilliSecondCounter = 0L
        beatCount = 0L

    }

    private fun startPlayEngine() {
        val task = Runnable { runEngine() }
        playEngineExecutor = Executors.newScheduledThreadPool(1)
        playEngineExecutor.scheduleAtFixedRate(task, 0, 1000, TimeUnit.MICROSECONDS)
    }

    private fun mainSlider(progress: Int) {
        mainSliderValue.postValue(progress)
    }

    fun onAction(action: Action) {

        when (action) {
            Action.Run -> runEngine()
            Action.OnUndoTapped -> undoLastSequence()
            Action.OnPlay -> startPlayEngine()
            Action.OnStop -> stopPlayEngine()
            Action.OnSettingsTapped -> _events.value = Event.ShowSettings
            Action.OnLoadTapped -> _events.value = Event.ShowLoadMenu
            is Action.OnNoteRepeatTapped -> _events.value =
                Event.ActivateNoteRepeat(action.isNoteRepeatActivated)
            Action.OnNoteRepeatDoubleTapped -> _events.value = Event.ShowNoteRepeatMenu
            Action.OnShowPatternUi -> {
            }
            Action.OnShowMainUi -> {
            }
            is Action.OnRecordTapped -> _events.value =
                Event.ActivateRecord(action.isRecording)
            is Action.OnMainSliderProgressChange -> mainSlider(action.progress)
            is Action.OnMainSliderMenuSelection -> TODO()
        }.exhaustive


    }

    sealed class Action {
        object Run : Action()
        object OnPlay : Action()
        object OnStop : Action()
        object OnUndoTapped : Action()
        object OnSettingsTapped : Action()
        object OnLoadTapped : Action()
        data class OnNoteRepeatTapped(val isNoteRepeatActivated: Boolean) : Action()
        object OnNoteRepeatDoubleTapped : Action()
        object OnShowPatternUi : Action()
        object OnShowMainUi : Action()
        data class OnMainSliderProgressChange(val progress : Int) : Action()
        data class OnRecordTapped(val isRecording: Boolean) : Action()
        data class OnMainSliderMenuSelection(val label: String) : MainViewModel.Action()
    }

    sealed class Event {
        object UndoLisEmptyMsg : Event()
        object ShowSettings : Event()
        object ShowLoadMenu : Event()
        data class ActivateNoteRepeat(val isNoteRepeatActivated: Boolean) : Event()
        data class ActivateRecord(val isRecording: Boolean) : Event()
        object ShowNoteRepeatMenu : Event()

    }


}
