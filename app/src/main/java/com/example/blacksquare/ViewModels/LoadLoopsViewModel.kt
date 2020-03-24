package com.example.blacksquare.ViewModels

import android.content.res.AssetManager
import android.widget.ProgressBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blacksquare.Models.LoadLoop
import com.example.blacksquare.Models.LoadSound
import com.example.blacksquare.Repository.SoundRepository
import timber.log.Timber

class LoadLoopsViewModel: ViewModel() {

    private val soundRepository: SoundRepository = SoundRepository()
    private val _viewState = MediatorLiveData<ViewState>()
    val viewState: LiveData<ViewState> get() = _viewState

    private val _loopList = MutableLiveData<List<LoadLoop>>()
        .also { _viewState.addSource(it) { combineLatest() } }

    private val _kitPreviewProgressBar = MutableLiveData<ProgressBar>()
        .also { _viewState.addSource(it) { combineLatest() } }

    private val _loadStoreProgressSpinner = MutableLiveData<Int>()
        .also { _viewState.addSource(it) { combineLatest() } }

    private fun combineLatest() {
        ViewState(
            soundList = _loopList.value ?: ArrayList()

        ).apply { _viewState.value = copy()}
    }

    fun fetchAllLoopSounds(assets: AssetManager){
        // Get list of sounds from repository
        val listOfSoundObjects = mutableListOf<LoadLoop>()

        val soundList = soundRepository.fetchAllLoopSounds(assets)
        var folderName: String

        var i =0
        while ( i < soundList.size) {

            folderName = soundList.keys.elementAt(i)
            val listOfSoundsInFolder : List<String> = soundList.get(folderName)!!
            i++

            for (sound in listOfSoundsInFolder.indices) {

                val title = listOfSoundsInFolder[sound]
                val fileLocation = "$folderName/$title"
                listOfSoundObjects.add(LoadLoop(title, fileLocation))
            }

        }

        //past list to viewState
        _loopList.postValue(listOfSoundObjects)
    }

    private fun filterLoopSoundList(query: String, assets: AssetManager) {

        // Get list of sounds from repository
        val listOfSoundObjects = mutableListOf<LoadLoop>()

        val soundList = soundRepository.fetchLoopSounds(query, assets)
        for (x in soundList!!.indices) {

            val title = soundList.get(x)
            val fileLocation = "$query/$title"
            listOfSoundObjects.add(LoadLoop(title, fileLocation))
        }

        //past list to viewState
        _loopList.postValue(listOfSoundObjects)
    }

    data class ViewState(
        val soundList: List<LoadLoop>
    )

    fun onAction(action: Action) {

        when (action) {
            is Action.OnFilterSounds -> {

                filterLoopSoundList(action.query, action.assets)

            }
        }
    }

    sealed class Action {
        class OnFilterSounds(
            val query: String,
            val assets: AssetManager
        ) : Action()
    }


}