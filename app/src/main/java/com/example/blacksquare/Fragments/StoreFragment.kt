package com.example.blacksquare.Fragments

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blacksquare.Fragments.StoreFragment.Event.NoPreviewPlaying
import com.example.blacksquare.Items.KitItem
import com.example.blacksquare.Models.StoreKit
import com.example.blacksquare.R
import com.example.blacksquare.ViewModels.StoreViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class StoreFragment : Fragment(), KitItem.ItemClickListenerInterface {

    private val listener: KitItem.ItemClickListenerInterface = this
    private var viewModel: StoreViewModel? = null
    private var previewIsPlaying = false
    private var recyclerView: RecyclerView? = null
    private var mediaPlayer: MediaPlayer? = null
    private var previewKitProgressEngineExecutor: ScheduledExecutorService? = null
    private var currentUrlPlaying: Any = NoPreviewPlaying
    private var storeProgressSpinner: ProgressBar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_view) as RecyclerView
        recyclerView?.setHasFixedSize(true)

        storeProgressSpinner = activity!!.findViewById(R.id.store_progress_spinner)

        this.let {
            viewModel = ViewModelProviders.of(it).get(StoreViewModel::class.java)
        }

        viewModel?.fetchKitData()

        viewModel?.viewState?.observe(this, Observer {
            storeProgressSpinner?.visibility = it.loadStoreProgressSpinner
            initRecyclerView(it.storeKitList.toRecyclerListItem())
        })

    }

    private fun initRecyclerView(items: List<KitItem>) {

        val groupAdapter = GroupAdapter<ViewHolder>().apply {
            spanCount = 2
            addAll(items)
        }
        //set up the layout manager and set the adapter
        recyclerView?.apply {

            layoutManager = GridLayoutManager(activity, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
        }

        groupAdapter.setOnItemClickListener { item, view ->

        }
    }

    // custom extension function
    private fun List<StoreKit>.toRecyclerListItem(): List<KitItem> {
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
        previewKitProgressEngineExecutor = null
        recyclerView = null
        viewModel = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.drum_screen_load_kit_layout, container, false)
        return view
    }

    override fun onPreviewKitItemClicked(
        view: View,
        previewUrl: String,
        previewProgress: ProgressBar,
        kitPreviewLoading: ProgressBar
    ) {

        //check if the url is the same
        //that way we only play one kit at a time.
        //We reset the currentUrlPlaying when we stop

        when (currentUrlPlaying) {
            NoPreviewPlaying -> {
                //set current playing and play preview
                currentUrlPlaying = previewUrl
                playKitItem(view, previewUrl, previewProgress, kitPreviewLoading)
            }
            previewUrl -> {
                //reset current playing and stop preview
                currentUrlPlaying = NoPreviewPlaying
                stopKitItem(view, previewProgress)

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

    sealed class Event {
        object NoPreviewPlaying : Event()
    }

    private fun stopKitItem(view: View, previewProgressBar: ProgressBar) {
        //Resetting the progress state
        previewProgressBar.progress = 0

        //shutdown incrementing progress bar
        if (!previewKitProgressEngineExecutor?.isShutdown!!) {
            previewKitProgressEngineExecutor?.shutdown()
        }

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

    private fun playKitItem(
        view: View,
        previewUrl: String,
        previewProgressBar: ProgressBar,
        kitPreviewLoading: ProgressBar
    ) {
        //Initialize our executor service
        previewKitProgressEngineExecutor = Executors.newScheduledThreadPool(1)

        //change play to pause
        view.background = ContextCompat.getDrawable(
            activity!!.applicationContext,
            R.drawable.ic_pause_circle_outline_black_24dp
        )
        //update our flag
        previewIsPlaying = true

        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            //show loading spinner
            kitPreviewLoading.visibility = View.VISIBLE

            setDataSource(previewUrl)
            prepareAsync()
            setOnPreparedListener {

                start()

                //hide loading spinner
                kitPreviewLoading.visibility = View.INVISIBLE

                //This will run every millisecond
                val action = Runnable {
                    previewProgressBar.max = duration
                    previewProgressBar.progress += 1

                    //shut down progress when song finish
                    if (previewProgressBar.progress == previewProgressBar.max) {
                        previewKitProgressEngineExecutor?.shutdown()
                    }
                }

                //start engine to incrementing the progressbar
                previewKitProgressEngineExecutor?.scheduleAtFixedRate(
                    action,
                    0,
                    1000,
                    TimeUnit.MICROSECONDS
                )

            }
            setOnCompletionListener { mp ->

                previewKitProgressEngineExecutor?.shutdown()

                stopKitItem(view, previewProgressBar)
            }
        }

    }


}