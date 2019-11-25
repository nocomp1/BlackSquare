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
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.nextsoundz.Adapters.TabsViewPagerAdapter
import com.example.nextsoundz.Fragments.*
import com.example.nextsoundz.Listeners.FabGestureDetectionListener
import com.example.nextsoundz.Listeners.HomeVolumeSliderListener
import com.example.nextsoundz.Singleton.Bpm
import com.example.nextsoundz.Singleton.Metronome
import com.example.nextsoundz.Util.MetronomeTask
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), FabGestureDetectionListener.FabGestureListener,
    MetronomeTask.MetronomeListener,HomeVolumeSliderListener.SliderListener {


    lateinit var metronomeExecutor: ScheduledExecutorService
    lateinit var metronomeTask: MetronomeTask
    lateinit var mygestureDetector: GestureDetector
    var GESTURETAGBUTTON = "MAINACTIVITYTOUCHMEBUTTON"
    private var isPlaying = false
    private var isRecording = false
    private var prefs: SharedPreferences? = null
    private lateinit var soundPool: SoundPool
    private var sound1: Int = 0
    val maxMetronomeIncrement = 25
    var maxProgress = 75
    var measureCount = 4
    private var SETTINGS_REQUEST_CODE: Int = 200
    private var LOAD_SOUND_REQUEST_CODE: Int = 300


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)









        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        Metronome.setSoundId(R.raw.wood)
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
        adapter.addFragment(SequenceFragment(), "Edit Sequence")
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
//
//        val viewSequence = layoutInflater.inflate(R.layout.home_custom_tab,null)
//        viewSequence.findViewById<ImageView>(R.id.icon).setBackgroundResource(R.drawable.ic_sequence)
//        tabs.getTabAt(2)!!.customView=viewSequence
//
//        val viewRecording = layoutInflater.inflate(R.layout.home_custom_tab,null)
//        viewRecording.findViewById<ImageView>(R.id.icon).setBackgroundResource(R.drawable.ic_mixer)
//        tabs.getTabAt(3)!!.customView=viewRecording

//        val viewSongMode = layoutInflater.inflate(R.layout.home_custom_tab,null)
//        viewSongMode.findViewById<ImageView>(R.id.icon).setBackgroundResource(R.drawable.ic_recording)
//        tabs.getTabAt(4)!!.customView=viewSongMode

//
        //create our listeners
        val fabGestureDetectionListener = FabGestureDetectionListener()
        val homeVolumeSliderListener = HomeVolumeSliderListener()
        metronomeTask = MetronomeTask(application)

        mygestureDetector = GestureDetector(this@MainActivity, fabGestureDetectionListener)

        var touchListener = View.OnTouchListener { v, event ->
            mygestureDetector.onTouchEvent(event)
        }

        play_stop_btn.setOnTouchListener(touchListener)
        voulume_slider.setOnTouchListener(homeVolumeSliderListener)

        //set our callback so we can deal with our gestures in this activity
        fabGestureDetectionListener.setFabGestureListener(this)
        homeVolumeSliderListener.setHomeVoulumeSliderListener(this)
        metronomeTask.setProgressListener(this)

    }

    @TargetApi(23)
    private fun setUpMidi() {

        val m = this.getSystemService(Context.MIDI_SERVICE) as MidiManager
        //Get List of Already Plugged In Entities
        val info = m.devices

        //notification when, for example, keyboards are plugged in or unplugged
        // m.registerDeviceCallback({ x -> })

    }



    override fun homeVoulumeSliderOntouch(v: View?, event: MotionEvent?) {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val screenWidth = displayMetrics.widthPixels

        val whereItouchX = event!!.rawX
val viewWidth = v!!.width
val startingXbounds =screenWidth-viewWidth

      //  val actWidth =width-viewWidth

        val whereIendAt:Float
        val inBetween :Float

                when (event!!.action) {

            MotionEvent.ACTION_DOWN -> {
                  Log.d("xx", "whereItouchX=  ${whereItouchX} ")
                // Log.d("xx", "screenWidth=  ${screenWidth}")
               // Log.d("xx", "viewWidth=  ${viewWidth}")
               // Log.d("xx", "startingXbounds=  ${startingXbounds}")
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d("xx", "whereItouchX...Moved=  ${event!!.rawX} ")

                  //   whereIendAt = event!!.rawX
                   //  inBetween=whereIendAt
               // Log.d("xx", "whereIendAt=  ${whereIendAt} ")
                   // Log.d("xx", "inBetween=  ${inBetween} ")
                    voulume_slider.translationX = (screenWidth-whereItouchX-viewWidth)



//                if(whereItouchX < whereIendAt){
//                    val inBetween = whereItouchX +whereIendAt
//                    voulume_slider.translationX = (whereItouchX+inBetween)
//                }

                //Log.d("xx", "XMove=  $layoutX")
            }


        }

    }





    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var paramX: Float
        var paramY: Float
        var layout = voulume_slider_layout as (LinearLayout)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        var layoutXandY = IntArray(2)

        voulume_slider_layout.getLocationOnScreen(layoutXandY)

//        val layoutX = event!!.rawX
//
//        val Y = voulume_slider.y

        when (event!!.action) {

            MotionEvent.ACTION_DOWN -> {
              //  Log.d("xx", "Xlayout=  ${layoutXandY[0]} ")
               // Log.d("xx", "Ylayout=  ${layoutXandY[1]}")
            }
            MotionEvent.ACTION_MOVE -> {

              //  voulume_slider.translationX =width-layoutX
                //Log.d("xx", "XMove=  $layoutX")
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


    override fun onFabSingleTapConfirmed(e: MotionEvent?) {
        Log.d(GESTURETAGBUTTON, "single confirmed")


        //user must double tap to stoprecording

        if (!isPlaying) {

            fabProgress.indeterminateDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.colorAccent),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            play_stop_btn.setImageResource(R.drawable.play_to_stop_anim)
            //fab.setBackgroundResource(R.drawable.selected_menu_btn)
            (play_stop_btn.drawable as AnimatedVectorDrawable).start()
            isPlaying = true


            Bpm.setBpm(140L)

            if (Metronome.isActive()) {
                startMetronome()
            }

            if (!isRecording) {


            }


        } else {


            // fab.setBackgroundResource(R.drawable.default_pad)
            play_stop_btn.setImageResource(R.drawable.stop_to_play_anim)
            (play_stop_btn.drawable as AnimatedVectorDrawable).start()
            isPlaying = false
            fabProgress.progress = 0

            if (Metronome.isActive()) {
                stopMetronome()
            }


        }
    }

    private fun startMetronome() {


        metronomeExecutor = Executors.newScheduledThreadPool(1)
        metronomeExecutor.scheduleWithFixedDelay(
            metronomeTask,
            0,
            Bpm.getBpm(),
            TimeUnit.MILLISECONDS
        )
        // metronomeExecutor.shutdown()

    }

    private fun stopMetronome() {


        metronomeExecutor.shutdownNow()

    }

    override fun onFabDoubleTap(e: MotionEvent?) {
        Log.d(GESTURETAGBUTTON, "double  tap ")

    }


    override fun onResume() {
        when (measureCount) {

            4 -> maxProgress = 100


            8 -> maxProgress = 200


        }

        super.onResume()
    }


    fun onRecord(view: View) {


        //start recording
        if (!isRecording) {
            record_btn.setImageResource(R.drawable.ic_recording_color)
            fabProgress.indeterminateDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.recording),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            //fab.setColorFilter(R.color.pinkendcolor)
            //fab.background=getDrawable(R.color.pinkendcolor)
            // (record_btn.drawable as AnimatedVectorDrawable).start()
            isRecording = true


        } else {

            //stop recording

            // fab.setBackgroundResource(R.drawable.default_pad)
            // fab.setColorFilter(R.color.colorPrimaryDark)
            record_btn.setImageResource(R.drawable.ic_record_default)
            // (play_stop_btn.drawable as AnimatedVectorDrawable).start()
            isRecording = false


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

        Log.d("bitch", "we are scrolling horizontal ")


        return false
    }


}

