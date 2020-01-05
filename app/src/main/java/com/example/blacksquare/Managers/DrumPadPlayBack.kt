package com.example.blacksquare.Managers

import android.content.Context
import android.util.ArrayMap
import android.util.Log
import com.example.blacksquare.Objects.PadSequenceTimeStamp
import com.example.blacksquare.Singleton.ApplicationState
import com.example.blacksquare.Singleton.Bpm

class DrumPadPlayBack(context: Context) {

    private var soundPool2: DrumPadSoundPool


    private var millisecSequenceIndexCounter = 0L



    init {
        //////Sound Pool for playback///////
        val drumPadSoundPool = DrumPadSoundPool(context)
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

companion object{

    val  padHitUndoSequenceList:ArrayList<ArrayList<ArrayMap<Long, PadSequenceTimeStamp>>>? = ArrayList()

}
    fun padPlayback(padIndex: Int, padLftVolume: Float, padRftVolume: Float) {


        if (ApplicationState.padHitSequenceArrayList!![padIndex].contains(
                millisecSequenceIndexCounter
            )
        ) {

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

            // our sequence loop in milliseconds
            if (millisecSequenceIndexCounter == Bpm.getPatternTimeInMilliSecs()) {

                //if the undo list is empty add arraylist of array maps to it
                if ((padHitUndoSequenceList!!.isEmpty())) {

                    //loop through all pads
                    var padIndexCounter = 0

                    while (padIndexCounter < ApplicationState.padHitSequenceArrayList!!.size) {

                      // Log.d("undoff", "inside empty")

                        //copy each pad sequence from sequence array list and add it to the undo list
                        val arrayMapCopy =ArrayMap(ApplicationState.padHitSequenceArrayList!![padIndexCounter])
                        val arrayListcopy = arrayListOf(arrayMapCopy)

                        //Add the array list of array maps to the undo list
                     padHitUndoSequenceList.add(arrayListcopy)


//
//                        Log.d(
//                            "undoff",
//                            "padHitUndoSequenceList Size= ${ApplicationState.padHitUndoSequenceList!![padIndexCounter][timeStampArrayMapIndex].size} hits"
//                        )
//
//                        Log.d(
//                            "undoff",
//                            "padHitUndoSequenceList Size= ${ApplicationState.padHitUndoSequenceList!![padIndexCounter].size} size"
//                        )
//                        Log.d(
//                            "undoff",
//                            "padHitSequenceArrayList value= ${ApplicationState.padHitSequenceArrayList!![padIndexCounter]} timestamps"
//                        )
//                        Log.d(
//                            "undoff",
//                            "padHitUndoSequenceList Size= ${ApplicationState.padHitUndoSequenceList!![padIndexCounter][0].size} "
//                        )


                        //move to the next pad index to check
                        padIndexCounter++
                    }


                    //exit
                    //return

                } else {

                    //1. check what pads has new pattern by seeing if size has changed
                    //2. compare against the last added index in the undo array map pattern size
                    //3. loop and add entire (every index) new array map patterns to the undo list
                    //4. Update the global application drum pad undo list with local copy


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

                               // Log.d("undoff", "local undo size  ${padHitUndoSequenceList!![0].last().size}")
                                //Feed the new undo updated list to the application list every pattern loop iteration
                               // ApplicationState.padHitUndoSequenceList = padHitUndoSequenceList

                               // Log.d("undoff", "Global undo size  ${ApplicationState.padHitUndoSequenceList!![0].last().size}")

                            }

                            //    Log.d("undoff", "hit size of new added last index after add ${padHitUndoSequenceList!![padIndexCounter1].last().size}")
                           // Log.d("undoff", "hit size of original ${ApplicationState.padHitSequenceArrayList!![padIndexCounter1].size}")


                        padIndexCounter1++
                    }

                }

            }


            if (ApplicationState.uiSequenceMillisecCounter == Bpm.getPatternTimeInMilliSecs()) {
                millisecSequenceIndexCounter = 0

            }
            //move to the next index
            millisecSequenceIndexCounter++
        }
    }

        /**
         * use to reset counter when playback is stopped tho sync playback
         */
        fun resetCounter() {

            millisecSequenceIndexCounter = 0
        }

    }