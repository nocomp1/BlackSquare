package com.example.blacksquare.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class DrumScreenLoadKitFrag : Fragment() {
    private lateinit var viewModel: DrumScreenLoadKitViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.setHasFixedSize(true)

        this.let { viewModel = ViewModelProviders.of(it).get(DrumScreenLoadKitViewModel :: class.java) }

        //TODO MAKE A NETWORK CALL TO FETCH KIT DATA
        viewModel.fetchKitData()



        viewModel.viewState.observe(this, Observer {

            initRecyclerView(it.kitList.toRecyclerListItem())
        })



//        val listOfKitObjects = listOf(
//            "Strawberry Hi Hats",
//            "Black Tar 120 degrees",
//            "Smoke out the back windows",
//            "Not my kid Not my problem"
//        )
//        getKitRequest()
        val listOfKitObjects  = mutableListOf<Kit>()
       listOfKitObjects.add(Kit("Strawberry Hi Hats","https://www.bigfishaudio.com/prodpictssm/smonsd132.jpg",
            "The hardest hitting 808 on the planet","$4.99"))
        listOfKitObjects.add(Kit("Black Tar 120 degrees","https://www.bigfishaudio.com/prodpictssm/smonsd132.jpg",
            "The hardest hitting 808 on the planet","$4.99"))
        listOfKitObjects.add(Kit("Smoke out the back windows","https://www.bigfishaudio.com/prodpictssm/smonsd132.jpg",
            "The hardest hitting 808 on the planet","$4.99"))
        listOfKitObjects.add(Kit("Not my kid Not my problem","https://www.bigfishaudio.com/prodpictssm/smonsd132.jpg",
            "The hardest hitting 808 on the planet","$4.99"))

     val listOfKit = listOfKitObjects.toTypedArray()
        listOfKit.get(0)

       // initRecyclerView(listOfKitObjects.toRecyclerListItem())


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
    private fun ArrayList<Kit>.toRecyclerListItem(): List<KitItem> {
        this.forEach {
            it.
        }

        return this.map { text ->
            KitItem()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.drum_screen_load_kit_layout, container, false)


        return view
    }


}

private var myListArray: Array<String> = arrayOf("1", "2", "3", "4", "5")
private lateinit var recyclerView: RecyclerView
