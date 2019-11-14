package com.example.nextsoundz.Fragments


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.nextsoundz.LoadDrumSoundDialogActivity
import com.example.nextsoundz.R
import com.example.nextsoundz.SettingsDialogActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drum_bank1.*
import kotlinx.android.synthetic.main.drum_bank1.view.*


class DrumScreenHomeFragment : Fragment(), View.OnClickListener {

    private var soundPool: SoundPool? = null

    private var LOAD_REQUEST_CODE: Int = 0
    private var SETTINGS_REQUEST_CODE: Int = 0
    private var sound1: Int = 0
    private var sound2: Int = 0
    private var sound3: Int = 0
    private var sound4: Int = 0
    private var sound5: Int = 0
    private var sound6: Int = 0
    private var sound7: Int = 0
    private var sound8: Int = 0
    private var sound9: Int = 0
    private var sound10: Int = 0
    private var sound11: Int = 0
    private var sound12: Int = 0
    private var padSelected: View? = null
    private var isPlaying = false
    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pager: ViewPager? = viewPager
        pager?.offscreenPageLimit = 5
        loadDefaultDrumKit()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.drum_fragment_layout, container, false)


        view.pad1.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> pad1()
                MotionEvent.ACTION_UP -> setPad1Selected()
                else -> false
            }
        }
        view.pad2.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> pad2()
                MotionEvent.ACTION_UP -> setPad2Selected()
                else -> false
            }
        }
        view.pad3.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> pad3()
                MotionEvent.ACTION_UP -> setPad3Selected()
                else -> false
            }
        }
        view.pad4.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> pad4()
                MotionEvent.ACTION_UP -> setPad4Selected()
                else -> false
            }
        }
        view.pad5.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> pad5()
                MotionEvent.ACTION_UP -> setPad5Selected()
                else -> false
            }
        }
        view.pad6.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> pad6()
                MotionEvent.ACTION_UP -> setPad6Selected()
                else -> false
            }
        }
        view.pad7.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> pad7()
                MotionEvent.ACTION_UP -> setPad7Selected()
                else -> false
            }
        }
        view.pad8.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> pad8()
                MotionEvent.ACTION_UP -> setPad8Selected()
                else -> false
            }
        }
        view.pad9.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> pad9()
                MotionEvent.ACTION_UP -> setPad9Selected()
                else -> false
            }
        }
        view.pad10.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> pad10()
                MotionEvent.ACTION_UP -> setPad10Selected()
                else -> false
            }
        }
        view.pad11.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> pad11()
                MotionEvent.ACTION_UP -> setPad11Selected()
                else -> false
            }
        }
        view.pad12.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> pad12()
                MotionEvent.ACTION_UP -> setPad12Selected()
                else -> false
            }
        }

        //Menu controls

        view.load_btn.setOnClickListener(this)
        view.play_stop_btn.setOnClickListener(this)
        view.record_armed_btn.setOnClickListener(this)

        return view
    }

    private fun setPad1Selected(): Boolean {

        if (padSelected != pad1 || padSelected == null) {
            pad1.setBackgroundResource(R.drawable.selected_buton)
            padSelected?.setBackgroundResource(R.drawable.default_pad)
            padSelected = pad1
        }

        return false
    }

    private fun setPad2Selected(): Boolean {

        if (padSelected != pad2 || padSelected == null) {
            pad2.setBackgroundResource(R.drawable.selected_buton)
            padSelected?.setBackgroundResource(R.drawable.default_pad)
            padSelected = pad2
        }

        return false
    }

    private fun setPad3Selected(): Boolean {

        if (padSelected != pad3 || padSelected == null) {
            pad3.setBackgroundResource(R.drawable.selected_buton)
            padSelected?.setBackgroundResource(R.drawable.default_pad)
            padSelected = pad3
        }

        return false
    }

    private fun setPad4Selected(): Boolean {

        if (padSelected != pad4 || padSelected == null) {
            pad4.setBackgroundResource(R.drawable.selected_buton)
            padSelected?.setBackgroundResource(R.drawable.default_pad)
            padSelected = pad4
        }

        return false
    }


    private fun setPad5Selected(): Boolean {

        if (padSelected != pad5 || padSelected == null) {
            pad5.setBackgroundResource(R.drawable.selected_buton)
            padSelected?.setBackgroundResource(R.drawable.default_pad)
            padSelected = pad5
        }

        return false
    }

    private fun setPad6Selected(): Boolean {

        if (padSelected != pad6 || padSelected == null) {
            pad6.setBackgroundResource(R.drawable.selected_buton)
            padSelected?.setBackgroundResource(R.drawable.default_pad)
            padSelected = pad6
        }

        return false
    }

    private fun setPad7Selected(): Boolean {

        if (padSelected != pad7 || padSelected == null) {
            pad7.setBackgroundResource(R.drawable.selected_buton)
            padSelected?.setBackgroundResource(R.drawable.default_pad)
            padSelected = pad7
        }

        return false
    }

    private fun setPad8Selected(): Boolean {

        if (padSelected != pad8 || padSelected == null) {
            pad8.setBackgroundResource(R.drawable.selected_buton)
            padSelected?.setBackgroundResource(R.drawable.default_pad)
            padSelected = pad8
        }

        return false
    }

    private fun setPad9Selected(): Boolean {

        if (padSelected != pad9 || padSelected == null) {
            pad9.setBackgroundResource(R.drawable.selected_buton)
            padSelected?.setBackgroundResource(R.drawable.default_pad)
            padSelected = pad9
        }

        return false
    }

    private fun setPad10Selected(): Boolean {

        if (padSelected != pad10 || padSelected == null) {
            pad10.setBackgroundResource(R.drawable.selected_buton)
            padSelected?.setBackgroundResource(R.drawable.default_pad)
            padSelected = pad10
        }

        return false
    }

    private fun setPad11Selected(): Boolean {

        if (padSelected != pad11 || padSelected == null) {
            pad11.setBackgroundResource(R.drawable.selected_buton)
            padSelected?.setBackgroundResource(R.drawable.default_pad)
            padSelected = pad11
        }

        return false
    }

    private fun setPad12Selected(): Boolean {

        if (padSelected != pad12 || padSelected == null) {
            pad12.setBackgroundResource(R.drawable.selected_buton)
            padSelected?.setBackgroundResource(R.drawable.default_pad)
            padSelected = pad12
        }

        return false
    }


    private fun loadDefaultDrumKit() {

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
        val am = activity!!.assets
        val context = activity?.applicationContext
        sound1 = soundPool!!.load(context, R.raw.sound1, 1)
        sound2 = soundPool!!.load(context, R.raw.sound1, 1)
        sound3 = soundPool!!.load(context, R.raw.sound1, 1)
        sound4 = soundPool!!.load(context, R.raw.sound1, 1)
        sound5 = soundPool!!.load(context, R.raw.sound1, 1)
        sound6 = soundPool!!.load(context, R.raw.sound1, 1)
        sound7 = soundPool!!.load(context, R.raw.sound1, 1)
        sound8 = soundPool!!.load(context, R.raw.sound1, 1)
        sound9 = soundPool!!.load(context, R.raw.sound1, 1)
        sound10 = soundPool!!.load(context, R.raw.sound1, 1)
        sound11 = soundPool!!.load(context, R.raw.sound1, 1)
        sound12 = soundPool!!.load(context, R.raw.sound1, 1)

    }

    init {
        System.loadLibrary("native-lib")
    }


    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.load_btn -> loadSound()
            R.id.play_stop_btn -> playStop()
            R.id.record_armed_btn -> recordArmed()
            R.id.settings_btn -> settings()
        }

    }


    fun pad1(): Boolean {
        soundPool!!.play(sound1, 1.0f, 1.0f, 0, 0, 10f)
        return false
    }

    fun pad2(): Boolean {
        soundPool!!.play(sound2, 1.0f, 1.0f, 0, 0, 10f)
        return false
    }

    fun pad3(): Boolean {
        soundPool!!.play(sound3, 1.0f, 1.0f, 0, 0, 10f)
        return false
    }

    fun pad4(): Boolean {
        soundPool!!.play(sound4, 1.0f, 1.0f, 0, 0, 10f)
        return false
        return false
    }

    fun pad5(): Boolean {
        soundPool!!.play(sound5, 1.0f, 1.0f, 0, 0, 10f)
        return false
    }

    fun pad6(): Boolean {
        soundPool!!.play(sound6, 1.0f, 1.0f, 0, 0, 10f)
        return false
    }

    fun pad7(): Boolean {
        soundPool!!.play(sound7, 1.0f, 1.0f, 0, 0, 10f)
        return false
    }

    fun pad8(): Boolean {
        soundPool!!.play(sound8, 1.0f, 1.0f, 0, 0, 10f)
        return false
    }

    fun pad9(): Boolean {
        soundPool!!.play(sound9, 1.0f, 1.0f, 0, 0, 10f)
        return false
    }

    fun pad10(): Boolean {
        soundPool!!.play(sound10, 1.0f, 1.0f, 0, 0, 10f)
        return false
    }

    fun pad11(): Boolean {
        soundPool!!.play(sound11, 1.0f, 1.0f, 0, 0, 10f)
        return false
    }

    fun pad12(): Boolean {
        soundPool!!.play(sound12, 1.0f, 1.0f, 0, 0, 10f)
        return false
    }


    override fun onSaveInstanceState(outState: Bundle) {

        // outState.putInt
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }


    // UI menu buttons

    private fun loadSound() {
        val intent = Intent(activity!!.applicationContext, LoadDrumSoundDialogActivity::class.java)
        startActivityForResult(intent,LOAD_REQUEST_CODE)
    }

    private fun settings() {
        val intent = Intent(activity!!.applicationContext, SettingsDialogActivity::class.java)
        startActivityForResult(intent,SETTINGS_REQUEST_CODE)
    }

    private fun playStop() {

        if (!isPlaying) {
            play_stop_btn.setImageResource(R.drawable.play_to_stop_anim)
            play_stop_btn.setBackgroundResource(R.drawable.selected_menu_btn)
            (play_stop_btn.drawable as AnimatedVectorDrawable).start()
            isPlaying = true


        } else {
            play_stop_btn.setBackgroundResource(R.drawable.default_pad)
            play_stop_btn.setImageResource(R.drawable.stop_to_play_anim)
            (play_stop_btn.drawable as AnimatedVectorDrawable).start()
            isPlaying = false


        }
    }


    fun recordArmed() {

        if (!isRecording) {
            record_armed_btn.setBackgroundResource(R.drawable.selected_menu_btn)
            isRecording = true


        } else {
            record_armed_btn.setBackgroundResource(R.drawable.default_pad)
            isRecording = false

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //  super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === LOAD_REQUEST_CODE) {
            if (resultCode === RESULT_OK) {
                // A contact was picked.  Here we will just display it
                // to the user.
                // startActivity(Intent(Intent.ACTION_VIEW, data))
            }
        }


        if (requestCode === SETTINGS_REQUEST_CODE) {
            if (resultCode === RESULT_OK) {
                // A contact was picked.  Here we will just display it
                // to the user.
                // startActivity(Intent(Intent.ACTION_VIEW, data))
            }
        }

    }
}