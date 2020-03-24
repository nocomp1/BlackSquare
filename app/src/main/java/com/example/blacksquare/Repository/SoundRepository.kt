package com.example.blacksquare.Repository

import android.content.res.AssetManager
import timber.log.Timber
import java.io.IOException

class SoundRepository {


    /**
     * fetch sound on the users device - or from cloud service
     */

    fun fetchAllSounds(assets: AssetManager): MutableMap<String, List<String>> {

        val list = mutableMapOf<String, List<String>>()

        try {
            val folderList = assets.list("sounds")
            //loop through the list of folders that contain sounds
            for (folder in folderList!!.indices) {
                // get  the folder name
                val folderName = folderList.get(folder)

                //get the list of assets in that folder
                val assets = assets.list("sounds/$folderName")
                //put the folder name and its assets into a map
                list.put(folderName, assets!!.toList())

            }


            return list
        } catch (io: IOException) {
            Timber.d("${io.printStackTrace()}")
            return list
        }

    }

    fun fetchSounds(query: String, assets: AssetManager): Array<out String>? {
        var list: Array<out String>? = null
        try {
            list = assets.list("sounds/$query")!!
            return list
        } catch (io: IOException) {
            Timber.d("${io.printStackTrace()}")
            return list
        }

    }

    fun fetchAllLoopSounds(assets: AssetManager):  MutableMap<String, List<String>> {

        val list = mutableMapOf<String, List<String>>()

        try {
            val folderList = assets.list("loops")
            //loop through the list of folders that contain sounds
            for (folder in folderList!!.indices) {
                // get  the folder name
                val folderName = folderList.get(folder)

                //get the list of assets in that folder
                val assets = assets.list("loops/$folderName")
                //put the folder name and its assets into a map
                list.put(folderName, assets!!.toList())

            }

            return list
        } catch (io: IOException) {
            Timber.d("${io.printStackTrace()}")
            return list
        }
    }

    fun fetchLoopSounds(query: String, assets: AssetManager): Array<out String>? {
        var list: Array<out String>? = null
        try {
            list = assets.list("loops/$query")!!
            return list
        } catch (io: IOException) {
            Timber.d("${io.printStackTrace()}")
            return list
        }

    }


}

