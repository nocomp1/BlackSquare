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
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.blacksquare.LoadDialogActivity
import com.example.blacksquare.Managers.DrumPadPlayBack
import com.example.blacksquare.Managers.DrumPadSoundPool
import com.example.blacksquare.Managers.SoundResManager
import com.example.blacksquare.Models.PadClickListenerModel
import com.example.blacksquare.Objects.PadSequenceTimeStamp
import com.example.blacksquare.Objects.ShowPadPlaying
import com.example.blacksquare.R
import com.example.blacksquare.SettingsDialogActivity
import com.example.blacksquare.Singleton.ApplicationState
import com.example.blacksquare.Singleton.Bpm
import com.example.blacksquare.Singleton.Definitions
import com.example.blacksquare.ViewModels.MainViewModel
import com.example.blacksquare.ViewModels.SoundsViewModel
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drum_bank1.*
import kotlinx.android.synthetic.main.drum_bank1.view.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class DrumScreenHomeFragment : BaseFragment(), View.OnClickListener {
    private lateinit var noteRepeatEngineExecutor: ScheduledExecutorService
    lateinit var sharedPref: SharedPreferences
    private lateinit var soundPool: DrumPadSoundPool
    private lateinit var pads: Array<PadClickListenerModel>
    private var LOAD_REQUEST_CODE: Int = 100
    private var SETTINGS_REQUEST_CODE: Int = 200
    private val padList = mutableListOf<PadClickListenerModel>()
    // private val padSequenceList = arrayOf<ArrayList<PadSequenceTimeStamp>>()
    private lateinit var volumeSeekBar: SeekBar
    private lateinit var sharedViewModel: MainViewModel
    private lateinit var mainViewModel: MainViewModel

    private lateinit var drumPadPlayback: DrumPadPlayBack
    //List of pad time stamps
    var pad1HitMap = ArrayMap<Long, PadSequenceTimeStamp>()
    val pad2HitMap = ArrayMap<Long, PadSequenceTimeStamp>()
    val pad3HitMap = ArrayMap<Long, PadSequenceTimeStamp>()
    val pad4HitMap = ArrayMap<Long, PadSequenceTimeStamp>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pager: ViewPager? = viewPager
        pager?.offscreenPageLimit = 5

        activity?.let {
            sharedViewModel = ViewModelProviders.of(it).get(MainViewModel::class.java)
        }

        sharedViewModel.mainSliderValue.observe(this, Observer {
            it?.let { progress ->
                val volume = progress.toFloat() / 100
                setPadVolume(volume)

                Log.d("padVolumeValue", "pad volume value= $progress")

            }
        })

            sharedViewModel.playbackPadId.observe(this, Observer {pad ->
                pad?.let {
                    padPlayBackchangePadState(it)
                    Log.d("pad1playback", "pad idroy= $it")

                }
            })

        }





    override fun onStart() {
        super.onStart()
        //sharedPref = activity!!.applicationContext.getSharedPreferences("drumscreen", Context.MODE_PRIVATE) ?: return
        //

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Setting up View model to communicate to our fragments











//
//        this.let {
//
//            mainActivityViewModel = ViewModelProviders.of(it).get(MainActivityViewModel::class.java)
//
//        }
        //sharedPref = activity!!.applicationContext.getSharedPreferences("drumscreen", Context.MODE_PRIVATE) ?: return
        //sharedPref = activity!!.getPreferences(Context.MODE_PRIVATE) ?: return

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

        initPadTimeStampArrayList()



//        ApplicationState.padHitUndoSequenceList!!.add(padArrayMapArrayList)
//        ApplicationState.padHitUndoSequenceList!!.add(padArrayMapArrayList)
//        ApplicationState.padHitUndoSequenceList!!.add(padArrayMapArrayList)
//        ApplicationState.padHitUndoSequenceList!!.add(padArrayMapArrayList)


        ///Set up our live data observers
       // setUpLiveDataToObserve()


    }

    fun initPadTimeStampArrayList() {
       Log.d("drumFrag","it called it")
        //Array map that holds timestamps for each pad hit
        padTimeStampArrayMapList.add(pad1HitMap)
        padTimeStampArrayMapList.add(pad2HitMap)
        padTimeStampArrayMapList.add(pad3HitMap)
        padTimeStampArrayMapList.add(pad4HitMap)

        //Array list that holds the array map timestamps for each pad
        ApplicationState.padHitSequenceArrayList!!.add(Definitions.pad1Index, pad1HitMap)
        ApplicationState.padHitSequenceArrayList!!.add(Definitions.pad2Index, pad2HitMap)
        ApplicationState.padHitSequenceArrayList!!.add(Definitions.pad3Index, pad3HitMap)
        ApplicationState.padHitSequenceArrayList!!.add(Definitions.pad4Index, pad4HitMap)

    }

    private fun setUpLiveDataToObserve() {
        //observe what pad needs to change state during playback
//        activity?.let {
//            val mainActivityViewModel = ViewModelProviders.of(it).get(MainActivityViewModel::class.java)
//
//            mainActivityViewModel.playbackPadId.observe(this, Observer {pad ->
//                pad?.let {
//                    padPlayBackchangePadState(it)
//                    Log.d("pad1playback", "pad idroy= $it")
//
//                }
//            })
//        }



//
//        //observe the main slider/volume/pan/pitch ..ect value
//        activity?.let {
//            val sharedViewModel = ViewModelProviders.of(it).get(SoundsViewModel::class.java)
//
//            sharedViewModel.mainSliderValue.observe(this, Observer {
//                it?.let { progress ->
//
//                    val volume = progress.toFloat() / 100
//                    setPadVolume(volume)
//
//                    Log.d("padVolumeValue", "pad volume value= $progress")
//
//                }
//            })
//
//
//            sharedViewModel.playbackPadId.observe(this, Observer {pad ->
//                pad?.let {
//                    padPlayBackchangePadState(it)
//                    Log.d("pad1playback", "pad idroy= $it")
//
//                }
//            })
//
//        }


    }

    private fun loadAsoundKit(soundKitFilesIds: Map<Int, Int>) {
        soundPool.loadSoundKit(soundKitFilesIds)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View =
            inflater.inflate(R.layout.drum_screen_home_fragment_layout, container, false)

        sharedPref = activity!!.getPreferences(Context.MODE_PRIVATE)

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
            var noteRepeatEngine: Disposable? = null

            val action: Runnable = Runnable {

               // Log.d("NREPEAT", "NOTE REPEAT Action down")
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
                                (Bpm.getNoteRepeatInterval(ApplicationState.selectedNoteRepeat))!!.times(1000)
                            Log.d("NREPEAT", "interval = $noteRepeatInterval")
                            //manually change pad state
                            pad.isPressed = true
                            pad.invalidate()
                            noteRepeatEngineExecutor = Executors.newScheduledThreadPool(1)
                            noteRepeatEngineExecutor.scheduleAtFixedRate(action, 0, noteRepeatInterval!!, TimeUnit.MICROSECONDS)


//                            noteRepeatEngine =
//                                Observable.interval(noteRepeatInterval, TimeUnit.NANOSECONDS)
//                                    .subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe {
//                                        Log.d("NREPEAT", "NOTE REPEAT Action down")
//
//                                        handlePadEvent(
//                                            padIndex,
//                                            padId
//                                        )
//                                    }


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
                        }else{


                            Log.d("NREPEAT", "Defualt handle Action up")

                        }

                    }

                }


                ApplicationState.noteRepeatActive
            }


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

//        if (ApplicationState.noteRepeatHasChanged) {
//            //Disable pad click listener to get an updated interval value
//            disableAllPadClickListeners()
//            //reset all the clickListeners
//            setPadClickListeners()
//
//            //reset the state
//            ApplicationState.noteRepeatHasChanged = false
//        }

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

            val soundPlayTimeStamp = ApplicationState.uiSequenceMillisecCounter
            addTimeStampToList(padIndex, soundId, soundPlayTimeStamp)

            Log.d("soundPlayTimeStamp", "sound play sound stamp = $soundPlayTimeStamp")

        }

    }


    /**
     * The time stamp sequence list is always going to have at least size of
     * 1(dummy data or actual time stamp)
     */
    private fun addTimeStampToList(
        padIndex: Int,
        soundId: Int?,
        soundPlayTimeStamp: Long
    ) {

        val numberOfPads = padTimeStampArrayMapList.size


        //Make sure we have an equal amount of pads inside both array list
        if (ApplicationState.padHitSequenceArrayList!!.size.equals(numberOfPads)) {

            //Loop through and set the array map to the corresponding pad index
            var padHitIndex = 0
            while (padHitIndex < numberOfPads) {

                if (padHitIndex.equals(padIndex)) {

                    //get the pad
                    val pad = padTimeStampArrayMapList[padHitIndex]
                    //log the time stamp for that pad to the array map
                    pad[soundPlayTimeStamp] =
                        PadSequenceTimeStamp(soundId, padIndex, soundPlayTimeStamp)
                    //add the array map to that pad index in the pad array list
                    ApplicationState.padHitSequenceArrayList!![padHitIndex] = pad


                    Log.d(
                        "soundPlayTimeStamp",
                        "number hits for first pad = ${ApplicationState.padHitSequenceArrayList!![padHitIndex].size} "
                    )

                } else {

                    //Get the array map and fill dummy data to keep index inline for pads not triggered
                    val pad = padTimeStampArrayMapList.get(padHitIndex)
                    pad.put(null, PadSequenceTimeStamp(null, padHitIndex, null))

                    ApplicationState.padHitSequenceArrayList!![padHitIndex] = pad
                    Log.d(
                        "timestamp",
                        "dummy data timestamp = ${ApplicationState.padHitSequenceArrayList!![padHitIndex].size} "
                    )

                }

                padHitIndex++


            }
        }
        Log.d(
            "timestamp",
            "number of pads = ${ApplicationState.padHitSequenceArrayList!!.size} "
        )
        ApplicationState.drumNoteHasBeenRecorded = true

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


    fun padPlayBackchangePadState(padId: Int) {
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

        // val  sharedPref = activity!!.getPreferences(Context.MODE_PRIVATE)

        with(sharedPref.edit()) {
            putFloat(padLftVolume, volume)
            putFloat(padRftVolume, volume)
            apply()
        }



        Log.d("padVolumeValue", "${sharedPref!!.getFloat(padLftVolume, 0.0f)}")


    }

    fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    companion object {
        val padTimeStampArrayMapList = arrayListOf<ArrayMap<Long, PadSequenceTimeStamp>>()

    }


}