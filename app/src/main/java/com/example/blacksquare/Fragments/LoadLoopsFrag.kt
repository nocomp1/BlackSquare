package com.example.blacksquare.Fragments

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blacksquare.Items.LoopSoundItem
import com.example.blacksquare.Models.LoadLoop
import com.example.blacksquare.R
import com.example.blacksquare.ViewModels.LoadLoopsViewModel
import com.example.blacksquare.Views.ToggleButtonGroupTableLayout
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.load_loops_layout.*
import java.util.concurrent.ScheduledExecutorService

class LoadLoopsFrag : Fragment(), LoopSoundItem.ItemClickListenerInterface,
    ToggleButtonGroupTableLayout.ToggleButtonListener {

    private val listener: LoopSoundItem.ItemClickListenerInterface = this
    private var viewModel: LoadLoopsViewModel? = null
    private var previewIsPlaying = false
    private var recyclerView: RecyclerView? = null
    private var mediaPlayer: MediaPlayer? = null
    private var previewKitProgressEngineExecutor: ScheduledExecutorService? = null
    private var currentUrlPlaying: Any = Event.NoPreviewPlaying

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
        viewModel?.onAction(
            LoadLoopsViewModel.Action.OnFilterSounds(
                radioButton?.text.toString(),
                activity!!.assets
            )
        )
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


    override fun onLoadLoopItemClicked(fileLocation: String, title: String, view: View) {

    }


    override fun onPreviewLoopItemClicked(fileLocation: String, title: String, view: View) {
        //check if the url is the same
        //that way we only play one kit at a time.
        //We reset the currentUrlPlaying when we stop

        when (currentUrlPlaying) {
            Event.NoPreviewPlaying -> {
                //set current playing and play preview
                currentUrlPlaying = fileLocation
                playSoundItem(view, fileLocation, title)
            }
            fileLocation -> {
                //reset current playing and stop preview
                currentUrlPlaying = Event.NoPreviewPlaying
                stopSoundItem(view)

            }
            else -> {
                Toast.makeText(
                    activity!!.applicationContext,
                    "Please stop current preview",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }


    private fun stopSoundItem(view: View) {
        currentUrlPlaying = Event.NoPreviewPlaying
        view.background = ContextCompat.getDrawable(
            activity!!.applicationContext,
            R.drawable.ic_play_circle_outline_black_24dp
        )

        previewIsPlaying = false
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
        }


    }

    private fun playSoundItem(view: View, fileLocation: String, title: String) {
        mediaPlayer = null
        //change play to pause
        view.background = ContextCompat.getDrawable(
            activity!!.applicationContext,
            R.drawable.ic_pause_circle_outline_black_24dp
        )

        previewIsPlaying = true

        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            //show loading spinner
            val afd = activity!!.applicationContext.assets.openFd("$FOLDER_NAME/$fileLocation")
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)

            prepare()
            setOnPreparedListener {

                start()

                setOnCompletionListener { mp ->

                    stopSoundItem(view)
                }
            }


        }


    }

    companion object {

        const val FOLDER_NAME = "loops"
    }

    sealed class Event {
        object NoPreviewPlaying : Event()
    }
}