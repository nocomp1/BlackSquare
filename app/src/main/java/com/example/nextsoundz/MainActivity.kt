package com.example.nextsoundz


import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.AnimatedVectorDrawable
import android.media.AudioAttributes
import android.media.SoundPool
import android.media.midi.MidiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.example.nextsoundz.Adapters.TabsViewPagerAdapter
import com.example.nextsoundz.Fragments.*
import com.example.nextsoundz.Listeners.FabGestureDetectionListener
import com.example.nextsoundz.Managers.DrumPadSoundPool
import com.example.nextsoundz.Singleton.ApplicationState
import com.example.nextsoundz.Singleton.Bpm
import com.example.nextsoundz.Singleton.Definitions
import com.example.nextsoundz.Singleton.Metronome
import com.example.nextsoundz.Tasks.PlayEngineTask
import com.example.nextsoundz.ViewModels.SoundsViewModel
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), FabGestureDetectionListener.FabGestureListener,
    PlayEngineTask.MetronomeListener, SeekBar.OnSeekBarChangeListener {

    private lateinit var soundsViewModel: SoundsViewModel
    private lateinit var metronomeSoundPool: DrumPadSoundPool
    private var metronomeSoundId = R.raw.wood
    private var projectTempo: Long = 100L
    private var isMetronomeOn: Boolean = true
    lateinit var sharedPref: SharedPreferences
    lateinit var playEngineExecutor: ScheduledExecutorService
    lateinit var playEngineTask: PlayEngineTask
    lateinit var mygestureDetector: GestureDetector
    var GESTURETAGBUTTON = "MAINACTIVITYTOUCHMEBUTTON"
    var beatCount =0
    var milliPerBeat = 0L

    private var SETTINGS_REQUEST_CODE: Int = 200
    private var LOAD_SOUND_REQUEST_CODE: Int = 300
    var engineClock: Disposable? = null
    private lateinit var volumeSlider: SeekBar
    private lateinit var soundPool: SoundPool
    var metronomeInterval: Long? = null
    private var sound = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Subscribe app to channel "all" for cloud messages
        //post to topics/app
        FirebaseMessaging.getInstance().subscribeToTopic("all")

        //////Setting up project shared preferences/////////
        sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return

        //////// Getting and setting our project tempo///////
        projectTempo = sharedPref.getLong(getString(R.string.project_tempo), 100L)
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


        //Setting up View model to communicate to our fragments
        this.let {
            soundsViewModel = ViewModelProviders.of(it).get(SoundsViewModel::class.java)
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
     * call this method after changing the sound
     */
    fun loadMetronomeSound() {
        sound = soundPool.load(this, Metronome.getSoundId(), 1)
    }


    private fun playMetronomeSound(millisecond: Long) {

        if (Metronome.isActive()) {
            if (millisecond == metronomeInterval) {
                soundPool.play(sound, 1.0f, 1.0f, 10, 0, 1.0f)

                //always updating progress every beat
                updateProgressBar()
                metronomeInterval = millisecond + Bpm.getBeatPerMilliSeconds()
            }
        }
    }

    override fun updateProgressBar() {
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


    }


    private fun startUIclock(milliseconds: Long) {



        if (milliPerBeat == Bpm.getBeatPerMilliSeconds()){
            beatCount++
            milliPerBeat = 0L


        }
        if (beatCount > (ApplicationState.selectedBarMeasure*4)){
            beatCount =1
        }
        milliPerBeat++
        var uiClock = getString(R.string.ui_clock, beatCount, milliPerBeat)
        millisec_clock.text = uiClock

    }

    private fun setUpMenuforMainVolumeslider() {

        seekbar_slider_menu_toggle.setOnClickListener {


            val popupMenu = PopupMenu(this, seekbar_slider_menu_toggle)
            //Inflating the Popup using xml file
            popupMenu.menuInflater.inflate(R.menu.main_seekbar_popup_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener {

                Toast.makeText(this, "You Clicked : " + it.title, Toast.LENGTH_SHORT).show()

                true
            }
            popupMenu.show()
        }

    }


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

    override fun onPause() {
        super.onPause()


    }

    override fun onResume() {
        super.onResume()


    }

    fun startPlayEngine() {

        engineClock = Observable.interval(1, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                playMetronomeSound(it)
                startUIclock(it)


            }

    }


    fun stopPlayEngine() {

        engineClock!!.dispose()
        //Reset the metronome interval
        metronomeInterval = Bpm.getBeatPerMilliSeconds()

        //reset uiClock
        beatCount =0
        milliPerBeat = 0L


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
        startActivityForResult(intent, SETTINGS_REQUEST_CODE)
    }

    fun onUndoTapped(view: View) {}

    fun onLoadTapped(view: View) {
        val intent = Intent(applicationContext, LoadDrumSoundDialogActivity::class.java)
        startActivityForResult(intent, LOAD_SOUND_REQUEST_CODE)
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
        Log.d(" onStartTrackTouch", seekBar.toString())
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        DrumScreenHomeFragment().onStopTrackingTouch(seekBar)
        Log.d(" onStopTrackiTouch", seekBar.toString())
    }


}

