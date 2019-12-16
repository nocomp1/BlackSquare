package com.example.nextsoundz.Fragments


import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
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
import com.example.nextsoundz.Models.PadClickListenerModel
import com.example.nextsoundz.R
import com.example.nextsoundz.SettingsDialogActivity
import com.example.nextsoundz.Singleton.ApplicationState
import com.example.nextsoundz.Singleton.Bpm
import com.example.nextsoundz.Singleton.Definitions
import com.example.nextsoundz.ViewModels.SoundsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drum_bank1.*
import kotlinx.android.synthetic.main.drum_bank1.view.*


class DrumScreenHomeFragment : BaseFragment(), View.OnClickListener {


    lateinit var sharedPref: SharedPreferences
    private lateinit var soundPool: DrumPadSoundPool
    private lateinit var pads: Array<PadClickListenerModel>
    private var LOAD_REQUEST_CODE: Int = 100
    private var SETTINGS_REQUEST_CODE: Int = 200
    private val padList = mutableListOf<PadClickListenerModel>()
    private lateinit var volumeSeekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pager: ViewPager? = viewPager
        pager?.offscreenPageLimit = 5


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

        //////Sound Pool///////
        val drumPadSoundPool = DrumPadSoundPool(activity!!.applicationContext)
        soundPool = drumPadSoundPool

        //Load Default kit
        loadAsoundKit(SoundResManager.getDefaultKitFilesIds())


        ///Set up our live data observers

        activity?.let {
            val sharedViewModel = ViewModelProviders.of(it).get(SoundsViewModel::class.java)

            ////Observer for SoundPool
            sharedViewModel.drumPadSoundPool.observe(this, Observer {
                it?.let {

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

        val view: View =
            inflater.inflate(R.layout.drum_screen_home_fragment_layout, container, false)


        return view
    }


    //This method creates list of all drum pads
    //to be used to set onclicklisteners
    private fun createPadList() {

        padList.add(PadClickListenerModel(pad1, Definitions.pad1Index, Definitions.pad1Id))
        padList.add(PadClickListenerModel(pad2, Definitions.pad2Index, Definitions.pad2Id))
        padList.add(PadClickListenerModel(pad3, Definitions.pad3Index, Definitions.pad3Id))
        padList.add(PadClickListenerModel(pad4, Definitions.pad4Index, Definitions.pad4Id))
        padList.add(PadClickListenerModel(pad5, Definitions.pad5Index, Definitions.pad5Id))
        padList.add(PadClickListenerModel(pad6, Definitions.pad6Index, Definitions.pad6Id))
        padList.add(PadClickListenerModel(pad7, Definitions.pad7Index, Definitions.pad7Id))
        padList.add(PadClickListenerModel(pad8, Definitions.pad8Index, Definitions.pad8Id))
        padList.add(PadClickListenerModel(pad9, Definitions.pad9Index, Definitions.pad9Id))
        padList.add(PadClickListenerModel(pad10, Definitions.pad10Index, Definitions.pad10Id))

    }

    override fun onPause() {
        super.onPause()


    }

    private fun setPadClickListeners() {

        //Loop through our pad list and set our listeners
        var index = 0
        while (index < padList.size) {

            val pad = padList[index].pad
            val padIndex = padList[index].padIndex
            val padId = padList[index].padId

            pad.setOnTouchListener(
                RepeatListener
                    (0, Bpm.getNoteRepeatInterval(ApplicationState.selectedNoteRepeat),
                    View.OnClickListener {

                        handlePadEvent(
                            padIndex,
                            padId
                        )

                    })
            )

            index++

        }

    }

    private fun disableAllPadClickListeners() {

        view!!.pad1.setOnTouchListener(null)
        view!!.pad2.setOnTouchListener(null)
        view!!.pad3.setOnTouchListener(null)
        view!!.pad4.setOnTouchListener(null)
        view!!.pad5.setOnTouchListener(null)
        view!!.pad6.setOnTouchListener(null)
        view!!.pad7.setOnTouchListener(null)
        view!!.pad8.setOnTouchListener(null)
        view!!.pad9.setOnTouchListener(null)
        view!!.pad10.setOnTouchListener(null)
    }

    override fun onResume() {
        super.onResume()

        if (ApplicationState.drumScreenInitialLoad == -1) {

            createPadList()
            setPadClickListeners()

            ApplicationState.drumScreenInitialLoad = 1
        }

        if (ApplicationState.tempoHasChanged) {
            //Disable pad click listener to get an updated interval value
            disableAllPadClickListeners()
            //reset all the clickListeners
            setPadClickListeners()

            //reset the state
            ApplicationState.tempoHasChanged = false
        }

        if (ApplicationState.noteRepeatHasChanged) {
            //Disable pad click listener to get an updated interval value
            disableAllPadClickListeners()
            //reset all the clickListeners
            setPadClickListeners()

            //reset the state
            ApplicationState.noteRepeatHasChanged = false
        }

    }

    private fun handlePadEvent(padIndex: Int, padId: Int) {

        var padLftVolume: Float = 0f
        var padRtVolume: Float = 0f

        when (padId) {

            1 -> {
                padLftVolume = ApplicationState.pad1LftVolume
                padRtVolume = ApplicationState.pad1RftVolume
            }
            2 -> {
                padLftVolume = ApplicationState.pad2LftVolume
                padRtVolume = ApplicationState.pad2RftVolume

            }
            3 -> {
                padLftVolume = ApplicationState.pad3LftVolume
                padRtVolume = ApplicationState.pad3RftVolume
            }
            4 -> {
                padLftVolume = ApplicationState.pad4LftVolume
                padRtVolume = ApplicationState.pad4RftVolume
            }
            5 -> {
                padLftVolume = ApplicationState.pad5LftVolume
                padRtVolume = ApplicationState.pad5RftVolume
            }
            6 -> {
                padLftVolume = ApplicationState.pad6LftVolume
                padRtVolume = ApplicationState.pad6RftVolume
            }
            7 -> {
                padLftVolume = ApplicationState.pad7LftVolume
                padRtVolume = ApplicationState.pad7RftVolume
            }
            8 -> {
                padLftVolume = ApplicationState.pad8LftVolume
                padRtVolume = ApplicationState.pad8RftVolume
            }
            9 -> {
                padLftVolume = ApplicationState.pad9LftVolume
                padRtVolume = ApplicationState.pad9RftVolume
            }
            10 -> {
                padLftVolume = ApplicationState.pad10LftVolume
                padRtVolume = ApplicationState.pad10RftVolume
            }
        }


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
        var soundPlayTimeStamp = SystemClock.uptimeMillis()
        Log.d("timestamp", "time we pressed the pad= $soundPlayTimeStamp")
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

    private fun setVolumeSliderProgress(leftV: Float, rightV: Float) {

        val progress: Int
        if (leftV == rightV) {
            progress = (leftV * 100).toInt()

            volumeSeekBar.progress = progress
        }

    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.settings_btn -> loadSoundDialogMenu()
            R.id.settings_btn -> settingsDialogMenu()
        }

    }

    private fun loadSoundDialogMenu() {
        val intent = Intent(activity!!.applicationContext, LoadDrumSoundDialogActivity::class.java)
        startActivityForResult(intent, LOAD_REQUEST_CODE)
    }

    private fun settingsDialogMenu() {
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


    /////////////Main Slider knob for Volume or ect...
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

    }


}