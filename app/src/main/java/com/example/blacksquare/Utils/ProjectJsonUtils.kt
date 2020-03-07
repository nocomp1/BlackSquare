package com.example.blacksquare.Utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonIOException
import timber.log.Timber
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class ProjectJsonUtils(private val applicationContext: Context) {

    data class WriteJsonTree(
        val project: ProjectJsonObjects.Project = ProjectJsonObjects.Project()

    )

    fun writeToFile(analyticsTree: WriteJsonTree) {

        val gson = Gson()
        val file: File
        try {

            file = File(applicationContext.filesDir, FILE_NAME)
            val fr = FileWriter(file)
            gson.toJson(analyticsTree, fr)

            fr.flush()
            fr.close()

        } catch (e: NullPointerException) {

            Timber.d(e.printStackTrace().toString())

        } catch (io: IOException) {
            Timber.d(io.printStackTrace().toString())

        } catch (jsonIo: JsonIOException) {
            Timber.d(jsonIo.printStackTrace().toString())

        }

    }

    fun getObject(): WriteJsonTree {

        val gson = Gson()
        val file: File
        var analyticsTree = WriteJsonTree()

        try {
            file = File(applicationContext.filesDir, FILE_NAME)

            if (file.exists() && !file.isDirectory()) {

                // get updated Json - parse Json file into AnalyticsTree object
                val fileReader = FileReader(file)
                analyticsTree = gson.fromJson(fileReader, WriteJsonTree::class.java)
                fileReader.close()

            } else {
                //Return new object
                return analyticsTree
            }
        } catch (e: NullPointerException) {

            Timber.d(e.printStackTrace().toString())

        } catch (io: IOException) {
            Timber.d(io.printStackTrace().toString())

        } catch (jsonIo: JsonIOException) {
            Timber.d(jsonIo.printStackTrace().toString())
        }

        return analyticsTree

    }

    companion object {
        const val FILE_NAME = "project.json"

    }
}