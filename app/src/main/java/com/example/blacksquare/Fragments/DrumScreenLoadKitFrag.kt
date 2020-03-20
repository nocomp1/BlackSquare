package com.example.blacksquare.Fragments

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blacksquare.Items.KitItem
import com.example.blacksquare.Models.Kit
import com.example.blacksquare.R
import com.example.blacksquare.ViewModels.DrumScreenLoadKitViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class DrumScreenLoadKitFrag : Fragment(), KitItem.ItemClickListenerInterface {
    private val listener: KitItem.ItemClickListenerInterface = this
    private lateinit var viewModel: DrumScreenLoadKitViewModel
    private var previewIsPlaying = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.setHasFixedSize(true)

        this.let {
            viewModel = ViewModelProviders.of(it).get(DrumScreenLoadKitViewModel::class.java)
        }

        viewModel.fetchKitData()

        viewModel.viewState.observe(this, Observer {


            // it.kitPreviewProgressBar!!.progress = it.kitPreviewProgress
            initRecyclerView(it.kitList.toRecyclerListItem())
        })

    }

    private fun initRecyclerView(items: List<KitItem>) {

        val groupAdapter = GroupAdapter<ViewHolder>().apply {
            spanCount = 2
            addAll(items)
        }
        //set up the layout manager and set the adapter
        recyclerView.apply {

            layoutManager = GridLayoutManager(activity, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
        }

        groupAdapter.setOnItemClickListener { item, view ->

        }
    }

    // custom extension function
    private fun List<Kit>.toRecyclerListItem(): List<KitItem> {
        return this.map { kit ->
            KitItem(
                kit.title,
                kit.imageUrl,
                kit.kitShortDescription,
                kit.kitPrice,
                kit.sale,
                kit.previewUrl,
                activity!!.applicationContext, listener
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.drum_screen_load_kit_layout, container, false)
        return view
    }

    private var mediaPlayer: MediaPlayer? = null
    private val previewKitProgressEngineExecutor = Executors.newScheduledThreadPool(1)

    override fun onPreviewKitItemClicked(
        view: View,
        previewUrl: String,
        previewProgressBar: ProgressBar
    ) {

        if (previewIsPlaying) {

            stopKitItem(view,previewProgressBar)

        } else {

            playKitItem(view,previewUrl,previewProgressBar)

        }

    }

    private fun stopKitItem(view: View,previewProgressBar: ProgressBar) {
        //Resetting the progress state
        previewProgressBar.progress = 0
        //shutdown incrementing progress bar
        previewKitProgressEngineExecutor.shutdown()


        view.background = ContextCompat.getDrawable(
            activity!!.applicationContext,
            R.drawable.ic_play_circle_outline_black_24dp
        )
        //  viewModel.action(DrumScreenLoadKitViewModel.Action.StopPreviewKitAction)
        previewIsPlaying = false
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
        }
    }

    private fun playKitItem(view: View, previewUrl: String, previewProgressBar: ProgressBar) {


        view.background = ContextCompat.getDrawable(
            activity!!.applicationContext,
            R.drawable.ic_pause_circle_outline_black_24dp
        )
        previewIsPlaying = true


        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(previewUrl)
            prepareAsync()
            setOnPreparedListener {

                start()

                val action: Runnable = Runnable {
                    previewProgressBar.max = duration
                    previewProgressBar.progress += 1

                    //shut down progress when song finish
                    if (previewProgressBar.progress == previewProgressBar.max) {
                        previewKitProgressEngineExecutor.shutdown()
                    }
                }

                //start incrementing the progressbar
                previewKitProgressEngineExecutor.scheduleAtFixedRate(
                    action,
                    0,
                    1000,
                    TimeUnit.MICROSECONDS
                )

            }
            setOnCompletionListener { mp ->

                previewKitProgressEngineExecutor.shutdown()

                stopKitItem(view,previewProgressBar)
            }
        }

    }

    private lateinit var recyclerView: RecyclerView
}