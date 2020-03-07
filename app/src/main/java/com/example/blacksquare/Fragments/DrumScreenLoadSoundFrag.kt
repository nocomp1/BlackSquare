package com.example.blacksquare.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blacksquare.Adapters.LoadSoundRvAdapter
import com.example.blacksquare.R

class DrumScreenLoadSoundFrag : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewAdapter = LoadSoundRvAdapter(myListArray, activity!!.applicationContext)
        recyclerView = view.findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)


        val divider = DividerItemDecoration(context,DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(divider)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = viewAdapter


    }

    private var myListArray: Array<String> = arrayOf("1", "2", "3", "4", "5")
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<LoadSoundRvAdapter.ViewHolder>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.drum_screen_load_kit_layout, container, false)


        return view
    }


}