package com.example.nextsoundz.Fragments


import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.nextsoundz.Listeners.RepeatListener
import com.example.nextsoundz.LoadDrumSoundDialogActivity
import com.example.nextsoundz.Managers.DrumPadSoundPool
import com.example.nextsoundz.Managers.SoundResManager
import com.example.nextsoundz.Objects.NoteRepeat
import com.example.nextsoundz.R
import com.example.nextsoundz.SettingsDialogActivity
import com.example.nextsoundz.Singleton.ApplicationState
import com.example.nextsoundz.Singleton.Bpm
import com.example.nextsoundz.Singleton.Definitions
import com.example.nextsoundz.Tasks.PlayEngineTask
import com.example.nextsoundz.ViewModels.SoundsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drum_bank1.view.*


class DrumScreenHomeFragment : BaseFragment(), View.OnClickListener,
    PlayEngineTask.NoteRepeatListener {

    override fun triggerNoteRepeat(noteRepeatObject: NoteRepeat) {

        noteRepeatInterval = noteRepeatObject


    }

    // private val handler: Handler = Handler(Looper.getMainLooper())


    override fun triggerNoteRepeat() {
        // Log.d("noteRepeat", "note repeat is triggered note repeat")
        // soundPool!!.play(sound1, pad1VolumeLeft, pad1VolumeRight, 0, 0, 1.0f)
        // DrumScreenHomeFragment().
        // test()

    }


    fun test() {


        // soundPool.startSound()

        //  Log.d("noteRepeat", "note repeat is triggered inside test")
    }


    private var selectedPadId: Int? = null
    private var lfVolumePad1: Float = 1.0f
    private var rtVolumePad1: Float = 1.0f
    private lateinit var noteRepeatInterval: NoteRepeat
    private var playEnginecounter: Long = 0
    //private var noteRepeatInterval: Long = 0
    private var defaulVolume: Float = 1.0f

    private var pad1VolumeLeft: Float = defaulVolume
    private var pad1VolumeRight: Float = defaulVolume
    lateinit var playEngineTask: PlayEngineTask
    lateinit var sharedPref: SharedPreferences
    private lateinit var soundPool: DrumPadSoundPool

    private var LOAD_REQUEST_CODE: Int = 100
    private var SETTINGS_REQUEST_CODE: Int = 200
    private var pad1Index: Int = 0
    private var pad2Index: Int = 1
    private var pad3Index: Int = 2
    private var pad4Index: Int = 3
    private var pad5Index: Int = 4
    private var pad6Index: Int = 5
    private var pad7Index: Int = 6
    private var pad8Index: Int = 7
    private var pad9Index: Int = 8
    private var pad10Index: Int = 9
    private var padSelectedView: View? = null
    private var isPlaying = false
    private var isRecording = false
    private var mainActivity = null
    private lateinit var volumeSeekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pager: ViewPager? = viewPager
        pager?.offscreenPageLimit = 5

        // loadDefaultDrumKit()


        // playEngineTask = PlayEngineTask(activity as MainActivity)
        // playEngineTask.setNoteRepeatListener(this)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sharedPref = activity!!.getPreferences(Context.MODE_PRIVATE) ?: return

        volumeSeekBar = activity!!.findViewById(R.id.main_ui_volume_seek_slider)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // volumeSeekBar.setProgress(100,false)
        } else {

            //add support for older versions

        }

        val drumPadSoundPool = DrumPadSoundPool(activity!!.applicationContext)
        soundPool = drumPadSoundPool

        //Load Default kit
        loadAsoundKit(SoundResManager.getDefaultKitFilesIds())


        ///Set up our live data observers

        activity?.let {
            val sharedViewModel = ViewModelProviders.of(it).get(SoundsViewModel::class.java)

            ////Observer for SoundPool
            sharedViewModel.drumPadSoundPool.observe(this, Observer {
                it?.let { sp ->
                    // soundPool = sp

                }
            })


        }
    }

    private fun loadAsoundKit(soundKitFilesIds: Map<Int, Int>) {
        soundPool.loadSoundKit(soundKitFilesIds)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


//        val view2: View =
//            layoutInflater.inflate(R.layout.activity_main, null, false)
//
//        volumeSeekBar = view2.findViewById<SeekBar>(R.id.main_ui_volume_seek_slider)


        val view: View =
            inflater.inflate(R.layout.drum_screen_home_fragment_layout, container, false)

//
//        view.pad1.setOnTouchListener(RepeatListener(1000,5000, View.OnClickListener {view
//
//            Log.d("noteRepeat", "note repeat is triggered note repeat")
//
//        }))


        view.pad1.setOnTouchListener(
            RepeatListener
                (0, Bpm.getNoteRepeatInterval(ApplicationState.selectedNoteRepeat),
                View.OnClickListener {

                    handlePadEvent(
                        pad1Index,
                        Definitions.pad1Id,
                        ApplicationState.pad1LftVolume,
                        ApplicationState.pad1RftVolume
                    )

                })
        )

        view.pad2.setOnTouchListener(
            RepeatListener
                (0, Bpm.getNoteRepeatInterval(ApplicationState.selectedNoteRepeat),
                View.OnClickListener {

                    handlePadEvent(
                        pad2Index,
                        Definitions.pad2Id,
                        ApplicationState.pad2LftVolume,
                        ApplicationState.pad2RftVolume
                    )

                })
        )
        view.pad3.setOnTouchListener(
            RepeatListener
                (0, Bpm.getNoteRepeatInterval(ApplicationState.selectedNoteRepeat),
                View.OnClickListener {

                    handlePadEvent(
                        pad3Index,
                        Definitions.pad3Id,
                        ApplicationState.pad3LftVolume,
                        ApplicationState.pad3RftVolume
                    )

                })
        )

        view.pad4.setOnTouchListener(
            RepeatListener
                (0, Bpm.getNoteRepeatInterval(ApplicationState.selectedNoteRepeat),
                View.OnClickListener {

                    handlePadEvent(
                        pad4Index,
                        Definitions.pad4Id,
                        ApplicationState.pad4LftVolume,
                        ApplicationState.pad4RftVolume
                    )

                })
        )

        view.pad5.setOnTouchListener(
            RepeatListener
                (0, Bpm.getNoteRepeatInterval(ApplicationState.selectedNoteRepeat),
                View.OnClickListener {

                    handlePadEvent(
                        pad5Index,
                        Definitions.pad5Id,
                        ApplicationState.pad5LftVolume,
                        ApplicationState.pad5RftVolume
                    )

                })
        )

        view.pad6.setOnTouchListener(
            RepeatListener
                (0, Bpm.getNoteRepeatInterval(ApplicationState.selectedNoteRepeat),
                View.OnClickListener {

                    handlePadEvent(
                        pad6Index,
                        Definitions.pad6Id,
                        ApplicationState.pad6LftVolume,
                        ApplicationState.pad6RftVolume
                    )

                })
        )
        view.pad7.setOnTouchListener(
            RepeatListener
                (0, Bpm.getNoteRepeatInterval(ApplicationState.selectedNoteRepeat),
                View.OnClickListener {

                    handlePadEvent(
                        pad7Index,
                        Definitions.pad7Id,
                        ApplicationState.pad7LftVolume,
                        ApplicationState.pad7RftVolume
                    )

                })
        )

        view.pad8.setOnTouchListener(
            RepeatListener
                (0, Bpm.getNoteRepeatInterval(ApplicationState.selectedNoteRepeat),
                View.OnClickListener {

                    handlePadEvent(
                        pad8Index,
                        Definitions.pad8Id,
                        ApplicationState.pad8LftVolume,
                        ApplicationState.pad8RftVolume
                    )

                })
        )
        view.pad9.setOnTouchListener(
            RepeatListener
                (0, Bpm.getNoteRepeatInterval(ApplicationState.selectedNoteRepeat),
                View.OnClickListener {

                    handlePadEvent(
                        pad9Index,
                        Definitions.pad9Id,
                        ApplicationState.pad9LftVolume,
                        ApplicationState.pad9RftVolume
                    )

                })
        )

        view.pad10.setOnTouchListener(
            RepeatListener
                (0, Bpm.getNoteRepeatInterval(ApplicationState.selectedNoteRepeat),
                View.OnClickListener {

                    handlePadEvent(
                        pad10Index,
                        Definitions.pad10Id,
                        ApplicationState.pad10LftVolume,
                        ApplicationState.pad10RftVolume
                    )

                })
        )

        return view
    }

    private fun handlePadEvent(padIndex: Int, padId: Int, padLftVolume: Float, padRtVolume: Float) {
        if (ApplicationState.hasLoadedAKit) {

            // get the sound list
            val soundArry = soundPool.getLoadedSoundKit()

            //check number of sounds in list
            if (soundArry.size > padIndex) {
                //get sound
                val soundId = soundArry[padIndex].soundId

                //play the sound
                playSound(
                    soundId,
                    padLftVolume,
                    padRtVolume
                )

            }
        } else if (ApplicationState.hasLoadedASound) {

            //get loaded sound map
            val soundLoadedMap = soundPool.getLoadedSoundId()

            //check if this is the pad selected for the loaded sound
            if (soundLoadedMap[getString(R.string.padSelected)] == padId) {
                //play sound
                playSound(
                    soundLoadedMap[getString(R.string.soundID)],
                    padLftVolume,
                    padRtVolume
                )


            }

        }

        setPadSelected(padId, padLftVolume, padRtVolume)


    }

    private fun playSound(soundId: Int?, padLftVolume: Float, padRftVolume: Float) {
        soundPool.startSound(
            soundId,
            padLftVolume,
            padRftVolume
        )

    }

    private fun setPad1Selected(): Boolean {


        if ((Definitions.pad1Id != ApplicationState.selectedPadId) ||
            (ApplicationState.selectedPadId == null)
        ) {

            // pad1.setBackgroundResource(R.drawable.selected_buton)
            // padSelectedView?.setBackgroundResource(R.drawable.default_pad)
            //  padSelectedView = pad1
            ApplicationState.selectedPadId = Definitions.pad1Id

            Log.d("noteRepeatVolume", "inside our pad1 if statement")
            Log.d("noteRepeatVolume", "padId== ${ApplicationState.selectedPadId}")
            // Log.d("noteRepeatVolume", "padId== $selectedPadId")
            //set the volume progress
            // lfVolumePad1 =ApplicationState.pad1LftVolume
            //rtVolumePad1 =ApplicationState.pad1RftVolume
            setVolumeSliderProgress(ApplicationState.pad1LftVolume, ApplicationState.pad1RftVolume)

        }

        return true
    }

    private fun setPadSelected(padId: Int, padLftVolume: Float, padRftVolume: Float): Boolean {

        if ((padId != ApplicationState.selectedPadId) ||
            (ApplicationState.selectedPadId == null)
        ) {
            ApplicationState.selectedPadId = padId
            setVolumeSliderProgress(padLftVolume, padRftVolume)
        }
        return false

    }


    private fun setPad2Selected(): Boolean {


        if ((Definitions.pad2Id != ApplicationState.selectedPadId) ||
            (ApplicationState.selectedPadId == null)
        ) {
            ApplicationState.selectedPadId = Definitions.pad2Id
            setVolumeSliderProgress(ApplicationState.pad2LftVolume, ApplicationState.pad2RftVolume)
        }
        return false
    }


    private fun setPad3Selected(): Boolean {
        if ((Definitions.pad3Id != ApplicationState.selectedPadId) ||
            (ApplicationState.selectedPadId == null)
        ) {
            ApplicationState.selectedPadId = Definitions.pad3Id
            setVolumeSliderProgress(ApplicationState.pad3LftVolume, ApplicationState.pad3RftVolume)
        }
        return false
    }

    private fun setPad4Selected(): Boolean {
        if ((Definitions.pad4Id != ApplicationState.selectedPadId) ||
            (ApplicationState.selectedPadId == null)
        ) {
            ApplicationState.selectedPadId = Definitions.pad4Id
            setVolumeSliderProgress(ApplicationState.pad4LftVolume, ApplicationState.pad4RftVolume)
        }
        return false
    }


    private fun setPad5Selected(): Boolean {
        if ((Definitions.pad5Id != ApplicationState.selectedPadId) ||
            (ApplicationState.selectedPadId == null)
        ) {
            ApplicationState.selectedPadId = Definitions.pad5Id
            setVolumeSliderProgress(ApplicationState.pad5LftVolume, ApplicationState.pad5RftVolume)
        }
        return false
    }

    private fun setPad6Selected(): Boolean {
        if ((Definitions.pad6Id != ApplicationState.selectedPadId) ||
            (ApplicationState.selectedPadId == null)
        ) {
            ApplicationState.selectedPadId = Definitions.pad6Id
            setVolumeSliderProgress(ApplicationState.pad6LftVolume, ApplicationState.pad6RftVolume)
        }
        return false
    }

    private fun setPad7Selected(): Boolean {
        if ((Definitions.pad7Id != ApplicationState.selectedPadId) ||
            (ApplicationState.selectedPadId == null)
        ) {
            ApplicationState.selectedPadId = Definitions.pad7Id
            setVolumeSliderProgress(ApplicationState.pad7LftVolume, ApplicationState.pad7RftVolume)
        }
        return false
    }

    private fun setPad8Selected(): Boolean {
        if ((Definitions.pad8Id != ApplicationState.selectedPadId) ||
            (ApplicationState.selectedPadId == null)
        ) {
            ApplicationState.selectedPadId = Definitions.pad8Id
            setVolumeSliderProgress(ApplicationState.pad8LftVolume, ApplicationState.pad8RftVolume)
        }
        return false
    }

    private fun setPad9Selected(): Boolean {
        if ((Definitions.pad9Id != ApplicationState.selectedPadId) ||
            (ApplicationState.selectedPadId == null)
        ) {
            ApplicationState.selectedPadId = Definitions.pad9Id
            setVolumeSliderProgress(ApplicationState.pad9LftVolume, ApplicationState.pad9RftVolume)
        }
        return false
    }

    private fun setPad10Selected(): Boolean {
        if ((Definitions.pad10Id != ApplicationState.selectedPadId) ||
            (ApplicationState.selectedPadId == null)
        ) {
            ApplicationState.selectedPadId = Definitions.pad10Id
            setVolumeSliderProgress(
                ApplicationState.pad10LftVolume,
                ApplicationState.pad10RftVolume
            )
        }
        return false
    }


    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.settings_btn -> loadSound()
            R.id.settings_btn -> settings()
        }

    }


    private fun setVolumeSliderProgress(leftV: Float, rightV: Float) {

        val progress: Int
        if (leftV == rightV) {
            progress = (leftV * 100).toInt()

            volumeSeekBar.progress = progress
        }

    }


    fun pad1(): Boolean {
        //test()
        // soundPool!!.play(sound1, pad1VolumeLeft, pad1VolumeRight, 0, 0, 1.0f)

        return true
    }

    fun pad2(): Boolean {
        //Log.d(" getVolumeRight", getVolumeRight().toString())
        // soundPool!!.play(sound2, getVolumeRight(), getVolumeRight(), 0, 0, 1.0f)
        // soundPool.startSound(sound, volume)
        return true
    }

    fun pad3(): Boolean {
        //soundPool!!.play(sound3, 1.0f, 1.0f, 0, 0, 1.0f)
        return false
    }

    fun pad4(): Boolean {
        //soundPool!!.play(sound4, 1.0f, 1.0f, 0, 0, 1.0f)
        return false
        return false
    }

    fun pad5(): Boolean {
        // soundPool!!.play(sound5, 1.0f, 1.0f, 0, 0, 1.0f)
        return false
    }

    fun pad6(): Boolean {
        // soundPool!!.play(sound6, 1.0f, 1.0f, 0, 0, 1.0f)
        return false
    }

    fun pad7(): Boolean {
        // soundPool!!.play(sound7, 1.0f, 1.0f, 0, 0, 1.0f)
        return false
    }

    fun pad8(): Boolean {
        //  soundPool!!.play(sound8, 1.0f, 1.0f, 0, 0, 1.0f)
        return false
    }

    fun pad9(): Boolean {
        //  soundPool!!.play(sound9, 1.0f, 1.0f, 0, 0, 1.0f)
        return false
    }

    fun pad10(): Boolean {
        //  soundPool!!.play(sound10, 1.0f, 1.0f, 0, 0, 10f)
        return false
    }

    fun pad11(): Boolean {
        // soundPool!!.play(sound11, 1.0f, 1.0f, 0, 0, 1.0f)
        return false
    }

    fun pad12(): Boolean {
        // soundPool!!.play(sound12, 1.0f, 1.0f, 0, 0, 1.0f)
        return false
    }


    // UI menu buttons

    private fun loadSound() {
        val intent = Intent(activity!!.applicationContext, LoadDrumSoundDialogActivity::class.java)
        startActivityForResult(intent, LOAD_REQUEST_CODE)
    }

    private fun settings() {
        val intent = Intent(activity!!.applicationContext, SettingsDialogActivity::class.java)
        startActivityForResult(intent, SETTINGS_REQUEST_CODE)
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

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

        val volume = progress.toFloat() / 100
        setPadVolume(volume)

    }

    private fun setPadVolume(volume: Float) {

        if (ApplicationState.selectedPadId == Definitions.pad1Id) {
            ApplicationState.pad1LftVolume = volume
            ApplicationState.pad1RftVolume = volume
        }
        if (ApplicationState.selectedPadId == Definitions.pad2Id) {
            ApplicationState.pad2LftVolume = volume
            ApplicationState.pad2RftVolume = volume
        }
        if (ApplicationState.selectedPadId == Definitions.pad3Id) {
            ApplicationState.pad3LftVolume = volume
            ApplicationState.pad3RftVolume = volume
        }
        if (ApplicationState.selectedPadId == Definitions.pad4Id) {
            ApplicationState.pad4LftVolume = volume
            ApplicationState.pad4RftVolume = volume
        }
        if (ApplicationState.selectedPadId == Definitions.pad5Id) {
            ApplicationState.pad5LftVolume = volume
            ApplicationState.pad5RftVolume = volume
        }
        if (ApplicationState.selectedPadId == Definitions.pad6Id) {
            ApplicationState.pad6LftVolume = volume
            ApplicationState.pad6RftVolume = volume
        }
        if (ApplicationState.selectedPadId == Definitions.pad7Id) {
            ApplicationState.pad7LftVolume = volume
            ApplicationState.pad7RftVolume = volume
        }
        if (ApplicationState.selectedPadId == Definitions.pad8Id) {
            ApplicationState.pad8LftVolume = volume
            ApplicationState.pad8RftVolume = volume
        }
        if (ApplicationState.selectedPadId == Definitions.pad9Id) {
            ApplicationState.pad9LftVolume = volume
            ApplicationState.pad9RftVolume = volume
        }
        if (ApplicationState.selectedPadId == Definitions.pad10Id) {
            ApplicationState.pad10LftVolume = volume
            ApplicationState.pad10RftVolume = volume
        }

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {


    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        Log.d(" onProgressChanged", pad1VolumeLeft.toString())


    }


}