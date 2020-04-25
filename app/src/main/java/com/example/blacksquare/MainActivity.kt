package com.example.blacksquare


import android.animation.Animator
import android.annotation.TargetApi
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.graphics.Typeface
import android.graphics.drawable.AnimatedVectorDrawable
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.media.midi.MidiManager
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.transition.Scene
import com.example.blacksquare.Adapters.TabsViewPagerAdapter
import com.example.blacksquare.Fragments.*
import com.example.blacksquare.Listeners.FabGestureDetectionListener
import com.example.blacksquare.Managers.DrumPadPlayBack
import com.example.blacksquare.Singleton.ApplicationState
import com.example.blacksquare.Singleton.Bpm
import com.example.blacksquare.Singleton.Definitions
import com.example.blacksquare.Singleton.Metronome
import com.example.blacksquare.Utils.Kotlin.exhaustive
import com.example.blacksquare.ViewModels.MainViewModel
import com.example.blacksquare.ViewModels.SoundsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import java.io.InputStream
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.ByteOrder


class MainActivity : AppCompatActivity(), FabGestureDetectionListener.FabGestureListener,
    SeekBar.OnSeekBarChangeListener, MainViewModel.UpdateListener {

    private lateinit var padPlayback1: DrumPadPlayBack
    private lateinit var padPlayback2: DrumPadPlayBack
    private lateinit var padPlayback3: DrumPadPlayBack
    private lateinit var padPlayback4: DrumPadPlayBack
    private lateinit var mainViewModel: MainViewModel
    private lateinit var volumeSlider: SeekBar
    private lateinit var soundsViewModel: SoundsViewModel
    private lateinit var sharedPref: SharedPreferences

    private lateinit var mygestureDetector: GestureDetector

    private var metronomeSoundId = R.raw.wood
    private var projectTempo: Long = 100L
    private var isMetronomeOn: Boolean = true
    private var metronomeInterval: Long? = null
    private var currentBarMeasure: Int? = null
    private var currentTempo: Long? = null

    private lateinit var recordButton: ImageButton

    // external fun stringFromJNI()
    private lateinit var mainControlsSceneRoot: ViewGroup
    private lateinit var mainUiControlsScene: Scene
    private lateinit var mainUiPatternControlScene: Scene
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var uiClock: TextView

    /**
     * UI clock variables
     */
    private var beatCount = 0

    /**
     * SoundPool (Metronome) variables
     */
    private lateinit var soundPool: SoundPool
    private var sound = 0


    external fun startEngine(assetManager: AssetManager)
    //external fun startEngine()


    val TAG = "OpenSLESDemo"


    external fun setDefaultStreamValues(
        defaultSampleRate: Int,
        defaultFramesPerBurst: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //mainViewModel = MainViewModel(application)

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mainViewModel.setListener(this)


        recordButton = findViewById(R.id.record_btn)


        mainViewModel.viewState.observe(this, Observer { viewState ->
//            if (viewState.updateTimeLineProgress) {
//                updateTimeLineProgressBar()
//            }
//            if (viewState.playMetronome) {
//                playMetronomeSound()
//            }

        })


        mainViewModel.event.observe(this, Observer { event ->


            when (event) {

                is MainViewModel.Event.UndoLisEmptyMsg -> {
                    Toast.makeText(this, "Nothing to undo!", Toast.LENGTH_SHORT).show()
                }
                MainViewModel.Event.ShowSettings -> {
                    val intent = Intent(this, SettingsDialogActivity::class.java)
                    startActivityForResult(intent, SETTINGS_REQUEST_CODE)
                }
                MainViewModel.Event.ShowLoadMenu -> {
                    val intent = Intent(this, LoadDialogActivity::class.java)
                    startActivityForResult(intent, LOAD_REQUEST_CODE)
                }
                is MainViewModel.Event.ActivateNoteRepeat -> {
                    ApplicationState.noteRepeatActive = event.isNoteRepeatActivated
                }
                MainViewModel.Event.ShowNoteRepeatMenu -> {
                    val intent = Intent(applicationContext, NoteRepeatDialogActivity::class.java)
                    startActivityForResult(intent, NOTE_REPEAT_REQUEST_CODE)
                }
                is MainViewModel.Event.ActivateRecord -> {
                    ApplicationState.isRecording = event.isRecording
                }
                MainViewModel.Event.ShowUndoConfirmMsg -> showUndoConfirmMsg()

            }.exhaustive

        })

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        uiClock = findViewById(R.id.millisec_clock)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val myAudioMgr: AudioManager =
                applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val sampleRateStr = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)
            val defaultSampleRate = Integer.parseInt(sampleRateStr)
            val framesPerBurstStr =
                myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)
            val defaultFramesPerBurst = Integer.parseInt(framesPerBurstStr)

            setDefaultStreamValues(defaultSampleRate, defaultFramesPerBurst)
            // startEngine()
        }

        //Subscribe app to channel "all" for cloud messages
        //post to topics/app
        FirebaseMessaging.getInstance().subscribeToTopic("all")

        //////Setting up project shared preferences/////////
        sharedPref = this.getPreferences(Context.MODE_PRIVATE)

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
        adapter.addFragment(DrumScreenHomeFragment(), DRUMS_SCREEN_NAME)
        adapter.addFragment(InstrumentFragment(), INSTRUMENTS_SCREEN_NAME)
        adapter.addFragment(SequenceFragment(), SEQUENCE_SCREEN_NAME)
        adapter.addFragment(RecordingFragment(), RECORDING_SCREEN_NAME)
        adapter.addFragment(SongFragment(), SONG_SCREEN_NAME)
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)



        initializeUiComponents()


    }

    private fun showUndoConfirmMsg() {

        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
            .setTitle("Undo?")
            .setMessage("Are you sure you want to undo the last selected pad sequence?")

        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    mainViewModel.onAction(MainViewModel.Action.OnUndoConfirmed)
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    dialog.dismiss()
                }
            }
        }

        builder.setPositiveButton("YES", dialogClickListener)
            .setNegativeButton("NO", dialogClickListener)
            .create()
            .show()

    }

    private fun initializeUiComponents() {
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
        setUpMenuForMainVolumeSlider()

        ////////Ui clock/////////
        val myTypeface = Typeface.createFromAsset(
            this.assets,
            "Digital3.ttf"
        )
        uiClock.typeface = myTypeface


    }

    /**
     * Metronome
     * call this method after changing the sound
     */

    private fun loadMetronomeSound() {
        sound = soundPool.load(applicationContext, Metronome.getSoundId(), 1)
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

    private fun playMetronomeSound() {
        //Play the sound
        soundPool.play(sound, 1.0f, 1.0f, 10, 0, 1.0f)
    }

    override fun updateMetronomeSound() {
        playMetronomeSound()
    }

    override fun updateTimeLineProgress() {
        updateTimeLineProgressBar()
    }

    /**
     * ------------------------PRACTICING READING WAVE FILES----------------------------------------------------------------------------------------
     */
    private fun testReadingWaveHeader(assets: AssetManager) {
        //InputStream to a file path from the assets
        val fileInputStream: InputStream = assets.open("sound1.wav")

        //Read the file - we get an array with size of 4 to represent the first 4bytes of a wave file
        // that contains the type characters are 1 byte long - expecting "RIFF"
        val fourByteBuffers = ByteArray(4)
        val twoByteBuffers = ByteArray(2)
        //Now lets read our fileInputStream - which is at the 4bytes position
        fileInputStream.read(fourByteBuffers)
        // at this point, the buffer contains the 4 bytes
        Log.d("testReadingWave", "marks the file as a RIFF = ${String(fourByteBuffers)}")

        //Now lets read our fileSIZE /NEXT 4 BYTES == INTEGER which is at the 8bytes position
        fileInputStream.read(fourByteBuffers)

        // at this point, the buffer contains the 4 bytes
        Log.d("testReadingWave", "FILE SIZE = ${littleEndianConversion(fourByteBuffers)}")
        Log.d("testReadingWave", "FILE SIZE bigInteger = ${BigInteger(fourByteBuffers)}")

        //next 4bytes we should ge the file type "WAVE" which is at the 12bytes position
        fileInputStream.read(fourByteBuffers)
        Log.d("testReadingWave", "type of file = ${String(fourByteBuffers)}")

        //Format chunk marker "String".= 16bytes position
        fileInputStream.read(fourByteBuffers)
        Log.d("testReadingWave", "Format chunk = ${String(fourByteBuffers)}")

        //Length of Format chunk = 20bytes position
        fileInputStream.read(fourByteBuffers)
        Log.d(
            "testReadingWave",
            "Length of Format chunk = ${littleEndianConversion(fourByteBuffers)}"
        )

        //File Format 1 = PCM"= 22bytes position
        fileInputStream.read(twoByteBuffers)
        Log.d(
            "testReadingWave",
            "File Format 1 equals PCM = ${littleEndianConversion(fourByteBuffers)}"
        )

        //Number of channels 1 = mono 2 = stereo"= 24bytes position
        fileInputStream.read(twoByteBuffers)
        Log.d(
            "testReadingWave",
            "Number of channels 1 = mono 2 = stereo = ${littleEndianConversion(fourByteBuffers)}"
        )

        //Sampling rate= 28bytes position
        fileInputStream.read(fourByteBuffers)
        Log.d("testReadingWave", "Sampling rate= = ${integerConversion(fourByteBuffers)}")
        fileInputStream.close()


    }


    fun littleEndianConversion(bytes: ByteArray): Int {
        var result = 0
        for (i in bytes.indices) {
            result = result or (bytes[i].toInt() shl 8 * i)
        }
        return result
    }

    fun integerConversion(bytes: ByteArray): Int {
        var result = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).int

        return result
    }

    /**
     * ------------------------------------------------------------------------------------------------------------------------------
     */
    //external fun add(a: Int, b: Int): Int
    external fun startPlayEngineFromNative(): String

    // external fun stopPlayEngineFromNative()

    var count: Int = 0
    fun messageMe(text: String) {
        // updateEngineClockEveryMilliSec()
        count++
        //Log.d("nativeCode", text)
        Log.d("nativeCode", "${count} second")
    }

    /**
     * Pad Playback methods
     */

    fun onUndoTapped(view: View) {
        mainViewModel.onAction(MainViewModel.Action.OnUndoTapped)
    }

    /**
     * Main progress bar --- This is called every millisecond and updated every beat
     */

    private fun updateTimeLineProgressBar() {

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

    override fun resetProgressBar() {
        fabProgress.progress = 0
    }

    /**
     * UICLOCK
     */

    override fun updateUiClockEveryMilliSec(
        uIClockMilliSecondCounter: Long,
        beatCount: Long
    ) {

        val milliSecPerBeat = String.format("%04d", uIClockMilliSecondCounter)
        val beats = String.format("%02d", beatCount)
        val uIClock = SpannableStringBuilder(beats)
        uIClock.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.colorAccent)),
            0, 2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        uIClock.append(" : ")
        uIClock.append(milliSecPerBeat)

        uiClock.text = uIClock
    }

    /**
     * Multi functional horizontal slider control
     */

    private fun setUpMenuForMainVolumeSlider() {
        // default label
        var label = getString(R.string.main_slider_short_volume_label)
        seekbar_slider_menu_toggle.text =
            sharedPref.getString(getString(R.string.main_slider_prefs_key), label)

        seekbar_slider_menu_toggle.setOnClickListener {

            val popupMenu = PopupMenu(this, seekbar_slider_menu_toggle)
            //Inflating the Popup using xml file
            popupMenu.menuInflater.inflate(R.menu.main_seekbar_popup_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener {

                // Toast.makeText(this, "You Clicked : " + it.title, Toast.LENGTH_SHORT).show()
                if (it.title == getString(R.string.volume)) {
                    label = getString(R.string.main_slider_short_volume_label)
                    seekbar_slider_menu_toggle.text = label

                }
                if (it.title == getString(R.string.pan)) {
                    label = getString(R.string.main_slider_short_pan_label)
                    seekbar_slider_menu_toggle.text = label
                }
                if (it.title == getString(R.string.pitch)) {
                    label = getString(R.string.main_slider_short_pitch_label)
                    seekbar_slider_menu_toggle.text = label
                }

                sharedPref.edit().putString(getString(R.string.main_slider_prefs_key), label)
                    .apply()

                mainViewModel.onAction(MainViewModel.Action.OnMainSliderMenuSelection(label))
                true
            }
            popupMenu.show()
        }

    }


    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        mainViewModel.onAction(MainViewModel.Action.OnMainSliderProgressChange(progress))
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        Log.d(" onStartTrackTouch", seekBar.toString())
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
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
        Log.d("flingTest", "ITS FLINGING!!!!")


    }

    fun onShowPatternUi(view: View) {

        main_ui_button_controls.animate()
            .translationY(main_ui_button_controls.height.toFloat())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    main_ui_button_controls.visibility = View.INVISIBLE

                    mainViewModel.onAction(MainViewModel.Action.OnShowPatternUi)

                }

                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
            })


    }

    fun onShowMainUi(view: View) {
        main_ui_button_controls.visibility = View.VISIBLE
        main_ui_button_controls.animate()
            .translationY(0f)
            .setListener(null)
        mainViewModel.onAction(MainViewModel.Action.OnShowMainUi)
    }

    override fun onFabSingleTapConfirmed(e: MotionEvent?) {
        /////////////////NOTE REPEAT //////////////
        if (ApplicationState.noteRepeatActive) {
            note_repeat_btn.setBackgroundResource(R.drawable.default_pad)
            mainViewModel.onAction(MainViewModel.Action.OnNoteRepeatTapped(false))

        } else {

            note_repeat_btn.setBackgroundResource(R.drawable.note_repeat_selected_state)
            mainViewModel.onAction(MainViewModel.Action.OnNoteRepeatTapped(true))

        }

    }

    override fun onFabDoubleTap(e: MotionEvent?) {
        ///Note repeat menu double tapped
        mainViewModel.onAction(MainViewModel.Action.OnNoteRepeatDoubleTapped)

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
            mainViewModel.onAction(MainViewModel.Action.OnPlay)

            if (!ApplicationState.isRecording) {


            }

        } else {

            play_stop_btn.setImageResource(R.drawable.stop_to_play_anim)
            (play_stop_btn.drawable as AnimatedVectorDrawable).start()

            ApplicationState.isPlaying = false
            ApplicationState.isMillisecondClockPlaying = false

            fabProgress.progress = 0
            mainViewModel.onAction(MainViewModel.Action.OnStop)
            if (Metronome.isActive()) {

            }

        }

    }

    fun onRecordTapped(view: View) {
        //start recording
        if (!ApplicationState.isRecording) {
            recordButton.setImageResource(R.drawable.ic_recording_color)
            fabProgress.indeterminateDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.recording),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            mainViewModel.onAction(MainViewModel.Action.OnRecordTapped(true))

        } else {

            recordButton.setImageResource(R.drawable.ic_record_default)
            mainViewModel.onAction(MainViewModel.Action.OnRecordTapped(false))

        }
    }

    fun onSettingsTapped(view: View) {
        mainViewModel.onAction(MainViewModel.Action.OnSettingsTapped)

    }


    fun onLoadTapped(view: View) {
        mainViewModel.onAction(MainViewModel.Action.OnLoadTapped)

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
                //stopPlayEngine()
                // startPlayEngine()
            }
        }

        if (!Metronome.isActive()) {
            if (ApplicationState.isPlaying) {
                //resetProgressBar()
            }
        }

    }

    companion object {
        const val SETTINGS_REQUEST_CODE: Int = 200
        const val LOAD_REQUEST_CODE: Int = 300
        const val NOTE_REPEAT_REQUEST_CODE: Int = 100
        const val DRUMS_SCREEN_NAME = "Drums"
        const val INSTRUMENTS_SCREEN_NAME = "Instruments"
        const val SEQUENCE_SCREEN_NAME = "Sequence"
        const val RECORDING_SCREEN_NAME = "Recording"
        const val SONG_SCREEN_NAME = "Song"

        init {
            System.loadLibrary("native-lib")
            // System.loadLibrary("OpenSLESDemo")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == LOAD_REQUEST_CODE) {

        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}




