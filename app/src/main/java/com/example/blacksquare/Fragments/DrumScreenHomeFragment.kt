package com.example.blacksquare.Fragments


import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.blacksquare.LoadDialogActivity
import com.example.blacksquare.Managers.DrumPadSoundPool
import com.example.blacksquare.Managers.SoundResManager
import com.example.blacksquare.Models.PadClickListenerModel
import com.example.blacksquare.Objects.PadSequenceTimeStamp
import com.example.blacksquare.Objects.Quantize
import com.example.blacksquare.Objects.ShowPadPlaying
import com.example.blacksquare.R
import com.example.blacksquare.SettingsDialogActivity
import com.example.blacksquare.Singleton.ApplicationState
import com.example.blacksquare.Singleton.Definitions
import com.example.blacksquare.Utils.BpmUtils
import com.example.blacksquare.Utils.SharedPrefKeys.BAR_MEASURE_DEFAULT
import com.example.blacksquare.Utils.SharedPrefKeys.BAR_MEASURE_SELECTED
import com.example.blacksquare.ViewModels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drum_bank1.*
import kotlinx.android.synthetic.main.drum_bank1.view.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class DrumScreenHomeFragment : BaseFragment(), View.OnClickListener,
    MainViewModel.SequenceListener {
    private lateinit var noteRepeatEngineExecutor: ScheduledExecutorService
    lateinit var sharedPref: SharedPreferences
    private lateinit var soundPool: DrumPadSoundPool
    private lateinit var pads: Array<PadClickListenerModel>
    private var LOAD_REQUEST_CODE: Int = 100
    private var SETTINGS_REQUEST_CODE: Int = 200
    private val padList = mutableListOf<PadClickListenerModel>()
    private val numberOfPadsList = arrayListOf(1, 2, 3, 4)
    private var sequenceMilliSecClock = 0L
    private lateinit var pad1Button: Button
    private var drumNoteHasBeenRecorded = false
    private var patternSelected: String? = null
    private  var barMeasure = 1
    private lateinit var volumeSeekBar: SeekBar
    private lateinit var sharedViewModel: MainViewModel

    /**
     *
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                              /
    //                                                                                                              /
    //   Undo List padHitUndoSequenceList List structure is:                                                        /
    //                                                                                                              /
    //   (array list 1)                                                                                             /
    //     (pad index) (array list 2)   (array map)sequences is what we that we can undo back to                    /
    //       pad 1     sequence index   each index has a time stamp that represents the time a user hit a pad       /
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
    //  array list (sequence index) - Then we check the size of our array map which is our time stamps              /
    //  against pad 1 array map size inside of "padHitSequenceArrayList". If the size has increased then            /
    //  we make a copy of it and store it into the Undo List "padHitUndoSequenceList" and increase our pointer by 1 /
    //  to point at the latest sequence. When we want to undo move our pointer to a previous index inside           /
    //  array list 2 (sequence index) then update pad 1 array map inside "padHitSequenceArrayList" to reflect the   /
    //  previous array map                                                                                          /
    //                                                                                                              /
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     *
     */

    /**
     * Arraylist of notes triggered for each pad
     *
     */
    ///////////////////////////////////////|Pads|   |Timestamp index| |object at index|
    private var padsPattern: ArrayList<ArrayMap<Long, PadSequenceTimeStamp>> =
        ArrayList()

    private var currentPattern: ArrayList<ArrayMap<Long, PadSequenceTimeStamp>> =  ArrayList()

    ////////////////////////|patterns| with |pattern Key||Pads| of |Timestamp index| |object at index|
    private var sequencePatternList =
        ArrayMap<String, ArrayList<ArrayMap<Long, PadSequenceTimeStamp>>>()

    private var padHitUndoSequenceList: ArrayList<ArrayList<ArrayMap<Long, PadSequenceTimeStamp>>> =
        ArrayList()
    private var padTimeStampArrayMapList = arrayListOf<ArrayMap<Long, PadSequenceTimeStamp>>()

    //List of pad time stamps
    private lateinit var pad1HitMap: ArrayMap<Long, PadSequenceTimeStamp>
    private lateinit var pad2HitMap: ArrayMap<Long, PadSequenceTimeStamp>
    private lateinit var pad3HitMap: ArrayMap<Long, PadSequenceTimeStamp>
    private lateinit var pad4HitMap: ArrayMap<Long, PadSequenceTimeStamp>

    private var isQuantizeEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pager: ViewPager? = viewPager
        pager?.offscreenPageLimit = 5

        sharedPref = activity!!.getSharedPreferences(
            getString(R.string.application_shared_prefs),
            Context.MODE_PRIVATE
        )


        patternSelected = sharedPref.getString(getString(R.string.shared_prefs_pattern_selected),getString(R.string.p1))

        activity?.let {
            sharedViewModel = ViewModelProviders.of(it).get(MainViewModel::class.java)
        }



        sharedViewModel.sharedViewState.observe(this, Observer { sharedViewState ->

            sharedViewState?.let { sharedViewState ->
                //observe the patter user selects
                onPatternChanged(getString(sharedViewState.patternSelected))

                //main slider value
                val volume = sharedViewState.mainSliderValue.toFloat() / 100
                setPadVolume(volume)

                //Drum pad id for playback
                val padId = sharedViewState.playBackPadId
                padPlayBackchangePadState(padId)

            }


        })

        sharedViewModel.setSeqListener(this)

       // onPatternChanged(patternSelected!!)


        initPadTimeStampArrayList()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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



        //Set the default pattern selection
        //onPatternChanged(patternSelected!!)
        //addPatternToListForPlayback()

    }

    private fun initPadTimeStampArrayList() {
        Log.d("drumFrag", "it called it")
       // currentPattern = ArrayList()
        padsPattern = ArrayList()
        padTimeStampArrayMapList = arrayListOf()

        pad1HitMap = ArrayMap<Long, PadSequenceTimeStamp>()
        pad2HitMap = ArrayMap<Long, PadSequenceTimeStamp>()
        pad3HitMap = ArrayMap<Long, PadSequenceTimeStamp>()
        pad4HitMap = ArrayMap<Long, PadSequenceTimeStamp>()

        padTimeStampArrayMapList.also { list ->
            //Array map that holds timestamps for each pad hit
            list.add(pad1HitMap)
            list.add(pad2HitMap)
            list.add(pad3HitMap)
            list.add(pad4HitMap)
        }

        //Array list that holds the array map timestamps for each pad
        padsPattern.also { list ->
            list.add(Definitions.pad1Index, pad1HitMap)
            list.add(Definitions.pad2Index, pad2HitMap)
            list.add(Definitions.pad3Index, pad3HitMap)
            list.add(Definitions.pad4Index, pad4HitMap)
        }

    }

    private fun loadAsoundKit(soundKitFilesIds: Map<Int, Int>) {
        soundPool.loadSoundKit(soundKitFilesIds)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pad1Button = activity!!.findViewById(R.id.pad1)

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
        padList.map { index ->

            val pad = index.pad
            val padIndex = index.padIndex
            val padId = index.padId
            val action: Runnable = Runnable {
                handlePadEvent(
                    padIndex,
                    padId
                )

            }

            pad.setOnTouchListener { v, m ->

                // Perform tasks here
                when (m.action) {

                    MotionEvent.ACTION_DOWN -> {
                        // noteRepeatEngineExecutor = Executors.newScheduledThreadPool(1)
                        if (ApplicationState.noteRepeatActive) {

                            val noteRepeatInterval =
                                (BpmUtils.getNoteRepeatInterval(ApplicationState.selectedNoteRepeat))!!.times(
                                    1000
                                )
                            Log.d("NREPEAT", "interval = $noteRepeatInterval")
                            //manually change pad state
                            pad.isPressed = true
                            pad.invalidate()
                            noteRepeatEngineExecutor = Executors.newScheduledThreadPool(1)
                            noteRepeatEngineExecutor.scheduleAtFixedRate(
                                action,
                                0,
                                noteRepeatInterval!!,
                                TimeUnit.MICROSECONDS
                            )


                        } else {


                            Log.d("NREPEAT", "Defualt handle Action down")

                            handlePadEvent(
                                padIndex,
                                padId
                            )

                        }

                    }

                    MotionEvent.ACTION_UP -> {

                        if (ApplicationState.noteRepeatActive) {
                            Log.d("NREPEAT", "NOTE REPEAT Action UP")
                            //manually change pad state
                            pad.isPressed = false
                            pad.invalidate()
                            //stop the repeat engine
                            // noteRepeatEngine!!.dispose()
                            noteRepeatEngineExecutor.shutdownNow()
                            // Thread.currentThread().interrupt()
                        } else {


                            Log.d("NREPEAT", "Defualt handle Action up")

                        }

                    }

                }


                ApplicationState.noteRepeatActive
            }


        }


    }

    private fun disableAllPadClickListeners() {
        view?.let { view ->
            view.pad1.setOnTouchListener(null)
            view.pad2.setOnTouchListener(null)
            view.pad3.setOnTouchListener(null)
            view.pad4.setOnTouchListener(null)
            view.pad5.setOnTouchListener(null)
            view.pad6.setOnTouchListener(null)
            view.pad7.setOnTouchListener(null)
            view.pad8.setOnTouchListener(null)
            view.pad9.setOnTouchListener(null)
            view.pad10.setOnTouchListener(null)
        }
    }

    override fun onResume() {
        super.onResume()

        barMeasure = sharedPref.getInt( getString(BAR_MEASURE_SELECTED),getString(
            BAR_MEASURE_DEFAULT).toInt())





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

    }

    private fun handlePadEvent(padIndex: Int, padId: Int) {

        var padLftVolume = 0f
        var padRtVolume = 0f

        val volumeDefaultValue = Definitions.padVolumeDefault

        when (padId) {

            1 -> {
                padLftVolume =
                    sharedPref.getFloat(Definitions.pad1LftVolume, volumeDefaultValue)
                padRtVolume = sharedPref.getFloat(Definitions.pad1RftVolume, volumeDefaultValue)
            }
            2 -> {
                padLftVolume =
                    sharedPref.getFloat(Definitions.pad2LftVolume, volumeDefaultValue)
                padRtVolume = sharedPref.getFloat(Definitions.pad2RftVolume, volumeDefaultValue)

            }
            3 -> {
                padLftVolume =
                    sharedPref.getFloat(Definitions.pad3LftVolume, volumeDefaultValue)
                padRtVolume = sharedPref.getFloat(Definitions.pad3RftVolume, volumeDefaultValue)
            }
            4 -> {
                padLftVolume =
                    sharedPref.getFloat(Definitions.pad4LftVolume, volumeDefaultValue)
                padRtVolume = sharedPref.getFloat(Definitions.pad4RftVolume, volumeDefaultValue)
            }
            5 -> {
                padLftVolume =
                    sharedPref.getFloat(Definitions.pad5LftVolume, volumeDefaultValue)
                padRtVolume = sharedPref.getFloat(Definitions.pad5RftVolume, volumeDefaultValue)
            }
            6 -> {
                padLftVolume =
                    sharedPref.getFloat(Definitions.pad6LftVolume, volumeDefaultValue)
                padRtVolume = sharedPref.getFloat(Definitions.pad6RftVolume, volumeDefaultValue)
            }
            7 -> {
                padLftVolume =
                    sharedPref.getFloat(Definitions.pad7LftVolume, volumeDefaultValue)
                padRtVolume = sharedPref.getFloat(Definitions.pad7RftVolume, volumeDefaultValue)
            }
            8 -> {
                padLftVolume =
                    sharedPref.getFloat(Definitions.pad8LftVolume, volumeDefaultValue)
                padRtVolume = sharedPref.getFloat(Definitions.pad8RftVolume, volumeDefaultValue)
            }
            9 -> {
                padLftVolume =
                    sharedPref.getFloat(Definitions.pad9LftVolume, volumeDefaultValue)
                padRtVolume = sharedPref.getFloat(Definitions.pad9RftVolume, volumeDefaultValue)
            }
            10 -> {
                padLftVolume =
                    sharedPref.getFloat(Definitions.pad10LftVolume, volumeDefaultValue)
                padRtVolume =
                    sharedPref.getFloat(Definitions.pad10RftVolume, volumeDefaultValue)
            }
        }

        Log.d("prefsVolume", " $padLftVolume")

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
                    padRtVolume,
                    padIndex
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
                    padRtVolume,
                    padId
                )
            }

        }

        setPadSelected(padId, padLftVolume, padRtVolume)

    }

    private fun playSound(
        soundId: Int?,
        padLftVolume: Float,
        padRftVolume: Float,
        padIndex: Int
    ) {

        soundPool.startSound(
            soundId,
            padLftVolume,
            padRftVolume
        )

        //If we are recording log timestamp of note
        if ((ApplicationState.isRecording) && (ApplicationState.isPlaying)) {

            val hitTimeStamp: Long
            //TODO IF NOTE REPEAT IS ENABLED DISABLE THE QUANTIZE

            val isQuantizeEnabled = sharedPref.getBoolean(
                getString(R.string.shared_prefs_quantize_checkbox_selected),
                true
            )


            if (isQuantizeEnabled) {

                val soundPlayTimeStamp = sequenceMilliSecClock
                hitTimeStamp = BpmUtils.quantizeNote(getQuantizePreference(), soundPlayTimeStamp,barMeasure)
                Log.d("calculateQuantize", " quantizedTimeStamp= ${hitTimeStamp}")


            } else {

                hitTimeStamp = sequenceMilliSecClock
            }
            addTimeStampToList(padIndex, soundId, hitTimeStamp, padLftVolume, padRftVolume)

            drumNoteHasBeenRecorded = true

            Log.d("soundPlayTimeStamp", "sound play sound stamp = $hitTimeStamp")

        }

    }

    private fun getQuantizePreference(): Quantize {

        //Get the quantize selection from sharedPrefs
        val quantizePrefs = sharedPref.getString(
            getString(R.string.shared_prefs_quantize_selected),
            getString(R.string.one_sixtenth_quantize)
        )

        when (quantizePrefs) {

            getString(R.string.one_eight_quantize) -> return Quantize.EightNote
            getString(R.string.one_sixtenth_quantize) -> return Quantize.SixTenthNote
            getString(R.string.one_thirty_two_quantize) -> return Quantize.ThirtyTwoNote
            getString(R.string.sixtenth_triplet_quantize) -> return Quantize.SixteenthTriplet
            getString(R.string.dotted_quantize) -> return Quantize.Dotted

        }

        return Quantize.SixTenthNote
    }


    /**
     * The time stamp sequence list is always going to have at least size of
     * 1(dummy data or actual time stamp) -
     * This records user hit each note of the pad timestamp
     */
    private fun addTimeStampToList(
        padIndex: Int,
        soundId: Int?,
        soundPlayTimeStamp: Long,
        padLftVolume: Float,
        padRftVolume: Float
    ) {

        val numberOfPads = padTimeStampArrayMapList.size


        //Make sure we have an equal amount of pads inside both array list
        if (padsPattern.size.equals(numberOfPads)) {

            //Loop through and set the array map to the corresponding pad index
            var padHitIndex = 0
            while (padHitIndex < numberOfPads) {

                if (padHitIndex.equals(padIndex)) {

                    //get the pad
                    val pad = padTimeStampArrayMapList[padHitIndex]
                    //log the time stamp for that pad to the array map
                    pad[soundPlayTimeStamp] =
                        PadSequenceTimeStamp(
                            soundId,
                            padIndex,
                            soundPlayTimeStamp,
                            padLftVolume,
                            padRftVolume
                        )
                    //add the array map to that pad index in the pad array list
                    padsPattern[padHitIndex] = pad


                    Log.d(
                        "soundPlayTimeStamp",
                        "number hits for pad $padHitIndex = ${padsPattern!![padHitIndex].size} "
                    )

                } else {

                    //Get the array map and fill dummy data to keep index inline for pads not triggered
                    val pad = padTimeStampArrayMapList.get(padHitIndex)
                    pad.put(
                        null, PadSequenceTimeStamp(
                            null,
                            padHitIndex,
                            null,
                            padLftVolume,
                            padRftVolume
                        )
                    )

                    padsPattern[padHitIndex] = pad

                    Log.d(
                        "timestamp",
                        "dummy data timestamp = ${padsPattern!![padHitIndex].size} "
                    )

                }

                padHitIndex++


            }
        }
        Log.d(
            "timestamp",
            "number of pads = ${padsPattern!!.size} "
        )
        ApplicationState.drumNoteHasBeenRecorded = true

        //after adding the note to our sequence list we add it to our
        //pattern list
        addPatternToListForPlayback(padsPattern)
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


    private fun padPlayBackchangePadState(padId: Int) {
        when (padId) {
            Definitions.pad1Index -> {
                val showPadPlaying = ShowPadPlaying()
                showPadPlaying.showPadPlaying(pad1)
            }
            Definitions.pad2Index -> {
                val showPadPlaying = ShowPadPlaying()
                showPadPlaying.showPadPlaying(pad2)
            }
            Definitions.pad3Index -> {
                val showPadPlaying = ShowPadPlaying()
                showPadPlaying.showPadPlaying(pad3)
            }
            Definitions.pad4Index -> {
                val showPadPlaying = ShowPadPlaying()
                showPadPlaying.showPadPlaying(pad4)
            }
            Definitions.pad5Index -> {
                val showPadPlaying = ShowPadPlaying()
                showPadPlaying.showPadPlaying(pad5)
            }
            Definitions.pad6Index -> {
                val showPadPlaying = ShowPadPlaying()
                showPadPlaying.showPadPlaying(pad6)
            }
            Definitions.pad7Index -> {
                val showPadPlaying = ShowPadPlaying()
                showPadPlaying.showPadPlaying(pad7)
            }
            Definitions.pad8Index -> {
                val showPadPlaying = ShowPadPlaying()
                showPadPlaying.showPadPlaying(pad8)
            }
            Definitions.pad9Index -> {
                val showPadPlaying = ShowPadPlaying()
                showPadPlaying.showPadPlaying(pad9)
            }
            Definitions.pad10Index -> {
                val showPadPlaying = ShowPadPlaying()
                showPadPlaying.showPadPlaying(pad10)
            }
        }
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
        val intent =
            Intent(activity!!.applicationContext, LoadDialogActivity::class.java)
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


    private fun setPadVolume(volume: Float) {

        if (ApplicationState.selectedPadId == Definitions.pad1Id) {
            setPadVolumePrefs(Definitions.pad1LftVolume, Definitions.pad1RftVolume, volume)
        }
        if (ApplicationState.selectedPadId == Definitions.pad2Id) {
            setPadVolumePrefs(Definitions.pad2LftVolume, Definitions.pad2RftVolume, volume)
        }
        if (ApplicationState.selectedPadId == Definitions.pad3Id) {
            setPadVolumePrefs(Definitions.pad3LftVolume, Definitions.pad3RftVolume, volume)
        }
        if (ApplicationState.selectedPadId == Definitions.pad4Id) {
            setPadVolumePrefs(Definitions.pad4LftVolume, Definitions.pad4RftVolume, volume)

        }
        if (ApplicationState.selectedPadId == Definitions.pad5Id) {
            setPadVolumePrefs(Definitions.pad5LftVolume, Definitions.pad5RftVolume, volume)

        }
        if (ApplicationState.selectedPadId == Definitions.pad6Id) {
            setPadVolumePrefs(Definitions.pad6LftVolume, Definitions.pad6RftVolume, volume)

        }
        if (ApplicationState.selectedPadId == Definitions.pad7Id) {
            setPadVolumePrefs(Definitions.pad7LftVolume, Definitions.pad7RftVolume, volume)

        }
        if (ApplicationState.selectedPadId == Definitions.pad8Id) {
            setPadVolumePrefs(Definitions.pad8LftVolume, Definitions.pad8RftVolume, volume)

        }
        if (ApplicationState.selectedPadId == Definitions.pad9Id) {
            setPadVolumePrefs(Definitions.pad9LftVolume, Definitions.pad9RftVolume, volume)

        }
        if (ApplicationState.selectedPadId == Definitions.pad10Id) {
            setPadVolumePrefs(Definitions.pad10LftVolume, Definitions.pad10RftVolume, volume)

        }

    }

    private fun setPadVolumePrefs(
        padLftVolume: String,
        padRftVolume: String,
        volume: Float
    ) {

        with(sharedPref.edit()) {
            putFloat(padLftVolume, volume)
            putFloat(padRftVolume, volume)
            apply()
        }



        Log.d("padVolumeValue", "${sharedPref!!.getFloat(padLftVolume, 0.0f)}")


    }

    private fun onPatternChanged(patternSelected: String) {
        this.patternSelected = patternSelected

        //already have a pattern
        if (sequencePatternList.get(patternSelected) != null) {

            //get the pattern
            padsPattern = sequencePatternList[patternSelected]!!

        } else {

            //no pattern then start fresh
            initPadTimeStampArrayList()

        }

    }

    private fun addPatternToListForPlayback(padsPattern: ArrayList<ArrayMap<Long, PadSequenceTimeStamp>>) {
        Log.d("patternBug", "seqPatternSizeB4=  ${sequencePatternList.size}")
        Log.d("patternBug", "patternSelected=  ${patternSelected}")
       // Log.d("patternBug", "padsPatternSize=  ${padsPattern.size}")
        padsPattern.forEachIndexed { index, arrayMap ->

            Log.d("patternBug", "pad ${index+1}=  ${arrayMap.size} HitPadTimeStamp")

        }
        //Add to Pattern map <key,value> pairs
        sequencePatternList.put(patternSelected, padsPattern)
Log.d("patternBug", "seqPatternSize=  ${sequencePatternList.size}")


        //post to our shared view model list
        sharedViewModel.drumPatternArrayList.postValue(sequencePatternList)

    }


    //This is call per milli second for the length of the sequence
    //then restarts over-
    //This is where we PlayBack our recorded pattern
    override fun updateSeqPerMilliSec(sequenceMilliSecClock: Long) {
        this.sequenceMilliSecClock = sequenceMilliSecClock

        patternSelected?.let { patternKey ->
            if (sequencePatternList.isNotEmpty()) {
                //if we have one /always checking to get current pattern
                // to be played from patternList
                sequencePatternList.get(patternKey)?.let { pattern ->
                    currentPattern = pattern

                    //playback our recorded sounds
                    numberOfPadsList.forEachIndexed { pad, element ->

                        //if (padHitSequenceArrayList.size !=0)
                        currentPattern.let { list ->

                            if (list[pad].contains(sequenceMilliSecClock)) {

                                soundPool.startSound(
                                    list[pad][sequenceMilliSecClock]!!.soundId,
                                    list[pad][sequenceMilliSecClock]!!.padLftVolume,
                                    list[pad][sequenceMilliSecClock]!!.padRftVolume
                                )

                                //show pad being played
                                sharedViewModel.playbackPadId.postValue(list[pad][sequenceMilliSecClock]!!.padId)
                            }
                            //create a undoList - call outside of
                            //condition because we need the sequenceMilliSecClick
                            //to update every milliSec
                            storeUndoList(sequenceMilliSecClock)
                        }

                    }
                }
            }
        }
    }

    private fun storeUndoList(sequenceMilliSecClock: Long) {

        // if a note has been recorded
        // store array for undo action each time sequence loop
        // and only if there is a new hit to the array
        if ((drumNoteHasBeenRecorded)) {
            // Log.d("undoff", "inside not has been recorded")

            //if the undo list is empty add arraylist of array maps to it
            if ((padHitUndoSequenceList!!.isEmpty())) {

                //loop through all pads
                var padIndexCounter = 0

                while (padIndexCounter < padsPattern!!.size) {

                    // Log.d("undoff", "inside undo empty loop")

                    //copy each pad sequence from sequence array list and add it to the undo list
                    val arrayMapCopy =
                        ArrayMap(padsPattern[padIndexCounter])
                    val arrayListcopy = arrayListOf(arrayMapCopy)

                    //Add the array list of array maps to the undo list
                    padHitUndoSequenceList.add(arrayListcopy)

                    //move to the next pad index to check
                    padIndexCounter++
                }


            } else {

                //1. check what pads has new pattern by seeing if size has changed
                //2. compare against the last added index in the undo array map pattern size
                //3. loop and add entire (every index) new array map patterns to the undo list
                //4. Update the global application drum pad undo list with local copy

                // Log.d("undoff", "seqClock= $sequenceMilliSecClock")

                // our sequence loop in milliseconds
                if ((sequenceMilliSecClock == BpmUtils.getSequenceTimeInMilliSecs(barMeasure)) || (!ApplicationState.isPlaying)) {

                    //Log.d("undoff", "inside seq iteration=")

                    //loop through all pads
                    var padIndexCounter1 = 0
                    while (padIndexCounter1 < padsPattern!!.size) {

                        //check what pads has new pattern
                        if (padsPattern[padIndexCounter1].size >
                            padHitUndoSequenceList[padIndexCounter1].last().size
                        ) {

                            //if we have changes add the map to the array list
                            //loop through all pads and add for every pad
                            var padIndexCounter2 = 0

                            while (padIndexCounter2 < padsPattern!!.size) {

                                //Log.d("undoff", "inside adding to all pads")

                                //add the array map to the already added array list of array maps
                                val arrayMapCopy =
                                    padsPattern[padIndexCounter2]

                                padHitUndoSequenceList[padIndexCounter2].add(arrayMapCopy)

                                //Log.d("undoff", "undo list size after the add all 4 should be same${padHitUndoSequenceList!![padIndexCounter2].size}")

                                padIndexCounter2++
                            }

                        }

                        padIndexCounter1++
                    }

                }

            }

        }

    }

    override fun onUndoTapped() {
        if (padHitUndoSequenceList!!.size != 0) {

            sharedViewModel.onAction(MainViewModel.Action.OnShowUndoConfirmMsg)

        } else {

            sharedViewModel.onAction(MainViewModel.Action.OnShowUndoErrorMsg)

        }
    }

    //TODO: UNDO APP CRASH AT LAST UNDO
    override fun onUndoConfirmed() {

        //remove last pattern
        padHitUndoSequenceList.forEach { undoList ->
            undoList.removeAt(undoList.size - 1)
            undoList.trimToSize()

        }

        padHitUndoSequenceList.forEachIndexed { index, arrayList ->
            if (arrayList.isNotEmpty()) {

                val previousPattern = ArrayMap(arrayList.get(arrayList.lastIndex))
                padTimeStampArrayMapList.also { arrayList ->

                    arrayList.set(index, previousPattern)
                    padsPattern[index] = previousPattern
                }
            } else {

                Log.d("undoff", "outside of is not empty")
                initPadTimeStampArrayList()
                sharedViewModel.onAction(MainViewModel.Action.OnShowUndoErrorMsg)

            }

        }

    }


}


