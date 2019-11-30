package com.example.nextsoundz.Tasks


import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.SoundPool
import android.os.SystemClock
import android.util.Log
import com.example.nextsoundz.MainActivity
import com.example.nextsoundz.R
import com.example.nextsoundz.Singleton.ApplicationState
import com.example.nextsoundz.Singleton.Bpm
import com.example.nextsoundz.Singleton.Metronome
import java.util.*


class PlayEngineTask(applicationContext: Context) : TimerTask() {


    private lateinit var callback: MetronomeListener
    private var soundPool: SoundPool
    private var interval: Long = 1L
    private var milliPerBeat: Long = 0L
    private var sound = 0
    private var isSoundLoaded = false
    private var startTime = 0L

    private var context = applicationContext

    interface MetronomeListener {
        fun updateProgressBar()
    }

    fun setProgressListener(callback: MetronomeListener) {
        this.callback = callback as MainActivity
    }

    var player: AudioTrack
    val minBufferSize: Int

    init {

        minBufferSize = AudioTrack.getMinBufferSize(
            44100, AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )



        player = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(44100)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(minBufferSize)
            .build()


        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .build()
        // } else
        // soundPool = SoundPool(1, AudioManager.STREAM_MUSIC, 0)


        sound = soundPool.load(applicationContext, Metronome.getSoundId(), 1)
    }


    fun play(): Boolean {


        var i = 0
        // var music: ByteArray? = null


        val inputStream = context.getResources().openRawResource(R.raw.sound3)
//
//
//        try {
//
//
////
////            player.play()
////
////            i = inputStream.read(music)
////
////            while (i != -1) {
////                player.write(music, 0, i)
//
//            }


        // val buff = ByteArray(1230)
//            inputStream.buffered().use { input ->
//
//
//
//
//                while(true) {
//                    val sz = input.read(music)
//                    if (sz <= 0) break
//
//                    ///at that point we have a sz bytes in the buff to process
//                    player.write(music, 0, sz)
//                }
//            }


//            while ((i = `is`.read(music)) != -1)
//                player.write(music, 0, i)

//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//        player.stop()
//        player.release()
        return true
    }


    override fun run() {


        /////////////ENGINE INITIAL START TIME BEFORE WE ENTER OUR ENGINE LOOP///////
        var startTime = SystemClock.uptimeMillis()

        //////////////MILLISECONDS INTERVAL TIME PERIOD/////////////
        var interV = startTime + Bpm.getConvertedBeatPerMilliSec()
        //Log.d("engineCounter", "interv time outside loop $interV.toString()")

        //////////////ENGINE LOOP/////////////////
        while (ApplicationState.isPlaying) {

           // Log.d("engineCounter", "engine loop is still runnung")


            ////////////MILLISECOND ENGINE COUNTER THAT UPDATES INSIDE OF LOOP //////
            var engineCounter = SystemClock.uptimeMillis()


            //////////////////METRONOME///////////////////////////////////////////////////////////////////
            //               METRONOME
            /////////////////////////////////////////////////////////////////////////////////////////////
            if (Metronome.isActive()) {

                if (engineCounter == interV) {

                    //////////play sound////////
                    soundPool.play(sound, 1.0f, 1.0f, 10, 0, 1.0f)

                    //////////update progress//////////
                    callback.updateProgressBar()

                    ////////////update our next interval since we started the play engine/////////
                    interV = engineCounter + Bpm.getConvertedBeatPerMilliSec()
                    // Log.d("engineCounter", "PLAY A CLICK SOUND")


                }
            } else {
                ///////set a new interval counter when we return back to a active state
                interV = engineCounter + Bpm.getConvertedBeatPerMilliSec()

            }

            ///////////////////////////////////////////////////////////////////////////////////////
            //
            ///////////////////////////////////////////////////////////////////////////////////////








        }


    }


}


//
//        if (Metronome.isActive()) {
//           // metronomeCounter++
//
//
//                if (isSoundLoaded) {
//                    soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
//
//                        soundPool.play(sound, 1.0f, 1.0f, 10, 0, 1.0f)
//                        isSoundLoaded = true
//                    }
//                } else {
//                    soundPool.play(sound, 1.0f, 1.0f, 10, 0, 1.0f)
//                }
//
//                callback.updateProgressBar()
//                //reset engineCounter
//               // metronomeCounter = 1
//
//        }










