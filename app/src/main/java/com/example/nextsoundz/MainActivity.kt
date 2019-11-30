package com.example.nextsoundz


import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.AnimatedVectorDrawable
import android.media.SoundPool
import android.media.midi.MidiManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.nextsoundz.Adapters.TabsViewPagerAdapter
import com.example.nextsoundz.Fragments.*
import com.example.nextsoundz.Listeners.FabGestureDetectionListener
import com.example.nextsoundz.Singleton.ApplicationState
import com.example.nextsoundz.Singleton.Bpm
import com.example.nextsoundz.Singleton.Metronome
import com.example.nextsoundz.Tasks.PlayEngineTask
import io.reactivex.internal.schedulers.ExecutorScheduler
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), FabGestureDetectionListener.FabGestureListener,
    PlayEngineTask.MetronomeListener, SeekBar.OnSeekBarChangeListener {

    private var metronomeSoundId = R.raw.wood
    private var projectTempo: Long = 100L
    private var playEngineMilliSecRate: Long = 1000L
    private var isMetronomeOn: Boolean = true
    lateinit var sharedPref: SharedPreferences
    lateinit var playEngineExecutor: ScheduledExecutorService
    lateinit var playEngineTask: PlayEngineTask
    lateinit var mygestureDetector: GestureDetector
    var GESTURETAGBUTTON = "MAINACTIVITYTOUCHMEBUTTON"
    private lateinit var soundPool: SoundPool
    private var sound1: Int = 0
    val maxMetronomeIncrement = 25
    var maxProgress = 75
    var measureCount = 4
    private var SETTINGS_REQUEST_CODE: Int = 200
    private var LOAD_SOUND_REQUEST_CODE: Int = 300

    private lateinit var volumeSlider: SeekBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //////Setttng up project shared preferences/////////
        sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return


        //////// Setting state of the metronome/////////
        isMetronomeOn = sharedPref.getBoolean(getString(R.string.isMetronomeOn), true)
        metronomeSoundId = sharedPref.getInt(getString(R.string.metronomeSoundId), metronomeSoundId)
        Metronome.setState(isMetronomeOn)
        Metronome.setSoundId(metronomeSoundId)
        playEngineTask = PlayEngineTask(application)
        ///// callback to increment progress bar per beat
        playEngineTask.setProgressListener(this)


        //////// Getting and setting our project tempo///////
        projectTempo = sharedPref.getLong(getString(R.string.project_tempo), 100L)
        Bpm.tempoToBeatPerMilliSec(projectTempo)


        //full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()

        //Checking for Midi capabilities
        if (this.packageManager.hasSystemFeature(PackageManager.FEATURE_MIDI)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setUpMidi()

            } else {
                //customer can not use the controller feature of the app

                TODO("VERSION.SDK_INT < M")
            }


        }


        val adapter = TabsViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(DrumScreenHomeFragment(), "Drums")
        adapter.addFragment(InstrumentFragment(), "Instrument")
        adapter.addFragment(SequenceFragment(), "Sequence")
        adapter.addFragment(RecordingFragment(), "Recording")
        adapter.addFragment(SongFragment(), "Song")
        viewPager.adapter = adapter

        tabs.setupWithViewPager(viewPager)


        //Set up icon for tabs
//        val viewDrums = layoutInflater.inflate(R.layout.home_custom_tab,null)
//        viewDrums.findViewById<ImageView>(R.id.icon).setBackgroundResource(R.drawable.ic_mpc)
//        tabs.getTabAt(0)!!.customView=viewDrums
//
//        val viewInstrument = layoutInflater.inflate(R.layout.home_custom_tab,null)
//        viewInstrument.findViewById<ImageView>(R.id.icon).setBackgroundResource(R.drawable.ic_instrument)
//        tabs.getTabAt(1)!!.customView=viewInstrument


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


    }

    @TargetApi(23)
    private fun setUpMidi() {

        val m = this.getSystemService(Context.MIDI_SERVICE) as MidiManager
        //Get List of Already Plugged In Entities
        val info = m.devices

        //notification when, for example, keyboards are plugged in or unplugged
        // m.registerDeviceCallback({ x -> })

    }

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
            GESTURETAGBUTTON,
            "on fling\t Veloxity x \t\t" + velocityX + "\t\tveloxity y\t\t" + velocityY
        )

    }


    fun startPlayEngine() {



        playEngineExecutor = Executors.newScheduledThreadPool(1)
        playEngineExecutor.schedule(playEngineTask,0,TimeUnit.MILLISECONDS)


    }

    fun stopPlayEngine() {

        playEngineExecutor.shutdownNow()


    }

    override fun onDestroy() {

//            if (Metronome.isActive()) {
//                playEngineExecutor.shutdownNow()
//            }

        super.onDestroy()
    }


    override fun onFabSingleTapConfirmed(e: MotionEvent?) {
        Log.d(GESTURETAGBUTTON, "single confirmed")

        if (ApplicationState.noteRepeatActive) {
            note_repeat_btn.setBackgroundResource(R.drawable.default_pad)
            ApplicationState.noteRepeatActive = false


        } else {

            note_repeat_btn.setBackgroundResource(R.drawable.note_repeat_selected_state)
            ApplicationState.noteRepeatActive = true

        }

    }

    override fun onFabDoubleTap(e: MotionEvent?) {
        Log.d(GESTURETAGBUTTON, "double  tap ")

        ///Note repeat menu double tapped
        val intent = Intent(applicationContext, NoteRepeatDialogActivity::class.java)
        startActivityForResult(intent, LOAD_SOUND_REQUEST_CODE)
    }


    override fun onResume() {

//        //Resume playing our Metronome when user returns
//        if (ApplicationState.isPlaying) {
//            if (Metronome.isActive()) {
//                startPlayEngine()
//            } else {
//
//                //reset progress to 0
//                fabProgress.progress = 0
//            }
//        }
//
//
//        when (measureCount) {
//
//            4 -> maxProgress = 100
//
//
//            8 -> maxProgress = 200
//
//
//        }

        super.onResume()
    }

    override fun onPause() {
//        //Resume playing our Metronome when user returns
//        if (ApplicationState.isPlaying) {
//            if (Metronome.isActive()) {
//                stopPlayEngine()
//            }
//        }

        super.onPause()
    }


    fun onPlayStopTapped(view: View) {

        if (!ApplicationState.isPlaying) {

            fabProgress.indeterminateDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.colorAccent),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            play_stop_btn.setImageResource(R.drawable.play_to_stop_anim)
            //fab.setBackgroundResource(R.drawable.selected_menu_btn)
            (play_stop_btn.drawable as AnimatedVectorDrawable).start()
            ApplicationState.isPlaying = true



            if (Metronome.isActive()) {
                startPlayEngine()
            }

            if (!ApplicationState.isRecording) {


            }

        } else {

            // fab.setBackgroundResource(R.drawable.default_pad)
            play_stop_btn.setImageResource(R.drawable.stop_to_play_anim)
            (play_stop_btn.drawable as AnimatedVectorDrawable).start()
            ApplicationState.isPlaying = false
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
        startActivityForResult(intent, SETTINGS_REQUEST_CODE)
    }

    fun onUndoTapped(view: View) {}

    fun onLoadTapped(view: View) {
        val intent = Intent(applicationContext, LoadDrumSoundDialogActivity::class.java)
        startActivityForResult(intent, LOAD_SOUND_REQUEST_CODE)
    }


    override fun updateProgressBar() {

//        var mediaPlayer: MediaPlayer? = MediaPlayer.create(this, R.raw.wood)
//        mediaPlayer?.start()
//
//        mediaPlayer!!.setOnCompletionListener { mp -> mp.release() }


        fabProgress.max = 100
        if (fabProgress.progress <= 100) {
            if (fabProgress.progress == 100) {
                fabProgress.progress = 0
            }
            fabProgress.progress = fabProgress.progress + maxMetronomeIncrement

            Log.d("xxx", "progress ${fabProgress.progress}")
        }

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


    //Volume Slider input from user --> to fragments

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        DrumScreenHomeFragment().onProgressChanged(seekBar, progress, fromUser)


    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        DrumScreenHomeFragment().onStartTrackingTouch(seekBar)
        Log.d("pussy onStartTrackTouch", seekBar.toString())
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        DrumScreenHomeFragment().onStopTrackingTouch(seekBar)
        Log.d("pussy onStopTrackiTouch", seekBar.toString())
    }


}

