package com.example.nextsoundz


import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.AnimatedVectorDrawable
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.media.midi.MidiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.nextsoundz.Adapters.TabsViewPagerAdapter
import com.example.nextsoundz.Fragments.*
import com.example.nextsoundz.Listeners.FabGestureDetectionListener
import com.example.nextsoundz.Util.Bpm
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), FabGestureDetectionListener.FabGestureListener {

    lateinit var mygestureDetector: GestureDetector
    var GESTURETAGBUTTON = "MAINACTIVITYTOUCHMEBUTTON"
    private var isPlaying = false
    private var isRecording = false

    private var soundPool: SoundPool? = null
    private var sound1: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()

        //Checking for Midi capabilities
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setUpMidi()

            } else {
                //customer can not use the controller feature of the app

                TODO("VERSION.SDK_INT < M")
            }


        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(12)
                .setAudioAttributes(audioAttributes)
                .build()
        } else {
            soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        }
        sound1 = soundPool!!.load(this, R.raw.sound1, 1)







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
        //create our listener
        val fabGestureDetectionListener = FabGestureDetectionListener()

        mygestureDetector = GestureDetector(this@MainActivity, fabGestureDetectionListener)

        var touchListener = View.OnTouchListener { v, event ->
            mygestureDetector.onTouchEvent(event)
        }

        fab.setOnTouchListener(touchListener)


        //set our callback so we can deal with our gestures in this activity
        fabGestureDetectionListener.setFabGestureListener(this)

    }

    @TargetApi(23)
    private fun setUpMidi() {

        val m = this.getSystemService(Context.MIDI_SERVICE) as MidiManager
        //Get List of Already Plugged In Entities
        val info = m.getDevices()

        //notification when, for example, keyboards are plugged in or unplugged
        // m.registerDeviceCallback({ x -> })

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


    var thread=Thread()


    override fun onFabSingleTapConfirmed(e: MotionEvent?) {
        Log.d(GESTURETAGBUTTON, "single confirmed")

        //user must double tap to stoprecording
        if (!isRecording) {
            if (!isPlaying) {

                fabProgress.getIndeterminateDrawable().setColorFilter(
                    ContextCompat.getColor(this, R.color.colorAccent),
                    android.graphics.PorterDuff.Mode.MULTIPLY
                )
                fab.setImageResource(R.drawable.play_to_stop_anim)
                //fab.setBackgroundResource(R.drawable.selected_menu_btn)
                (fab.drawable as AnimatedVectorDrawable).start()
                isPlaying = true



                val selectedBpm = 120L
                val bpm = Bpm(selectedBpm)
            Log.d("tempo",bpm.getTempo().toString())


                val hdlr = Handler()

               var i = fabProgress.getProgress()

          Thread(Runnable {
                    while (i < 96) {
                        i += 12

                        soundPool!!.play(sound1, 1.0f, 1.0f, 0, 0, 10f)
                        // Update the progress bar and display the current value in text view
                        hdlr.post(Runnable {
                            fabProgress.setProgress(i)

                            if(i == 96){
                                i=0
                                fabProgress.setProgress(i)
                            }

                        })
                        try {
                            // Sleep for  milliseconds per beat to show the progress slowly.
                            Thread.sleep(bpm.getTempo())
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }

                    }
                }).start()

//
//                thread.run {
//
//                    while (i < 96) {
//                        i += 12
//
//                        soundPool!!.play(sound1, 1.0f, 1.0f, 0, 0, 10f)
//                        // Update the progress bar and display the current value in text view
//                        hdlr.post(Runnable {
//                            fabProgress.setProgress(i)
//
//                            if(i == 96){
//                                i=0
//                                fabProgress.setProgress(i)
//                            }
//
//                        })
//                        try {
//                            // Sleep for  milliseconds per beat to show the progress slowly.
//                            Thread.sleep(bpm.getTempo())
//                        } catch (e: InterruptedException) {
//                            e.printStackTrace()
//                        }
//
//                    }
//
//
//                }
//
//                thread.start()

            } else {




                // fab.setBackgroundResource(R.drawable.default_pad)
                fab.setImageResource(R.drawable.stop_to_play_anim)
                (fab.drawable as AnimatedVectorDrawable).start()
                isPlaying = false


            }
        }
    }



    override fun onFabDoubleTap(e: MotionEvent?) {
        Log.d(GESTURETAGBUTTON, "double  tap ")


        //start recording
        if (!isRecording) {
            fab.setImageResource(R.drawable.play_to_stop_recordong_anim)
            fabProgress.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.recording),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            //fab.setColorFilter(R.color.pinkendcolor)
            //fab.background=getDrawable(R.color.pinkendcolor)
            (fab.drawable as AnimatedVectorDrawable).start()
            isRecording = true


        } else {

            //stop recording

            // fab.setBackgroundResource(R.drawable.default_pad)
            // fab.setColorFilter(R.color.colorPrimaryDark)
            fab.setImageResource(R.drawable.stop_to_play_recording)
            (fab.drawable as AnimatedVectorDrawable).start()
            isRecording = false


        }


    }
}
