package com.example.blacksquare.Managers

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.blacksquare.R
import com.example.blacksquare.Helpers.ApplicationState

class DrumPadSoundPool(context: Context?) {
    private val soundPool: SoundPool
    private val volume = 1.0f
    private lateinit var loadedSound : Map<String, Int>
    private var metronomeloadedSound : Int? = null
    private lateinit var sounds: Array<Sound>
    private var context: Context?
    private val soundsList = mutableListOf<Sound>()
    var sound: Int = 0
    var metronomeSound: Int = 0
    init {

        val soundFilesIds = 10

        soundPool = SoundPool.Builder()
            .setMaxStreams(soundFilesIds)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .build()

        this.context = context

        // sound = soundPool.load(context, Metronome.getSoundId(), 1)
    }

    fun loadSoundKit(soundFilesIds: Map<Int, Int>) {

        //val sound = soundPool.load(context, Metronome.getSoundId(), 1)

        for (fileID in soundFilesIds) {

            sound = soundPool.load(context, fileID.value, 1)
            soundsList.add(Sound(sound))
        }
        sounds = soundsList.toTypedArray()


        //set the state of the application
        ApplicationState.hasLoadedAKit=true
        ApplicationState.hasLoadedASound = false
    }


    /**
     * Must call one of these load methods first
     */
    fun getLoadedSoundKit(): Array<Sound> {

        if (sounds.isNotEmpty()) {

            return sounds
        } else {

            /// return a defualt sound kit here

        }

        return sounds
    }


    fun loadSound(soundFileId : Int, padSelected : Int){

        sound = soundPool.load(context, soundFileId, 1)
        
        //map the loaded sound
        loadedSound = mapOf(context!!.getString(R.string.soundID) to soundFileId, context!!.getString(
                    R.string.padSelected) to padSelected)

        //set the state of the application
        ApplicationState.hasLoadedASound = true
        ApplicationState.hasLoadedAKit=false
    }

    fun loadMetronomeSound(soundFileId : Int):Int{

        metronomeSound = soundPool.load(context, soundFileId, 1)

       return metronomeSound

    }

    //Must call this after calling load sound
    //or will return null

    fun getLoadedSoundId(): Map<String, Int> {
        return loadedSound
    }
    fun getMetronomeLoadedSoundId(): Int?{
        return metronomeloadedSound
    }



    fun startSound(sound: Int?, leftVolume: Float, rightVolume: Float) {
        // val streamId = soundPool.play(sound.id, volume, volume, 1, 0, 1f)
        if (sound != null) {
            soundPool.play(sound, leftVolume, rightVolume, 1, 0, 1f)
        }

    }








    fun updateSounds(soundingPitches: Array<Int>) {
        //   val soundsToStop = sounds.filter { it.isPlaying() && !soundingPitches.contains(it.pitch) }
        //   val soundsToPlay = sounds.filter { !it.isPlaying() && soundingPitches.contains(it.pitch) }
        //for (sound in soundsToStop) stopSound(sound)
        // for (sound in soundsToPlay) startSound(sound)
    }

    private fun stopSound(sound: Sound) {
        // soundPool.stop(sound.streamId)
        // sound.streamId = 0
    }


}