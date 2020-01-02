package com.example.blacksquare


import android.annotation.TargetApi
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Typeface
import android.graphics.drawable.AnimatedVectorDrawable
import android.media.AudioAttributes
import android.media.SoundPool
import android.media.midi.MidiManager
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.ArrayMap
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.blacksquare.Adapters.TabsViewPagerAdapter
import com.example.blacksquare.Fragments.*
import com.example.blacksquare.Listeners.FabGestureDetectionListener
import com.example.blacksquare.Managers.DrumPadSoundPool
import com.example.blacksquare.Managers.SoundResManager
import com.example.blacksquare.Objects.PadSequenceTimeStamp
import com.example.blacksquare.Singleton.ApplicationState
import com.example.blacksquare.Singleton.Bpm
import com.example.blacksquare.Singleton.Definitions
import com.example.blacksquare.Singleton.Metronome
import com.example.blacksquare.Tasks.PlayEngineTask
import com.example.blacksquare.ViewModels.SoundsViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), FabGestureDetectionListener.FabGestureListener,
    PlayEngineTask.MetronomeListener, SeekBar.OnSeekBarChangeListener {

    private lateinit var soundPool2: DrumPadSoundPool
    private var uiClockSecondsCount: Long = 0
    private var secondMilliInterval: Long = 1000
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private lateinit var soundsViewModel: SoundsViewModel
    private lateinit var metronomeSoundPool: DrumPadSoundPool
    private var metronomeSoundId = R.raw.wood
    private var projectTempo: Long = 100L
    private var isMetronomeOn: Boolean = true
    lateinit var sharedPref: SharedPreferences
    lateinit var playEngineExecutor: ScheduledExecutorService
    lateinit var playEngineTask: PlayEngineTask
    lateinit var mygestureDetector: GestureDetector
    var beatCount = 0
    var milliPerBeat = 0L
    var engineClock: Disposable? = null
    private lateinit var volumeSlider: SeekBar
    private lateinit var soundPool: SoundPool
    var metronomeInterval: Long? = null
    private var sound = 0
    private var currentBarMeasure: Int? = null
    private var currentTempo: Long? = null
    external fun stringFromJNI()
    // external fun startEngine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //////Sound Pool for playback///////
        val drumPadSoundPool = DrumPadSoundPool(this)
        soundPool2 = drumPadSoundPool //startEngine()
        soundPool2.loadSoundKit(SoundResManager.getDefaultKitFilesIds())

        //Subscribe app to channel "all" for cloud messages
        //post to topics/app
        FirebaseMessaging.getInstance().subscribeToTopic("all")
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)


        //////Setting up project shared preferences/////////
        sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return

        //////// Getting and setting our project tempo///////
        projectTempo = sharedPref.getLong(getString(R.string.project_tempo), 120L)
        Bpm.setProjectTempo(projectTempo)

        //////// Setting state of the metronome/////////
        isMetronomeOn = sharedPref.getBoolean(getString(R.string.isMetronomeOn), true)
        metronomeSoundId = sharedPref.getInt(getString(R.string.metronomeSoundId), metronomeSoundId)
        Metronome.setState(isMetronomeOn)
        Metronome.setSoundId(metronomeSoundId)
        setUpMetronomeSoundPool()
        loadMetronomeSound()
        metronomeInterval = Bpm.getBeatPerMilliSeconds()

        playEngineTask = PlayEngineTask(application)

        ///// callback to increment progress bar per beat
        playEngineTask.setProgressListener(this)

        //full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()

        /////Set up MIDI  capabilities
        setUpMidi()

        ////Set up our Tabs
        val adapter = TabsViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(DrumScreenHomeFragment(), "Drums")
        adapter.addFragment(InstrumentFragment(), "Instrument")
        adapter.addFragment(SequenceFragment(), "Sequence")
        adapter.addFragment(RecordingFragment(), "Recording")
        adapter.addFragment(SongFragment(), "Song")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

//        //Setting up View model to communicate to our fragments
//        this.let {
//            soundsViewModel = ViewModelProviders.of(it).get(SoundsViewModel::class.java)
//        }
///
//
        // Set up our live data observers

        this.let {
            val sharedViewModel = ViewModelProviders.of(it).get(SoundsViewModel::class.java)

            ////Observer to communicate with the clock from main activity
            sharedViewModel.drumPadSequenceNoteList.observe(this, Observer {
                it?.let {

                    Log.d("pad1playback", "sharedViewModel= $it")

                }
            })


        }

        //Set up icon for tabs
//        val viewDrums = layoutInflater.inflate(R.layout.home_custom_tab,null)
//        viewDrums.findViewById<ImageView>(R.id.icon).setBackgroundResource(R.drawable.ic_mpc)
//        tabs.getTabAt(0)!!.customView=viewDrums


        //create our listener for the ui button touch events
        val fabGestureDetectionListener = FabGestureDetectionListener()
        mygestureDetector = GestureDetector(this@MainActivity, fabGestureDetectionListener)

        var touchListener = View.OnTouchListener { v, event ->
            mygestureDetector.onTouchEvent(event)
        }


        note_repeat_btn.setOnTouchListener(touchListener)

        //set our callback so we can deal with our gestures in this activity
        fabGestureDetectionListener.setFabGestureListener(this)

        //Main UI volume slider
        volumeSlider = main_ui_volume_seek_slider
        volumeSlider.setOnSeekBarChangeListener(this)

        //Selector menu for volume slider
        setUpMenuforMainVolumeslider()

        ////////Ui clock/////////
        val myTypeface = Typeface.createFromAsset(
            this.assets,
            "Digital3.ttf"
        )
        millisec_clock.typeface = myTypeface
        resetUiClock()


        isPhoneSetToDarkTheme()
    }

    /**
     * Pad Playback methods
     */
    private var pad1UndoListIndexPointer = 0
    private var pad2UndoListIndex = 0
    private var pad3UndoListIndex = 0
    private var pad4UndoListIndex = 0
    private var padSequenceIndex = 0L
    private var pad2SequenceListIndex = 0L
    private var pad3SequenceListIndex = 0L
    private var pad4SequenceListIndex = 0L


    fun onUndoTapped(view: View) {
        if (ApplicationState.pad1HitTimeStampList!!.isNotEmpty()) {

            lateinit var dialog: AlertDialog

            // Initialize a new instance of alert dialog builder object
            val builder = AlertDialog.Builder(this)
            // Set a title for alert dialog
            builder.setTitle("Undo?")
            // Set a message for alert dialog
            builder.setMessage("Are you sure you want to undo the last selected pad sequence?")

            // On click listener for dialog buttons
            val dialogClickListener = DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {


                        undoLastSequence()
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                        dialog.dismiss()
                    }
                }
            }

            // Set the alert dialog positive/yes button
            builder.setPositiveButton("YES", dialogClickListener)

            // Set the alert dialog negative/no button
            builder.setNegativeButton("NO", dialogClickListener)

            // Initialize the AlertDialog using builder object
            dialog = builder.create()

            // Finally, display the alert dialog
            dialog.show()

        } else {

            Toast.makeText(this, "Nothing to undo!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun undoLastSequence() {


        //Only subtract the index if its greater than zero
        if ((pad1UndoListIndexPointer > 0)) {


            //remove unwanted pattern from undo list
            ApplicationState.undoPad1HitTimeStampList!!.removeAt(pad1UndoListIndexPointer)


            //Move our index pointer back one in the undo list
            pad1UndoListIndexPointer--


            //copy the pattern in the undo list
            val copy = ArrayMap(ApplicationState.undoPad1HitTimeStampList!![pad1UndoListIndexPointer])
            //clear the original list
            ApplicationState.pad1HitTimeStampList!!.clear()
            //assign original list to newly pattern copied from undo list
            ApplicationState.pad1HitTimeStampList = copy



            Log.d(
                "undoff",
                "undo size ${ApplicationState.undoPad1HitTimeStampList!![pad1UndoListIndexPointer].size}"
            )
            Log.d("undoff", "hitlist size${ApplicationState.pad1HitTimeStampList!!.size}")
        }

    }


    /**
     *
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                              /
    //                                                                                                              /
    //   Undo List padHitUndoArrayList List structure is:                                                           /
    //                                                                                                              /
    //   (array list 1)                                                                                             /
    //     (pad index) (array list 2)   (array map) are like the different sequences that we can undo back to       /
    //       pad 1       sequences      each index has a time stamp that represents the time a user hit a pad       /
    //                                                                                                              /
    //     /////////    //////////     //////////////////////////////////////////////                               /
    //     /       /    /        /     /time stamp/         /           /           /                               /
    // --> /   0   /    /   0    /     /    0     /   1     /     2     /     3     /                               /
    //     /       /    /        /     /          /         /           /           /                               /
    //     /////////    //////////     //////////////////////////////////////////////////////////                   /
    //                  /        /     /          /         /           /           /           /                   /
    //              --> /   1    / --> /    0     /    1    /     2     /     3     /     4     /                   /
    //                  /        /     /          /         /           /           /           /                   /
    //                  //////////     //////////////////////////////////////////////////////////                   /
    //                                                                                                              /
    //                                                                                                              /
    //  Example: We go into our first pad index 0 - with our latest index pointer at index 1 of the                 /
    //  array list (sequences) - Then we check the size of our array map which is our time stamps                   /
    //  against pad 1 array map size inside of "padHitTimeStampArrayList". If the size has increased then           /
    //  we make a copy of it and store it into the Undo List "padHitUndoArrayList" and increase our pointer by 1    /
    //  to point at the latest sequence. When we want to undo move our pointer to a previous index inside           /
    //  array list 2 (sequences) then update pad 1 array map inside "padHitTimeStampArrayList" to reflect the       /
    //  previous array map                                                                                          /
    //                                                                                                              /
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     *
     */
    private fun padPlayback(
        pad1HitTimeStampList: ArrayMap<Long, PadSequenceTimeStamp>?,
        padSequenceListIndex: Long,
        pad1LftVolume: Float,
        pad1RftVolume: Float,
        undoPad1HitTimeStampList: ArrayList<ArrayMap<Long, PadSequenceTimeStamp>>?,
        padUndoListIndexPointer: Int
    ) {



    }

    private fun pad1Playback() {
//
//        padPlayback(
//            ApplicationState.pad1HitTimeStampList,
//            pad1SequenceListIndex,
//            ApplicationState.pad1LftVolume,
//            ApplicationState.pad1RftVolume,
//            ApplicationState.undoPad1HitTimeStampList,
//            pad1UndoListIndexPointer
//        )

        if (ApplicationState.padHitTimeStampArrayList!!.isNotEmpty()) {

            if (ApplicationState.padHitTimeStampArrayList!![Definitions.pad1Index].contains(padSequenceIndex)) {
                soundPool2.startSound(
                    ApplicationState.padHitTimeStampArrayList!![Definitions.pad1Index][padSequenceIndex]!!.soundId,
                    ApplicationState.pad1LftVolume,
                    ApplicationState.pad1RftVolume
                )
            }

            // store array for undo action each time sequence loop
            // and only if there is a new hit to the array
            if (padSequenceIndex == Bpm.getPatternTimeInMilliSecs()) {
                if ( ApplicationState.padHitUndoArrayList!!.isEmpty()) {

                     val counter = 0
                    while (counter < ApplicationState.padHitTimeStampArrayList!!.size){

                        val copy = arrayListOf(ApplicationState.padHitTimeStampArrayList!![counter])
                       // ApplicationState.undoPad1HitTimeStampList?.add(copy)
                        ApplicationState.padHitUndoArrayList!!.add(counter,copy)
                    }



                    Log.d(
                        "undoff",
                        "undo Arrayvmap Size inside is empty= ${ApplicationState.undoPad1HitTimeStampList!![pad1UndoListIndexPointer].size}"
                    )

                    Log.d(
                        "undoff",
                        "undo pad1HitTimeStampList Size= ${ApplicationState.pad1HitTimeStampList!!.size }"
                    )

                }







                else if ((ApplicationState.pad1HitTimeStampList!!.size > ApplicationState.undoPad1HitTimeStampList!![pad1UndoListIndexPointer].size)
                ){

                    //if we have new hits during this loop we add to list
                    val copy = ArrayMap(ApplicationState.pad1HitTimeStampList!!)
                    ApplicationState.undoPad1HitTimeStampList?.add(copy)

                    //then we move to the next index for next loop around
                    pad1UndoListIndexPointer++


                    //Log.d("undoff", "padUndoListIndex size${pad1UndoListIndex}")
                    Log.d(
                        "undoff",
                        "undo pad1HitTimeStampList Size= ${ApplicationState.pad1HitTimeStampList!!.size }"
                    )

                    Log.d(
                        "undoff",
                        "undo Arrayvmap Size= ${ApplicationState.undoPad1HitTimeStampList!![pad1UndoListIndexPointer].size}"
                    )
                }
            }

            if (ApplicationState.uiSequenceMillisecCounter == Bpm.getPatternTimeInMilliSecs()) {
                padSequenceIndex = 0
            }
            //move to the next index
            padSequenceIndex++
        }
    }









    fun pad2Playback() {

        if (ApplicationState.pad2HitTimeStampList!!.isNotEmpty()) {

            if (ApplicationState.pad2HitTimeStampList!!.containsKey(pad2SequenceListIndex)) {

                soundPool2.startSound(
                    ApplicationState.pad2HitTimeStampList!![pad2SequenceListIndex]!!.soundId,
                    ApplicationState.pad2LftVolume,
                    ApplicationState.pad2RftVolume
                )
            }

            // store array for undo action each time sequence loop
            //and only if there is a new hit to the array
            if (pad2SequenceListIndex == Bpm.getPatternTimeInMilliSecs()) {
                if (ApplicationState.undoPad2HitTimeStampList!!.isEmpty()) {

                    val copy = ArrayMap(ApplicationState.pad2HitTimeStampList!!)
                    ApplicationState.undoPad2HitTimeStampList?.add(copy)
                }
                if (ApplicationState.pad2HitTimeStampList!!.size > ApplicationState.undoPad2HitTimeStampList!![pad2UndoListIndex].size) {

                    //if we have new hits during this loop we add to list
                    val copy = ArrayMap(ApplicationState.pad2HitTimeStampList!!)
                    ApplicationState.undoPad2HitTimeStampList?.add(copy)
                    //then we move to the next index for next loop around
                    pad2UndoListIndex++
                }
            }
            if (ApplicationState.uiSequenceMillisecCounter == Bpm.getPatternTimeInMilliSecs()) {
                pad2SequenceListIndex = 0
            }
            //move to the next index
            pad2SequenceListIndex++
        }
    }


    fun pad3Playback() {
        if (ApplicationState.pad3HitTimeStampList!!.isNotEmpty()) {

            if (ApplicationState.pad3HitTimeStampList!!.containsKey(pad3SequenceListIndex)) {

                soundPool2.startSound(
                    ApplicationState.pad3HitTimeStampList!![pad3SequenceListIndex]!!.soundId,
                    ApplicationState.pad3LftVolume,
                    ApplicationState.pad3RftVolume
                )
            }
            // store array for undo action each time sequence loop
            //and only if there is a new hit to the array
            if (pad3SequenceListIndex == Bpm.getPatternTimeInMilliSecs()) {
                if (ApplicationState.undoPad3HitTimeStampList!!.isEmpty()) {

                    val copy = ArrayMap(ApplicationState.pad3HitTimeStampList!!)
                    ApplicationState.undoPad3HitTimeStampList?.add(copy)
                }
                if (ApplicationState.pad3HitTimeStampList!!.size > ApplicationState.undoPad3HitTimeStampList!![pad3UndoListIndex].size) {

                    //if we have new hits during this loop we add to list
                    val copy = ArrayMap(ApplicationState.pad3HitTimeStampList!!)
                    ApplicationState.undoPad3HitTimeStampList?.add(copy)
                    //then we move to the next index for next loop around
                    pad3UndoListIndex++
                }
            }
            if (ApplicationState.uiSequenceMillisecCounter == Bpm.getPatternTimeInMilliSecs()) {
                pad3SequenceListIndex = 0
            }
            //move to the next index
            pad3SequenceListIndex++
        }
    }


    fun pad4Playback() {
        if (ApplicationState.pad4HitTimeStampList!!.isNotEmpty()) {

            if (ApplicationState.pad4HitTimeStampList!!.containsKey(pad4SequenceListIndex)) {

                soundPool2.startSound(
                    ApplicationState.pad4HitTimeStampList!![pad4SequenceListIndex]!!.soundId,
                    ApplicationState.pad4LftVolume,
                    ApplicationState.pad4RftVolume
                )
            }
            // store array for undo action each time sequence loop
            //and only if there is a new hit to the array
            if (pad4SequenceListIndex == Bpm.getPatternTimeInMilliSecs()) {
                if (ApplicationState.undoPad4HitTimeStampList!!.isEmpty()) {

                    val copy = ArrayMap(ApplicationState.pad4HitTimeStampList!!)
                    ApplicationState.undoPad4HitTimeStampList?.add(copy)
                }
                if (ApplicationState.pad4HitTimeStampList!!.size > ApplicationState.undoPad4HitTimeStampList!![pad4UndoListIndex].size) {

                    //if we have new hits during this loop we add to list
                    val copy = ArrayMap(ApplicationState.pad4HitTimeStampList!!)
                    ApplicationState.undoPad4HitTimeStampList?.add(copy)
                    //then we move to the next index for next loop around
                    pad4UndoListIndex++
                }
            }
            if (ApplicationState.uiSequenceMillisecCounter == Bpm.getPatternTimeInMilliSecs()) {
                pad4SequenceListIndex = 0
            }
            //move to the next index
            pad4SequenceListIndex++
        }
    }


    external fun add(a: Int, b: Int): Int

    private fun isPhoneSetToDarkTheme() {

        val mode = resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
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
                Toast.makeText(this, "UI_MODE_NIGHT_UNDEFINED", Toast.LENGTH_SHORT).show()
            }
        }


    }

    /**
     * Metronome
     * call this method after changing the sound
     */

    fun loadMetronomeSound() {
        sound = soundPool.load(this, Metronome.getSoundId(), 1)
    }

    private fun playMetronomeSound() {


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

    private fun setUpMetronomeSoundPool() {
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
     * Main progress bar --- This is called every millisecond and updated every beat
     */

    override fun updateProgressBar() {

        //increase every beat
        if ((ApplicationState.uiProgressBarMillisecCounter == Bpm.getBeatPerMilliSeconds()) || (ApplicationState.uiProgressBarMillisecCounter == 0L)) {

            var maxMetronomeIncrement: Int?
            Log.d("xxx", "progress ${ApplicationState.selectedBarMeasureRadioButtonId}")

            when (ApplicationState.selectedBarMeasure) {

                Definitions.oneBar -> {
                    maxMetronomeIncrement = 25
                    fabProgress.max = 100
                    if (fabProgress.progress <= 100) {
                        if (fabProgress.progress == 100) {
                            fabProgress.progress = 0
                        }
                        fabProgress.progress = fabProgress.progress + maxMetronomeIncrement
                        Log.d("xxx", "progress ${fabProgress.progress}")
                    }
                }

                Definitions.twoBars -> {
                    maxMetronomeIncrement = 10
                    fabProgress.max = 80
                    if (fabProgress.progress <= 80) {
                        if (fabProgress.progress == 80) {
                            fabProgress.progress = 0
                        }
                        fabProgress.progress = fabProgress.progress + maxMetronomeIncrement
                        Log.d("xxx", "progress ${fabProgress.progress}")
                    }
                }

                Definitions.fourBars -> {
                    maxMetronomeIncrement = 10
                    fabProgress.max = 160
                    if (fabProgress.progress <= 160) {
                        if (fabProgress.progress == 160) {
                            fabProgress.progress = 0
                        }
                        fabProgress.progress = fabProgress.progress + maxMetronomeIncrement
                        Log.d("xxx", "progress ${fabProgress.progress}")
                    }


                }
                Definitions.eightBars -> {
                    maxMetronomeIncrement = 10
                    fabProgress.max = 320
                    if (fabProgress.progress <= 320) {
                        if (fabProgress.progress == 320) {
                            fabProgress.progress = 0
                        }
                        fabProgress.progress = fabProgress.progress + maxMetronomeIncrement
                        Log.d("xxx", "progress ${fabProgress.progress}")
                    }
                }
            }
            //Reset counter
            ApplicationState.uiProgressBarMillisecCounter = 0L
        }

        ApplicationState.uiProgressBarMillisecCounter++

    }

    fun resetProgressBar() {
        fabProgress.progress = 0
    }

    /**
     * UICLOCK
     */

    override fun updateUiClockEveryMilliSec() {

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
            updateProgressBar()

        }

        val seconds = String.format("%02d", uiClockSecondsCount)
        val milliSecPerBeat = String.format("%04d", ApplicationState.uiClockMillisecCounter)
        val beats = String.format("%02d", beatCount)
        val uIClock = SpannableStringBuilder(beats)
        uIClock.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent)),
            0, 2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        uIClock.append(" : ")
        //uIClock.append(oneHundmillis)
        uIClock.append(milliSecPerBeat)

        //post to ui thread
        millisec_clock.post {
            millisec_clock.text = uIClock
        }

        //update counters
        ApplicationState.uiClockMillisecCounter++
        ApplicationState.uiSequenceMillisecCounter++

        //call these functions every millisecond
        playMetronomeSound()
        updateProgressBar()
        //soundsViewModel.drumPadSequenceNoteList.postValue(ApplicationState.uiSequenceMillisecCounter)
        pad1Playback()
        //pad2Playback()
        // pad3Playback()
        // pad4Playback()
    }

    private fun resetUiClock() {
        beatCount = 0
        ApplicationState.uiClockMillisecCounter = 0L
        val uIClock = SpannableStringBuilder("00")
        uIClock.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent)),
            0, 2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        uIClock.append(" : ")
        uIClock.append("0000")

        //post to ui thread
        millisec_clock.post {
            millisec_clock.text = uIClock
        }
    }

    /**
     * Multi horizontal slider control
     */

    private fun setUpMenuforMainVolumeslider() {

        seekbar_slider_menu_toggle.setOnClickListener {

            val popupMenu = PopupMenu(this, seekbar_slider_menu_toggle)
            //Inflating the Popup using xml file
            popupMenu.menuInflater.inflate(R.menu.main_seekbar_popup_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener {

                // Toast.makeText(this, "You Clicked : " + it.title, Toast.LENGTH_SHORT).show()
                if (it.title == getString(R.string.volume)) {
                    seekbar_slider_menu_toggle.text = "V"
                }
                if (it.title == getString(R.string.pan)) {
                    seekbar_slider_menu_toggle.text = "P"
                }
                if (it.title == getString(R.string.pitch)) {
                    seekbar_slider_menu_toggle.text = "Pi"
                }

                true
            }
            popupMenu.show()
        }

    }

    //Volume Slider input from user --> to fragments

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        DrumScreenHomeFragment().onProgressChanged(seekBar, progress, fromUser)


    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        DrumScreenHomeFragment().onStartTrackingTouch(seekBar)
        Log.d(" onStartTrackTouch", seekBar.toString())
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        DrumScreenHomeFragment().onStopTrackingTouch(seekBar)
        Log.d(" onStopTrackiTouch", seekBar.toString())
    }

    /**
     * Midi
     */

    @TargetApi(23)
    private fun setUpMidi() {
        //Checking for Midi capabilities
        if (this.packageManager.hasSystemFeature(PackageManager.FEATURE_MIDI)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                val m = this.getSystemService(Context.MIDI_SERVICE) as MidiManager
                //Get List of Already Plugged In Entities
                val info = m.devices

                //notification when, for example, keyboards are plugged in or unplugged
                // m.registerDeviceCallback({ x -> })
            } else {
                //customer can not use the controller feature of the app

                TODO("VERSION.SDK_INT < M")
            }
        }
    }

    /**
     * Main play engine
     */

    fun startPlayEngine() {
//
//        engineClock = Observable.interval(1, TimeUnit.MILLISECONDS)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe {
//                playMetronomeSound(it)
//                updateUiClockEveryMilliSec(it)
//            }


        playEngineExecutor = Executors.newScheduledThreadPool(1)
        playEngineExecutor.scheduleAtFixedRate(playEngineTask, 0, 1000000, TimeUnit.NANOSECONDS)

    }


    fun stopPlayEngine() {

        playEngineExecutor.shutdownNow()


        //  engineClock!!.dispose()
        //Reset the metronome interval
        // metronomeInterval = Bpm.getBeatPerMilliSeconds()

        //reset uiClock
        resetUiClock()
        //reset progress bar
        resetProgressBar()
        //reset counters
        ApplicationState.metronomeMillisecCounter = 0L
        ApplicationState.uiProgressBarMillisecCounter = 0L
        ApplicationState.uiSequenceMillisecCounter = 0L
        padSequenceIndex = 0
        pad2SequenceListIndex = 0
        pad3SequenceListIndex = 0
        pad4SequenceListIndex = 0
    }

    /**
     * Touch and click events
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {

            MotionEvent.ACTION_DOWN -> {

            }
            MotionEvent.ACTION_MOVE -> {

            }

        }

        return super.onTouchEvent(event)
    }


    override fun onFabFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ) {

        Log.d(
            Companion.GESTURETAGBUTTON,
            "on fling\t Veloxity x \t\t" + velocityX + "\t\tveloxity y\t\t" + velocityY
        )

    }

    override fun onFabSingleTapConfirmed(e: MotionEvent?) {
        Log.d(Companion.GESTURETAGBUTTON, "single confirmed")

        if (ApplicationState.noteRepeatActive) {
            note_repeat_btn.setBackgroundResource(R.drawable.default_pad)
            ApplicationState.noteRepeatActive = false


        } else {

            note_repeat_btn.setBackgroundResource(R.drawable.note_repeat_selected_state)
            ApplicationState.noteRepeatActive = true

        }

    }

    override fun onFabDoubleTap(e: MotionEvent?) {
        // Log.d(GESTURETAGBUTTON, "double  tap ")

        ///Note repeat menu double tapped
        val intent = Intent(applicationContext, NoteRepeatDialogActivity::class.java)
        startActivityForResult(intent, Companion.LOAD_SOUND_REQUEST_CODE)
    }


    fun onPlayStopTapped(view: View) {

        if (!ApplicationState.isPlaying) {

            fabProgress.indeterminateDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.colorAccent),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            play_stop_btn.setImageResource(R.drawable.play_to_stop_anim)

            (play_stop_btn.drawable as AnimatedVectorDrawable).start()


            ///////Set the play state//////
            ApplicationState.isPlaying = true
            startPlayEngine()



            if (!ApplicationState.isRecording) {


            }

        } else {

            // fab.setBackgroundResource(R.drawable.default_pad)
            play_stop_btn.setImageResource(R.drawable.stop_to_play_anim)
            (play_stop_btn.drawable as AnimatedVectorDrawable).start()



            ApplicationState.isPlaying = false
            ApplicationState.isMillisecondClockPlaying = false


            fabProgress.progress = 0

            if (Metronome.isActive()) {
                stopPlayEngine()
            }

        }

    }


    fun onRecordTapped(view: View) {

        //start recording
        if (!ApplicationState.isRecording) {
            record_btn.setImageResource(R.drawable.ic_recording_color)
            fabProgress.indeterminateDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.recording),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            //fab.setColorFilter(R.color.pinkendcolor)
            //fab.background=getDrawable(R.color.pinkendcolor)
            // (record_btn.drawable as AnimatedVectorDrawable).start()
            ApplicationState.isRecording = true


        } else {

            //stop recording

            // fab.setBackgroundResource(R.drawable.default_pad)
            // fab.setColorFilter(R.color.colorPrimaryDark)
            record_btn.setImageResource(R.drawable.ic_record_default)
            // (play_stop_btn.drawable as AnimatedVectorDrawable).start()
            ApplicationState.isRecording = false

        }
    }

    fun onSettingsTapped(view: View) {

        val intent = Intent(applicationContext, SettingsDialogActivity::class.java)
        startActivityForResult(intent, Companion.SETTINGS_REQUEST_CODE)
    }


    fun onLoadTapped(view: View) {
        val intent = Intent(applicationContext, LoadDrumSoundDialogActivity::class.java)
        startActivityForResult(intent, Companion.LOAD_SOUND_REQUEST_CODE)
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {

        Log.d("hhh", "we are scrolling horizontal ")

        return false
    }

    /**
     * LifeCycle Events
     */

    override fun onPause() {
        super.onPause()
        currentBarMeasure = ApplicationState.selectedBarMeasure
        currentTempo = Bpm.getProjectTempo()
    }

    override fun onResume() {
        super.onResume()
        Log.d("mainActivity", "onResume is triggerd")
        //restart the play engine if bar has changed
        if (currentBarMeasure == null) {
            return
        }
        if ((currentBarMeasure != ApplicationState.selectedBarMeasure)
        //|| (currentTempo != Bpm.getProjectTempo())
        ) {
            if (ApplicationState.isPlaying) {
                stopPlayEngine()
                startPlayEngine()
            }
        }

        if (!Metronome.isActive()) {
            if (ApplicationState.isPlaying) {
                //resetProgressBar()
            }
        }

    }

    companion object {


        const val DARK_THEME_ENABLED_USER_PROPERTY = "UsesDarkMode"
        const val GESTURETAGBUTTON = "MAINACTIVITYTOUCHMEBUTTON"
        const val DARK_THEME_ENABLED_VALUE = "true"
        const val DARK_THEME_DISABLED_VALUE = "false"
        const val SETTINGS_REQUEST_CODE: Int = 200
        const val LOAD_SOUND_REQUEST_CODE: Int = 300

        init {
            // System.loadLibrary("native-lib")
        }


    }
}




