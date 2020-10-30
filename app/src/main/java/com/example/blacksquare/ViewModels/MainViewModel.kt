package com.example.blacksquare.ViewModels

import android.util.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blacksquare.Helpers.ApplicationState
import com.example.blacksquare.Helpers.Metronome
import com.example.blacksquare.Models.PadSequenceTimeStamp
import com.example.blacksquare.Models.PopUpMainEditMenu.*
import com.example.blacksquare.Models.Quantize
import com.example.blacksquare.R
import com.example.blacksquare.Utils.BpmUtils
import com.example.blacksquare.Utils.Kotlin.exhaustive
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class MainViewModel() : ViewModel() {
    //using interface for precision
    private lateinit var callback: UpdateListener
    private lateinit var sequenceCallback: SequenceListener

    interface SequenceListener {
        fun updateSeqPerMilliSec(sequenceMilliSecClock: Long)
        fun onUndoTapped()
        fun onUndoConfirmed()
    }

    interface UpdateListener {
        fun resetProgressBar()
        fun updateMetronomeSound()
        fun updateTimeLineProgress(sequenceMilliSecClock: Long)
        fun updateUiClockEveryMilliSec(
            uIClockMilliSecondCounter: Long,
            beatCount: Long
        )
    }

    fun setListener(activityListener: UpdateListener) {
        callback = activityListener
    }

    fun setSeqListener(sequenceListener: SequenceListener) {
        sequenceCallback = sequenceListener
    }

    private var sequenceMilliSecClock = 0L
    private lateinit var playEngineExecutor: ScheduledExecutorService
    private lateinit var countInExecutor: ScheduledExecutorService
    private var uiProgressBarMilliSecCounter = 0L
    private var metronomeCounter = 0L
    private var uIClockMilliSecondCounter = 0L
    private var uiClockBeatCount = 1L

    private val _viewState: MediatorLiveData<ViewState> = MediatorLiveData()
    val sharedViewState: LiveData<ViewState> get() = _viewState


    //////////////////////////////|Patterns|  |Pads| |Timestamp index| |object at index|
    private val drumPatternArrayList =
        MutableLiveData<ArrayMap<String, ArrayList<ArrayMap<Long, PadSequenceTimeStamp>>>>()
            .also { _viewState.addSource(it) { combineSources() } }

    private val playbackPadId = MutableLiveData<Int>()
        .also { _viewState.addSource(it) { combineSources() } }

    val popupEditRotaryKnob = MutableLiveData<MainEditRotaryKnob>()
        .also { _viewState.addSource(it) { combineSources() } }

    private val isQuantizeEnabled = MutableLiveData<Boolean>()
        .also { _viewState.addSource(it) { combineSources() } }

    private val timeLeftBeforePatternChange = MutableLiveData<Long>()
        .also { _viewState.addSource(it) { combineSources() } }

    private val quantizeStyle = MutableLiveData<Quantize>()
        .also { _viewState.addSource(it) { combineSources() } }

    private val patternSelected = MutableLiveData<Int>()
        .also { _viewState.addSource(it) { combineSources() } }


    private val _events: MutableLiveData<Event> = MediatorLiveData()
    val event: LiveData<Event> get() = _events

    private val barMeasure = MutableLiveData<Int>()
        .also { _viewState.addSource(it) { combineSources() } }


    data class ViewState(
        val playBackPadId: Int,
        val popupEditRotaryKnob: MainEditRotaryKnob,
        val drumPatternList: ArrayMap<String, ArrayList<ArrayMap<Long, PadSequenceTimeStamp>>>,
        val patternSelected: Int,
        val isQuantizeEnabled: Boolean,
        val quantizeStyle: Quantize,
        val barMeasure: Int,
        val timeLeftBeforePatternChange: Long
    )

    private fun combineSources() {

        ViewState(
            playBackPadId = playbackPadId.value ?: -1,
            popupEditRotaryKnob = popupEditRotaryKnob.value ?: MainEditRotaryKnob(),
            drumPatternList = drumPatternArrayList.value ?: ArrayMap(),
            patternSelected = patternSelected.value ?: R.string.p1,
            isQuantizeEnabled = isQuantizeEnabled.value ?: false,
            quantizeStyle = quantizeStyle.value ?: Quantize.SixTenthNote,
            barMeasure = barMeasure.value ?: 1,
            timeLeftBeforePatternChange = timeLeftBeforePatternChange.value ?: 0L
        ).also { _viewState.value = it }

    }

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

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

    private fun onUndoTapped() {
        sequenceCallback.onUndoTapped()
    }

    private fun onUndoConfirmed() {
        sequenceCallback.onUndoConfirmed()
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
        if ((uIClockMilliSecondCounter == BpmUtils.getBeatPerMilliSeconds()) || (uIClockMilliSecondCounter == 0L)) {
            uiClockBeatCount++
            uIClockMilliSecondCounter = 0L
        }

        if (uiClockBeatCount > (ApplicationState.selectedBarMeasure * 4)) {
            //resetting the beat count - Start at 1
            uiClockBeatCount = 1

        }

        callback.updateUiClockEveryMilliSec(uIClockMilliSecondCounter, uiClockBeatCount)
    }


    private fun metronomeCounter() {
        metronomeCounter++

        if (Metronome.isActive()) {

            if (metronomeCounter == BpmUtils.getBeatPerMilliSeconds() || metronomeCounter == 0L) {
                metronomeCounter = 0L

                callback.updateMetronomeSound()

//                //Trigger the countIn if activated
//                if (ApplicationState.isCountInActivated && ApplicationState.isRecording) {
//
//                    if (countInCount <= ApplicationState.countInCountPreference) {
//                        _events.postValue(Event.UpdateCountInClock(countInCount))
//                        //to keep sequence clock on time we zero it out each count in
//                        sequenceMilliSecClock = 0
//
//                    }
//                    countInCount++
//                }

            }

        } else if (metronomeCounter == BpmUtils.getBeatPerMilliSeconds()) {
            //reset counter
            metronomeCounter = 0L
        }

    }

    /**
     * When play is engaged the sequence clock is always running - each millisecond
     */
    private fun sequenceClock() {
        //keeping track of current sequence by milliseconds
        sequenceMilliSecClock++
        sequenceCallback.updateSeqPerMilliSec(sequenceMilliSecClock)

        //Reset the counter when we reach the end of sequence
        if (sequenceMilliSecClock == BpmUtils.getSequenceTimeInMilliSecs(
                barMeasure.value ?: 2
            )
        ) {
            sequenceMilliSecClock = 0
        }
    }

    private fun timeLineProgress() {
        //Count in conditions - don't start the  timeline until after count in
        if (countInCount > ApplicationState.countInCountPreference
            && ApplicationState.isCountInActivated && ApplicationState.isRecording
        ) {
            callback.updateTimeLineProgress(sequenceMilliSecClock)
        } else if (!ApplicationState.isArmedToRecord || !ApplicationState.isCountInActivated) {
            callback.updateTimeLineProgress(sequenceMilliSecClock)
        }

    }


    private fun stopPlayEngine() {

        countInExecutor.shutdown()
        playEngineExecutor.shutdownNow()
        resetCounters()

    }

    private var countInCount = 1
    private val engineTask = Runnable { runEngine() }
    private val countInTask = Runnable {

        if (countInCount <= ApplicationState.countInCountPreference) {

            _events.postValue(Event.UpdateCountInClock(countInCount))
            callback.updateMetronomeSound()
            println("inside of the countInTask")

        } else if (countInCount > ApplicationState.countInCountPreference) {
            countInExecutor.shutdown()
            callback.updateMetronomeSound()

            playEngineExecutor.scheduleAtFixedRate(engineTask, 0, 1000, TimeUnit.MICROSECONDS)


        }

        countInCount++

    }

    private fun startPlayEngine() {

        //println("inside of the countInTask")
        // countInExecutor = Executors.newScheduledThreadPool(1)
        // countInExecutor.scheduleAtFixedRate(countInTask, 0, BpmUtils.getBeatPerMilliSeconds()*1000, TimeUnit.MICROSECONDS)

        //playEngineExecutor = Executors.newScheduledThreadPool(1)
        // playEngineExecutor.scheduleAtFixedRate(task, 0, 1000, TimeUnit.MICROSECONDS)

        countInExecutor = Executors.newScheduledThreadPool(2)
        playEngineExecutor = Executors.newSingleThreadScheduledExecutor()

        if (ApplicationState.isRecording && ApplicationState.isCountInActivated) {

            countInExecutor.scheduleAtFixedRate(
                countInTask,
                0,
                BpmUtils.getBeatPerMilliSeconds() * 1000,
                TimeUnit.MICROSECONDS
            )

        } else {

            playEngineExecutor.scheduleAtFixedRate(engineTask, 0, 1000, TimeUnit.MICROSECONDS)
        }

    }

    private fun popupEditMenuRotaryKnob(value: Int, type : RotaryKnobType) {
        popupEditRotaryKnob.postValue(MainEditRotaryKnob(value,type))
    }


    private fun onPatternSelected(patternResourceId: Int, bar: Int) {
        if (ApplicationState.isPlaying) {

            //while playing - change the pattern on the next start of the sequence
            val delay =
                BpmUtils.getSequenceTimeInMilliSecs(barMeasure.value ?: 2) - sequenceMilliSecClock
            _events.value = Event.TimeRemainingBeforePatternChange(delay)
            // timeLeftBeforePatternChange.value = delay
            println("countend = inside onPatternSelected")
            Timer("SettingUp", false).schedule(delay) {
                println("countend = inside Timer")
                _events.postValue(Event.TimeRemainingBeforePatternChange(0L))
                // timeLeftBeforePatternChange.postValue(0L)
                patternSelected.postValue(patternResourceId)
                barMeasure.postValue(bar)

                resetCounters()
            }

        } else {

            patternSelected.postValue(patternResourceId)
            barMeasure.postValue(bar)

        }

    }

    private fun resetCounters() {
        //Reset all clocks/counters
        callback.updateUiClockEveryMilliSec(0L, 0L)
        callback.resetProgressBar()
        uiProgressBarMilliSecCounter = 0L
        metronomeCounter = 0L
        uIClockMilliSecondCounter = 0L
        uiClockBeatCount = 1L
        sequenceMilliSecClock = 0
        countInCount = 1

    }

    fun onAction(action: Action) {

        when (action) {
            Action.Run -> runEngine()
            Action.OnUndoTapped -> onUndoTapped()
            Action.OnUndoConfirmed -> onUndoConfirmed()
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
            is Action.OnEditRotaryKnobProgressChange -> popupEditMenuRotaryKnob(action.value, action.type)
            is Action.OnMainSliderMenuSelection -> TODO()
            Action.OnShowUndoErrorMsg -> _events.value = Event.UndoLisEmptyMsg
            Action.OnShowUndoConfirmMsg -> _events.value = Event.ShowUndoConfirmMsg
            is Action.OnPatternSelected -> onPatternSelected(action.patternResourceId, action.bar)
            is Action.OnBarMeasureUpdate -> {
            }

        }.exhaustive

    }

    sealed class Action {
        object Run : Action()
        object OnPlay : Action()
        object OnStop : Action()
        object OnUndoTapped : Action()
        object OnUndoConfirmed : Action()
        object OnSettingsTapped : Action()
        object OnLoadTapped : Action()
        data class OnNoteRepeatTapped(val isNoteRepeatActivated: Boolean) : Action()
        object OnNoteRepeatDoubleTapped : Action()
        object OnShowPatternUi : Action()
        object OnShowMainUi : Action()
        object OnShowUndoErrorMsg : Action()
        object OnShowUndoConfirmMsg : Action()
        data class OnPatternSelected(val patternResourceId: Int, val bar: Int) : Action()
        data class OnEditRotaryKnobProgressChange(val value: Int, val type : RotaryKnobType) : Action()
        data class OnMainSliderMenuSelection(val label: String) : Action()
        data class OnBarMeasureUpdate(val bar: Int) : Action()

    }

    sealed class Event {
        object UndoLisEmptyMsg : Event()
        object ShowSettings : Event()
        object ShowLoadMenu : Event()
        object ShowUndoConfirmMsg : Event()
        data class ActivateNoteRepeat(val isNoteRepeatActivated: Boolean) : Event()
        data class TimeRemainingBeforePatternChange(val remainingTime: Long) : Event()
        object ShowNoteRepeatMenu : Event()
        data class UpdateCountInClock(val countInCount: Int) : Event()

    }


}
