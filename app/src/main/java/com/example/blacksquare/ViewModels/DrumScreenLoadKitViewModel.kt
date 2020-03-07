package com.example.blacksquare.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blacksquare.Models.Kit
import com.example.blacksquare.Repository.LoadRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class DrumScreenLoadKitViewModel : ViewModel() {

    private val disposables = CompositeDisposable()
    private val loadRepository: LoadRepository = LoadRepository()
    private val _viewState = MediatorLiveData<ViewState>()
    val viewState: LiveData<ViewState> get() = _viewState

    private val _kitList = MutableLiveData<ArrayList<Kit>>()
        .also { _viewState.addSource(it) { combineLatest() } }

    private fun combineLatest() {
        ViewState(
            _kitList.value ?: ArrayList<Kit>()
        ).apply { _viewState.value = copy() }
    }

    data class ViewState(
        val kitList: ArrayList<Kit>
    )


    fun fetchKitData() {
        loadRepository.fetchKitData()
            .subscribeOn(Schedulers.io())
            .subscribe ({
                Timber.d("KitListSize= ${it.size}")


            _kitList.postValue(it)

        },{
            Timber.d(it.printStackTrace().toString())

        }).also { disposables.add(it) }

    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}