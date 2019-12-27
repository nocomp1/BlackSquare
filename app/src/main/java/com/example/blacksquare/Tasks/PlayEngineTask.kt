package com.example.blacksquare.Tasks


import android.R
import android.app.Activity
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.blacksquare.MainActivity
import com.example.blacksquare.Singleton.Bpm
import com.example.blacksquare.Singleton.Metronome
import kotlinx.android.synthetic.main.activity_main.*


class PlayEngineTask(applicationContext: Context) : Runnable {


    private lateinit var callback: MetronomeListener
    private var soundPool: SoundPool

    private var sound = 0
    private var context = applicationContext

    interface MetronomeListener {
        fun updateProgressBar()
        fun updateUiClockEveryMilliSec()
    }

    fun setProgressListener(callback: MetronomeListener) {
        this.callback = callback as MainActivity
    }

   // var player: AudioTrack
   // val minBufferSize: Int

    init {


//        minBufferSize = AudioTrack.getMinBufferSize(
//            44100, AudioFormat.CHANNEL_OUT_MONO,
//            AudioFormat.ENCODING_PCM_16BIT
//        )
//
//
//
//        player = AudioTrack.Builder()
//            .setAudioAttributes(
//                AudioAttributes.Builder()
//                    .setUsage(AudioAttributes.USAGE_GAME)
//                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                    .build()
//            )
//            .setAudioFormat(
//                AudioFormat.Builder()
//                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
//                    .setSampleRate(44100)
//                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
//                    .build()
//            )
//            .setBufferSizeInBytes(minBufferSize)
//            .build()


        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        // } else
        // soundPool = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .build()

        sound = soundPool.load(applicationContext, Metronome.getSoundId(), 1)

        //metronomeInterval= Bpm.getBeatPerMilliSeconds()
    }


    /**
     * This engine is triggered every millisecond
     */
    override fun run() {

        callback.updateUiClockEveryMilliSec()


//
//        Log.d("engineCounter", "millis= $millisecond milli second")
//
//
//        if (Metronome.isActive()) {
//
//            if ((millisecond == metronomeInterval) || (millisecond == 0L)) {
//                soundPool.play(sound, 1.0f, 1.0f, 10, 0, 1.0f)
//
//                //always updating progress every beat
//                callback.updateProgressBar()
//                metronomeInterval = millisecond + Bpm.getBeatPerMilliSeconds()
//            }
//        } else {
//            metronomeInterval = millisecond + Bpm.getBeatPerMilliSeconds()
//        }

       // photoManager.handleState(this,1)
//callback.updateUiClockEveryMilliSec()


//
//      //  Log.d("engineCounter", "xxxxxxxxx${ApplicationState.isPlaying}")
//        /////////////ENGINE INITIAL START TIME BEFORE WE ENTER OUR ENGINE LOOP///////
//        var startTime = SystemClock.elapsedRealtime()
//        Log.d("engineCounter", "interv time outside loop start time $startTime")
//
//        ///MILLISECONDS INTERVAL TIME PERIOD FOR
//        // EACH BEAT PER MILLISECONDS(EX: 750 MILLSEC IS 80BPM)/////////////
//        var bpmInterval = startTime + Bpm.getBeatPerMilliSeconds()
//        Log.d("engineCounter", "interv time outside loop bpm $bpmInterval")
//
//        var milisecInterval = startTime + 1
//
//
//        var x = 0
//
//        //////////////ENGINE LOOP/////////////////
//        while (ApplicationState.isPlaying) {
//
//            // Log.d("engineCounter", "inside engine loop is still runnung")
//
//
//            ////////////MILLISECOND ENGINE COUNTER THAT UPDATES INSIDE OF LOOP //////
//            var engineCounter = SystemClock.elapsedRealtime()
//            Log.d("engineCounter", "engineCounter= $engineCounter")
//            var milliSecEngineCounter = SystemClock.elapsedRealtimeNanos()
//            Log.d("engineCounter", "bpmInterval..= $bpmInterval")
//           // Log.d("engineCounter", "milisecInterval= $milisecInterval")
//            //////////////////METRONOME///////////////////////////////////////////////////////////////////
//            //               METRONOME
//            /////////////////////////////////////////////////////////////////////////////////////////////
//            if (Metronome.isActive()) {
//
//                if (engineCounter == bpmInterval ) {
//
//                    //////////play sound////////
//                    soundPool.play(sound, 1.0f, 1.0f, 10, 0, 1.0f)
//
//                    //////////update progress//////////
//                    callback.updateProgressBar()
//
//                    ////////////update our next interval since we started the play engine/////////
//                    bpmInterval = engineCounter + Bpm.getBeatPerMilliSeconds()
//                     Log.d("engineCounter", "PLAY A CLICK SOUND")
//
//
//                }
//            } else {
//
//                ///////set a new interval counter when we return back to a active state
//                bpmInterval = engineCounter + Bpm.getBeatPerMilliSeconds()
//
//            }
//
//            ///////////////////////////////////////////////////////////////////////////////////////
//            //
//            ///////////////////////////////////////////////////////////////////////////////////////
//
////            if (ApplicationState.isMillisecondClockPlaying){
////
////            // Log.d("xxxxxx","${milliSecEngineCounter} and ${milisecInterval} passed")
////                if (milliSecEngineCounter == milisecInterval) {
////                    x+=1
////                     ApplicationState.millisecondClock= x
////                    Log.d("xxxxxx", "${x} millisecond passed")
////
////                    milisecInterval = (milliSecEngineCounter + 1L)
////
////                   // Log.d("xxxxxx", "what millsecInterval equal now =${milisecInterval} passed")
////                }
////        }else{
////                milisecInterval = milliSecEngineCounter + 1L
////            }
////
//
//
//
//
//
//        }
//
//
    }


}








