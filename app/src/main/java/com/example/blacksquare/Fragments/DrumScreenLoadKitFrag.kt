package com.example.blacksquare.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blacksquare.Adapters.LoadKitRvAdapter

import com.example.blacksquare.R

class DrumScreenLoadKitFrag : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewAdapter = LoadKitRvAdapter(myListArray, activity!!.applicationContext)
        recyclerView = view.findViewById(R.id.load_kit_rv) as RecyclerView
        recyclerView.layoutManager = GridLayoutManager(activity,2)

        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = viewAdapter
    }

    private var myListArray: Array<String> = arrayOf("1", "2", "3", "4", "5")
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<LoadKitRvAdapter.ViewHolder>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.drum_screen_load_kit_layout, container, false)


        return view
    }


}
