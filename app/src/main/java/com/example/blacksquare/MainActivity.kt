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
import android.media.SoundPool
import android.media.midi.MidiManager
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.transition.Slide
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.transition.Scene
import com.example.blacksquare.Adapters.TabsViewPagerAdapter
import com.example.blacksquare.Fragments.*
import com.example.blacksquare.Helpers.ApplicationState
import com.example.blacksquare.Helpers.Metronome
import com.example.blacksquare.Listeners.FabGestureDetectionListener
import com.example.blacksquare.Managers.SharedPrefManager
import com.example.blacksquare.Models.Pad
import com.example.blacksquare.Models.PopUpMainEditMenu.RotaryKnobType
import com.example.blacksquare.Utils.BpmUtils
import com.example.blacksquare.Utils.Kotlin.exhaustive
import com.example.blacksquare.Utils.SharedPrefKeys.APP_SHARED_PREFERENCES
import com.example.blacksquare.Utils.SharedPrefKeys.BAR_MEASURE_DEFAULT
import com.example.blacksquare.Utils.SharedPrefKeys.BAR_MEASURE_SELECTED
import com.example.blacksquare.Utils.SharedPrefKeys.IS_METRONOME_ON
import com.example.blacksquare.Utils.SharedPrefKeys.MAIN_SLIDER_CONTROL_TEXT_TITLE
import com.example.blacksquare.Utils.SharedPrefKeys.METRONOME_SOUND_ID
import com.example.blacksquare.Utils.SharedPrefKeys.PATTERN_SELECTED
import com.example.blacksquare.Utils.SharedPrefKeys.PATTERN_SELECTED_DEFAULT
import com.example.blacksquare.Utils.SharedPrefKeys.PROJECT_TEMPO
import com.example.blacksquare.ViewModels.MainViewModel
import com.example.blacksquare.Views.RotaryKnobView
import com.example.blacksquare.Views.ToggleButtonGroupExtensions.blinkingTransitionState
import com.example.blacksquare.Views.ToggleButtonGroupExtensions.getRadioBtnText
import com.example.blacksquare.Views.ToggleButtonGroupExtensions.setRadioBtnSelection
import com.example.blacksquare.Views.ToggleButtonGroupTableLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import java.io.InputStream
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.ByteOrder


class MainActivity : AppCompatActivity(), FabGestureDetectionListener.FabGestureListener,
    SeekBar.OnSeekBarChangeListener, RotaryKnobView.RotaryKnobListener,
    MainViewModel.UpdateListener,
    ToggleButtonGroupTableLayout.ToggleButtonListener {


    private lateinit var mainViewModel: MainViewModel
    private lateinit var volumeSlider: SeekBar
    private lateinit var sharedPref: SharedPreferences
    private var barMeasure = 2
    private lateinit var mygestureDetector: GestureDetector
    private lateinit var popupWindow: PopupWindow
    private var metronomeSoundId = R.raw.wood
    private var projectTempo: Long = 100L
    private var isMetronomeOn: Boolean = true
    private var metronomeInterval: Long? = null
    private var currentTempo: Long? = null
    private lateinit var padSelected: Pad

    //Edit menu controls
    private var editMenuRotaryKnob: RotaryKnobView? = null
    private var rotaryKnobType: RotaryKnobType = RotaryKnobType.Volume()
    private lateinit var editMenuSelection: Spinner
    private lateinit var editMenuCloseBtn: ImageButton
    private lateinit var editMenuAutomation: Button
    private lateinit var editMenuMute: Button
    private lateinit var editMenuSolo: Button


    private lateinit var recordButton: ImageButton
    private val filterListener: ToggleButtonGroupTableLayout.ToggleButtonListener = this

    // external fun stringFromJNI()
    private lateinit var mainControlsSceneRoot: ViewGroup
    private lateinit var mainUiControlsScene: Scene
    private lateinit var mainUiPatternControlScene: Scene
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var uiClock: TextView
    private lateinit var countInClock: TextView

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

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mainViewModel.setListener(this)

        // mainRotaryKnobView = findViewById(R.id.edit_knob)
        // main_edit_knob.listener =this

        // val toggleLayout = findViewById<ToggleButtonGroupTableLayout>(R.id.main_ui_pattern_radio_group)
        main_ui_pattern_radio_group.setUpListener(filterListener)
        countInClock = findViewById(R.id.count_in_view)
        recordButton = findViewById(R.id.record_btn)



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

                MainViewModel.Event.ShowUndoConfirmMsg -> showUndoConfirmMsg()
                is MainViewModel.Event.TimeRemainingBeforePatternChange -> {
                    //Set state of view before a pattern change
                    main_ui_pattern_radio_group.blinkingTransitionState(
                        getPatternSelected(),
                        event.remainingTime
                    )

                }
                is MainViewModel.Event.UpdateCountInClock -> {
                    updateCountInView(event.countInCount)

                    // runOnUiThread {   }

                }

            }.exhaustive

        })

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        uiClock = findViewById(R.id.millisec_clock)


//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            val myAudioMgr: AudioManager =
//                applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//            val sampleRateStr = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)
//            val defaultSampleRate = Integer.parseInt(sampleRateStr)
//            val framesPerBurstStr =
//                myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)
//            val defaultFramesPerBurst = Integer.parseInt(framesPerBurstStr)
//
//            setDefaultStreamValues(defaultSampleRate, defaultFramesPerBurst)
//            // startEngine()
//        }

        //Subscribe app to channel "all" for cloud messages
        //post to topics/app
        FirebaseMessaging.getInstance().subscribeToTopic("all")

        //////Setting up project shared preferences/////////
        sharedPref = this.getSharedPreferences(
            getString(APP_SHARED_PREFERENCES),
            Context.MODE_PRIVATE
        )


        //////// Getting and setting our project tempo///////
        projectTempo = sharedPref.getLong(getString(PROJECT_TEMPO), 120L)
        BpmUtils.setProjectTempo(projectTempo)

        //////// Setting state of the metronome/////////
        isMetronomeOn = sharedPref.getBoolean(getString(IS_METRONOME_ON), true)
        metronomeSoundId = sharedPref.getInt(getString(METRONOME_SOUND_ID), metronomeSoundId)
        Metronome.setState(isMetronomeOn)
        Metronome.setSoundId(metronomeSoundId)
        setUpMetronomeSoundPool()
        loadMetronomeSound()
        metronomeInterval = BpmUtils.getBeatPerMilliSeconds()

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
        adapter.addFragment(DrumScreenHomeFragment(), getString(R.string.drum_screen_name))
        adapter.addFragment(InstrumentFragment(), getString(R.string.instruments_screen_name))
        adapter.addFragment(SequenceFragment(), getString(R.string.sequence_screen_name))
        adapter.addFragment(RecordingFragment(), getString(R.string.mixing_screen_name))
        adapter.addFragment(SongFragment(), getString(R.string.song_screen_name))
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)


        //////////set up pattern choice
        //set the choice if there is one
        main_ui_pattern_radio_group.setRadioBtnSelection(getPatternSelected())


        initializeUiComponents()


        mainViewModel.sharedViewState.observe(this, Observer { viewState ->

            editMenuRotaryKnob?.setKnobPositionByValue(viewState.popupEditRotaryKnob.value)
            barMeasure = viewState.barMeasure

            padSelected = viewState.padSelected

        })


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
        //  volumeSlider = main_ui_volume_seek_slider
        //  volumeSlider.setOnSeekBarChangeListener(this)

        //Selector menu for volume slider
        setUpEditPopUpMenu()

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
            .setMaxStreams(10)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .build()
    }

    /**
     * This is called every beat
     */
    override fun updateMetronomeSound() {
        soundPool.play(sound, 1.0f, 1.0f, 10, 0, 1.0f)
    }

    private fun updateCountInView(countInCount: Int) {
        //show view on first count in
        if (countInCount == 1) {
            countInClock.visibility = View.VISIBLE
        }
        val count = ApplicationState.countInCountPreference.minus(countInCount)
        countInClock.text = String.format("%1s %2s", "Count in:", count + 1)

        val countInAnimationScaleOut =
            AnimationUtils.loadAnimation(applicationContext, R.anim.pad_scale_out)
        val countInAnimationScaleDwn =
            AnimationUtils.loadAnimation(applicationContext, R.anim.pad_scale_in)
        val countInAnimationFadeOUt =
            AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)

        countInClock.startAnimation(countInAnimationScaleOut)

        countInAnimationScaleOut.setAnimationListener(object :
            Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                countInClock.startAnimation(countInAnimationScaleDwn)

                if (countInCount == ApplicationState.countInCountPreference) {
                    countInClock.startAnimation(countInAnimationFadeOUt)
                }

            }
        })

        countInAnimationFadeOUt.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                if (countInCount == ApplicationState.countInCountPreference) {
                    countInClock.clearAnimation()
                    countInClock.visibility = View.GONE
                }
            }
        })
    }

    /**
     * Main progress bar --- This is called every millisecond
     */
    override fun updateTimeLineProgress(sequenceMilliSecClock: Long) {

        val maxProgress: Int?
        maxProgress = BpmUtils.getSequenceTimeInMilliSecs(barMeasure).toInt()
        fabProgress.max = maxProgress

        if (fabProgress.progress < maxProgress) {
            if (fabProgress.progress == maxProgress) {
                fabProgress.progress = 0
            }
            fabProgress.progress = sequenceMilliSecClock.toInt()
        }
    }

    fun onUndoTapped(view: View) {
        mainViewModel.onAction(MainViewModel.Action.OnUndoTapped)
    }

    override fun resetProgressBar() {
        fabProgress.progress = 0
    }

    /**
     * UICLOCK
     */

    //TODO CLOCK  AND TIMELINE WILL NOT RESET
    override fun updateUiClockEveryMilliSec(
        uIClockMilliSecondCounter: Long,
        beatCount: Long
    ) {

        val milliSecPerBeat = String.format("%04d", uIClockMilliSecondCounter)
        val beats = String.format("%02d", beatCount)
        val uIClock = SpannableStringBuilder(beats)
        uIClock.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent)),
            0, 2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        uIClock.append(" : ")
        uIClock.append(milliSecPerBeat)

        runOnUiThread { uiClock.text = uIClock }

    }

    /**
     * This brings up the edit popup window
     */
    private fun setUpEditPopUpMenu() {
        var isEditMenuShowing = false


        // default label
        val label = getString(R.string.main_edit_menu_label)
        edit_menu_button.text =
            sharedPref.getString(getString(MAIN_SLIDER_CONTROL_TEXT_TITLE), label)

        // Initialize a new layout inflater instance
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Inflate a custom view using layout inflater
        val editMenuView =
            inflater.inflate(R.layout.edit_menu_view, this.findViewById(R.id.edit_menu_layout))

        //initialize views that's inside of popup window
        editMenuRotaryKnob = editMenuView.findViewById(R.id.main_edit_knob)
        editMenuSelection = editMenuView.findViewById(R.id.edit_param_menu)
        editMenuCloseBtn = editMenuView.findViewById(R.id.close_edit_menu)
        editMenuAutomation = editMenuView.findViewById(R.id.automation_btn)
        editMenuMute = editMenuView.findViewById(R.id.mute_btn)
        editMenuSolo = editMenuView.findViewById(R.id.solo_btn)

        editMenuRotaryKnob?.listener = this

        // Initialize a new instance of popup window
        popupWindow = PopupWindow(
            editMenuView, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )
        popupWindow.contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        //popupWindow.isOutsideTouchable = true
        popupWindow.setOnDismissListener {
            isEditMenuShowing = false
        }



        edit_menu_button.setOnClickListener {

            // Set an elevation for the popup window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popupWindow.elevation = 10.0F
            }

            // If API level 23 or higher then execute the code
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                var x = 0
                var slideInEdge = Gravity.LEFT
                //Make sure we slide in popup window on side that has the most space
                if (padSelected.padPositionX?.let { it1 -> window.decorView.width.minus(it1) }!! > padSelected.padPositionX!!) {
                    x = window.decorView.width.minus(popupWindow.contentView.measuredWidth)
                    slideInEdge = Gravity.RIGHT
                }

                // Create a new slide animation for popup window enter transition
                val slideIn = Slide()
                slideIn.slideEdge = slideInEdge
                popupWindow.enterTransition = slideIn

                popupWindow.showAtLocation(
                    window.decorView,
                    Gravity.NO_GRAVITY,
                    x,
                    window.decorView.height / 2 - (popupWindow.contentView.measuredHeight / 2)
                )


                // Slide animation for popup window exit transition
                val slideOut = Slide()
                slideOut.slideEdge = Gravity.RIGHT
                popupWindow.exitTransition = slideOut

            }

            if (!isEditMenuShowing) {
                // Finally, show the popup window on app
                popupWindow.showAtLocation(
                    editMenuView, // Location to display popup window
                    Gravity.CENTER, // Exact position of layout to display popup
                    0, // X offset
                    0 // Y offset
                )
                isEditMenuShowing = true

            }

        }

        editMenuCloseBtn.setOnClickListener {
            popupWindow.dismiss()
            isEditMenuShowing = false

        }


        popUpEditMenuSelection()

        //This is to move the window around on touch
        var dX: Float = 0f
        var dY: Float = 0f
        var updatX = 0
        var updateY = 0
        editMenuView.setOnTouchListener { view, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    println("RawX = ${event.getRawX()}")
                    println("X = ${event.getX()}")

                    dX = (event.getRawX() - updatX)
                    dY = (event.getRawY() - updateY)

                    println("dx = ${dX}")
                }
                MotionEvent.ACTION_MOVE -> {

                    updatX = (event.getRawX() - dX).toInt()
                    updateY = (event.getRawY() - dY).toInt()
                    popupWindow.update(updatX, updateY, -1, -1, true)

                }

                MotionEvent.ACTION_UP -> {


                }
            }
            true
        }

    }

    private fun popUpEditMenuSelection() {

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.DrumScreenEdit,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            editMenuSelection.adapter = adapter
        }

        editMenuSelection.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                parent?.let {
                    val item = it.getItemAtPosition(position).toString()

                    rotaryKnobType = when (item) {

                        getString(RotaryKnobType.Volume().nameResource) -> {
                            RotaryKnobType.Volume()
                        }
                        getString(RotaryKnobType.Pan().nameResource) -> {
                            RotaryKnobType.Pan()
                        }
                        getString(RotaryKnobType.Pitch().nameResource) -> {
                            RotaryKnobType.Pitch()
                        }
                        getString(RotaryKnobType.HiPassFilter().nameResource) -> {
                            RotaryKnobType.HiPassFilter()
                        }
                        getString(RotaryKnobType.LowPassFilter().nameResource) -> {
                            RotaryKnobType.LowPassFilter()
                        }
                        getString(RotaryKnobType.Reverb().nameResource) -> {
                            RotaryKnobType.Reverb()
                        }
                        getString(RotaryKnobType.Delay().nameResource) -> {
                            RotaryKnobType.Delay()
                        }

                        else -> {
                            RotaryKnobType.Volume()
                        }
                    }


                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }


        }
    }

    //Main edit rotary knob
    override fun onRotate(value: Int) {
        mainViewModel.onAction(
            MainViewModel.Action.OnEditRotaryKnobProgressChange(
                value,
                rotaryKnobType
            )
        )
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        Log.d(" onStartTrackTouch", seekBar.toString())
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        Log.d(" onStopTrackiTouch", seekBar.toString())
    }


    /**
     * Touch and click events
     */

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

            if (ApplicationState.isArmedToRecord) {
                ApplicationState.isRecording = true
            }

            mainViewModel.onAction(MainViewModel.Action.OnPlay)

        } else {

            play_stop_btn.setImageResource(R.drawable.stop_to_play_anim)
            (play_stop_btn.drawable as AnimatedVectorDrawable).start()

            ApplicationState.isPlaying = false
            ApplicationState.isMillisecondClockPlaying = false
            ApplicationState.isRecording = false

            mainViewModel.onAction(MainViewModel.Action.OnStop)

        }

    }

    fun onRecordTapped(view: View) {
        //start recording
        if (!ApplicationState.isArmedToRecord) {
            recordButton.setImageResource(R.drawable.ic_recording_color)
            fabProgress.indeterminateDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.recording),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            ApplicationState.isArmedToRecord = true

            if (ApplicationState.isPlaying) {
                ApplicationState.isRecording = true
            }

        } else {
            ApplicationState.isArmedToRecord = false
            ApplicationState.isRecording = false
            recordButton.setImageResource(R.drawable.ic_record_default)

        }
    }

    //This for the main ui pattern buttons onClick
    override fun onToggleButtonClicked(radioButton: RadioButton?) {
        val patternChoice = main_ui_pattern_radio_group.getRadioBtnText()
        radioButton?.let { selection ->

            //use the pattern choice as the key to get
            // the saved bar for that pattern
            val barMeasure = sharedPref.getString(
                selection.text.toString(),
                getString(BAR_MEASURE_DEFAULT)
            )!!.toInt()

            //Match what pattern has been selected by title and send
            //the resource id
            SharedPrefManager.settingsPatternTitles().map { resouceId ->
                val patternTitle = getString(resouceId)
                //when we know the choice
                if (patternTitle == patternChoice) {

                    //store what pattern has been selected
                    sharedPref.edit()
                        .putString(
                            getString(PATTERN_SELECTED),
                            patternChoice
                        ).apply()

                    //send that id to the viewModel
                    mainViewModel.onAction(
                        MainViewModel.Action.OnPatternSelected(
                            resouceId,
                            barMeasure
                        )
                    )


                }
            }


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

    var currentBar = 2
    override fun onPause() {
        super.onPause()
        val patternChoice = main_ui_pattern_radio_group.getRadioBtnText()
        //store what pattern has been selected
        sharedPref.edit()
            .putString(
                getString(PATTERN_SELECTED),
                patternChoice
            ).apply()

        currentTempo = BpmUtils.getProjectTempo()

        currentBar = sharedPref.getInt(
            getString(BAR_MEASURE_SELECTED),
            getString(BAR_MEASURE_DEFAULT).toInt()
        )


    }

    private fun getPatternSelected(): String? {
        return sharedPref.getString(
            getString(PATTERN_SELECTED),
            getString(PATTERN_SELECTED_DEFAULT)
        )

    }

    override fun onResume() {
        super.onResume()

        // mainViewModel.test()

        //Get updated bar measure
        barMeasure = sharedPref.getInt(
            getString(BAR_MEASURE_SELECTED),
            getString(BAR_MEASURE_DEFAULT).toInt()
        )

        main_ui_pattern_radio_group.setRadioBtnSelection(getPatternSelected())

        //Match what pattern has been selected by title and send
        //the resource id to set the pattern selected
        SharedPrefManager.settingsPatternTitles().map { resouceId ->
            val patternTitle = getString(resouceId)
            if (patternTitle == getPatternSelected()) {
                //send that id to the viewModel
                mainViewModel.onAction(
                    MainViewModel.Action.OnPatternSelected(
                        resouceId,
                        barMeasure
                    )
                )
            }
        }


        // mainViewModel.onAction(MainViewModel.Action.OnBarMeasureUpdate(barMeasure))


        if (ApplicationState.isPlaying) {
            //check if the barMeasure has changed
            if (currentBar != barMeasure) {
                mainViewModel.onAction(MainViewModel.Action.OnStop)
                mainViewModel.onAction(MainViewModel.Action.OnPlay)

            }
        }

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


    companion object {
        const val SETTINGS_REQUEST_CODE: Int = 200
        const val LOAD_REQUEST_CODE: Int = 300
        const val QUANTIZE_REQUEST_CODE: Int = 400

        const val NOTE_REPEAT_REQUEST_CODE: Int = 100

        init {
            System.loadLibrary("native-lib")
            // System.loadLibrary("OpenSLESDemo")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == QUANTIZE_REQUEST_CODE) {


        }

        super.onActivityResult(requestCode, resultCode, data)
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


}




