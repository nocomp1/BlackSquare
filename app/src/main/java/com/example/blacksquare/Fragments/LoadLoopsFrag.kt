package com.example.blacksquare.Fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blacksquare.Items.LoopSoundItem
import com.example.blacksquare.Items.SoundItem
import com.example.blacksquare.Models.LoadLoop
import com.example.blacksquare.Models.LoadSound
import com.example.blacksquare.R
import com.example.blacksquare.ViewModels.LoadLoopsViewModel
import com.example.blacksquare.ViewModels.LoadSoundViewModel
import com.example.blacksquare.Views.ToggleButtonGroupTableLayout
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.load_loops_layout.*
import kotlinx.android.synthetic.main.load_sound_layout.*
import java.util.concurrent.ScheduledExecutorService

class LoadLoopsFrag : Fragment() , LoopSoundItem.ItemClickListenerInterface,
    ToggleButtonGroupTableLayout.ToggleButtonListener{

    private val listener: LoopSoundItem.ItemClickListenerInterface = this
    private var viewModel: LoadLoopsViewModel? = null
    private var previewIsPlaying = false
    private var recyclerView: RecyclerView? = null
    private var mediaPlayer: MediaPlayer? = null
    private var previewKitProgressEngineExecutor : ScheduledExecutorService? = null
    private var currentUrlPlaying: Any = LoadSoundFrag.Event.NoPreviewPlaying

    private val filterListener: ToggleButtonGroupTableLayout.ToggleButtonListener = this

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view) as RecyclerView
        recyclerView?.setHasFixedSize(true)

        load_loops_radio_group.setUpListener(filterListener)

        this.let {
            viewModel = ViewModelProviders.of(it).get(LoadLoopsViewModel::class.java)
        }

        viewModel?.fetchAllLoopSounds(activity!!.assets)

        viewModel?.viewState?.observe(this, Observer {

            initRecyclerView(it.soundList.toRecyclerListItem())
        })

    }

    //Filter tag
    override fun onToggleButtonClicked(radioButton: RadioButton?) {
        viewModel?.onAction(LoadLoopsViewModel.Action.OnFilterSounds(radioButton?.text.toString(),activity!!.assets))
    }

    private fun initRecyclerView(items: List<LoopSoundItem>) {

        val groupAdapter = GroupAdapter<ViewHolder>().apply {

            addAll(items)
            setHasStableIds(true)
        }

        //set up the layout manager and set the adapter
        recyclerView?.apply {

            layoutManager = LinearLayoutManager(activity!!.applicationContext)
            adapter = groupAdapter
            addItemDecoration(
                DividerItemDecoration(
                    activity!!.applicationContext,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        groupAdapter.setOnItemClickListener { item, view ->

        }

    }

    // custom extension function
    private fun List<LoadLoop>.toRecyclerListItem(): List<LoopSoundItem> {
        return this.map { sound ->
            LoopSoundItem(
                listener,
                sound.title,
                sound.fileLocation
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        previewKitProgressEngineExecutor = null
        recyclerView = null
        viewModel = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.load_loops_layout, container, false)
        return view
    }

    override fun onPreviewLoopItemClicked(fileLocation: String, title: String, view: View) {

    }

    override fun onLoadSoundItemClicked(fileLocation: String, title: String, view: View) {

    }

}