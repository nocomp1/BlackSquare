package com.example.blacksquare.Managers

import android.content.Context
import android.util.ArrayMap
import android.util.Log
import com.example.blacksquare.Objects.PadSequenceTimeStamp
import com.example.blacksquare.Singleton.ApplicationState
import com.example.blacksquare.Utils.BpmUtils

class DrumPadPlayBack(applicationContext: Context) {

    private var soundPool2: DrumPadSoundPool

    private var millisecSequenceIndexCounter = 0L


    init {
        //////Sound Pool for playback///////
        val drumPadSoundPool = DrumPadSoundPool(applicationContext)
        soundPool2 = drumPadSoundPool //startEngine()
        soundPool2.loadSoundKit(SoundResManager.getDefaultKitFilesIds())

    }


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

    companion object {

        val padHitUndoSequenceList: ArrayList<ArrayList<ArrayMap<Long, PadSequenceTimeStamp>>>? =
            ArrayList()

    }

    //triggered every millisecond
    fun padPlayback(padIndex: Int, padLftVolume: Float, padRftVolume: Float) {

        millisecSequenceIndexCounter = ApplicationState.sequenceMillisecClock
        //Timber.d("millisecond counter $millisecSequenceIndexCounter")
       // Log.d("fuckJACK","millisecSequenceIndexCounter= ${ApplicationState.uiSequenceMillisecCounter}")

        //check if the pad contains a millisecond time stamp that matches the ApplicationState
        //sequence counter. Example: if we recorded a drum hit time stamp at 22 milliseconds
        // into the sequence we will play that sound every 22 milliseconds into the sequence

        if (ApplicationState.padHitSequenceArrayList!![padIndex].contains(millisecSequenceIndexCounter)
        ) {
            //Log.d("soundPlayTimeStamp","key/timestamp for  retreived playback= ${ApplicationState.uiSequenceMillisecCounter}")

            soundPool2.startSound(
                ApplicationState.padHitSequenceArrayList!![padIndex][millisecSequenceIndexCounter]!!.soundId,
                padLftVolume,
                padRftVolume
            )

        }

        // if a note has been recorded
        // store array for undo action each time sequence loop
        // and only if there is a new hit to the array
        if ((ApplicationState.drumNoteHasBeenRecorded)) {

                //if the undo list is empty add arraylist of array maps to it
                if ((padHitUndoSequenceList!!.isEmpty())) {

                    //loop through all pads
                    var padIndexCounter = 0

                    while (padIndexCounter < ApplicationState.padHitSequenceArrayList!!.size) {

                        // Log.d("undoff", "inside empty")

                        //copy each pad sequence from sequence array list and add it to the undo list
                        val arrayMapCopy =
                            ArrayMap(ApplicationState.padHitSequenceArrayList!![padIndexCounter])
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

                    // our sequence loop in milliseconds
                    if (millisecSequenceIndexCounter == BpmUtils.getSequenceTimeInMilliSecs()) {

                        Log.d("sequencetime","iteration=")

                        //loop through all pads
                    var padIndexCounter1 = 0
                    while (padIndexCounter1 < ApplicationState.padHitSequenceArrayList!!.size) {


                        // Log.d("undoff", "List hit size${ApplicationState.padHitSequenceArrayList!![padIndexCounter1].size}")

                        // Log.d("undoff", "undo hit size${padHitUndoSequenceList!![padIndexCounter1].last().size}")

                        //check what pads has new pattern
                        if (ApplicationState.padHitSequenceArrayList!![padIndexCounter1].size >
                            padHitUndoSequenceList!![padIndexCounter1].last().size
                        ) {

                            // Log.d("undoff", "inside if statement")

                            //if we have changes add the map to the array list
                            //loop through all pads and add for every pad
                            var padIndexCounter2 = 0

                            while (padIndexCounter2 < ApplicationState.padHitSequenceArrayList!!.size) {

                                //  Log.d("undoff", "inside adding to all pads")

                                //add the array map to the already added array list of array maps
                                val arrayMapCopy =
                                    ArrayMap(ApplicationState.padHitSequenceArrayList!![padIndexCounter2])

                                padHitUndoSequenceList!![padIndexCounter2].add(arrayMapCopy)

                                // Log.d("undoff", "undo list size after the add all 4 should be same${padHitUndoSequenceList!![padIndexCounter2].size}")

                                padIndexCounter2++
                            }

                        }

                        padIndexCounter1++
                    }

                }

            }

        }

    }

    /**
     * use to reset counter when playback is stopped tho sync playback
     */
    fun resetCounter() {
        millisecSequenceIndexCounter = 0
    }

}