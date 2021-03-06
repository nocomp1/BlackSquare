package com.example.blacksquare.ViewModels

import android.media.MediaPlayer
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.*
import com.example.blacksquare.Models.StoreKit
import com.google.firebase.database.*
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch

class StoreViewModel : ViewModel() {
    private lateinit var database: FirebaseDatabase
    private lateinit var dbReference: DatabaseReference
    private var mediaPlayer: MediaPlayer? = null
    private val _viewState = MediatorLiveData<ViewState>()
    val viewState: LiveData<ViewState> get() = _viewState

    private val _kitList = MutableLiveData<List<StoreKit>>()
        .also { _viewState.addSource(it) { combineLatest() } }

    private val _kitPreviewProgressBar = MutableLiveData<ProgressBar>()
        .also { _viewState.addSource(it) { combineLatest() } }

    private val _loadStoreProgressSpinner = MutableLiveData<Int>()
        .also { _viewState.addSource(it) { combineLatest() } }

    private fun combineLatest() {
        ViewState(
            storeKitList = _kitList.value ?: ArrayList(),
            loadStoreProgressSpinner = _loadStoreProgressSpinner.value ?: 0,
            kitPreviewProgressBar = _kitPreviewProgressBar.value
        ).apply { _viewState.value = copy() }
    }

    data class ViewState(
        val storeKitList: List<StoreKit>,
        val loadStoreProgressSpinner: Int,
        val kitPreviewProgressBar: ProgressBar?
    )

    fun fetchKitData() {
        //show loading spinner
_loadStoreProgressSpinner.postValue(View.VISIBLE)
        viewModelScope.launch {

            database = FirebaseDatabase.getInstance()
            //get reference to the "users" node
            dbReference = database.getReference("kit")

            val listOfKitObjects = mutableListOf<StoreKit>()
            dbReference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                   // for (kits in dataSnapshot.children) {
                    val listOfKit = dataSnapshot.children.map { kits ->
                        val title = kits.child("title").value.toString()
                        val imageUrl = kits.child("imageUrl").value.toString()
                        val kitShortDescription = kits.child("kitDescription").value.toString()
                        val kitFullDescription = kits.child("kitFullDescription").value.toString()
                        val kitPrice = kits.child("kitPrice").value.toString()
                        val sale = kits.child("sale").value.toString().toBoolean()
                        val previewUrl = kits.child("previewUrl").value.toString()

                      //  listOfKitObjects.add(
                            StoreKit(
                                title,
                                imageUrl,
                                kitShortDescription,
                                kitFullDescription,
                                kitPrice,
                                sale,
                                previewUrl
                            )
                       // )

                }
                  //  }

                  //  val listOfKit = listOfKitObjects.toList()
                    _kitList.postValue(listOfKit)
                    //hide loading spinner
                    _loadStoreProgressSpinner.postValue(View.INVISIBLE)
                }
            })

        }

    }

    fun action(action: Action) {
        when (action) {
            is Action.StartPreviewKitAction -> {
                playKitPreview(action.url,action.previewProgressBar)
            }
            is Action.BuyKitAction -> TODO()
            is Action.ViewKitDetailsAction -> TODO()
            is Action.FavoriteKitAction -> TODO()
            is Action.StopPreviewKitAction -> {
                stopPreview()
            }
        }
    }

    private fun stopPreview() {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
        }

    }

    private fun playKitPreview(url: String, previewProgressBar: ProgressBar) {

    }

    sealed class Action {
        class StartPreviewKitAction(val url: String, val previewProgressBar : ProgressBar) : Action()
        object StopPreviewKitAction : Action()
        class BuyKitAction(val kitId: Int) : Action()
        class ViewKitDetailsAction(val kitId: Int) : Action()
        class FavoriteKitAction(val kitId: Int) : Action()

    }

    sealed class Event {
        class PreviewProgress(val currentPosition: Int)
    }

    override fun onCleared() {
        super.onCleared()

    }
}