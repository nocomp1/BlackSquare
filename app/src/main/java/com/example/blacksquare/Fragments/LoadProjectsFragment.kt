package com.example.blacksquare.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blacksquare.Adapters.LoadProjectRVAdapter
import com.example.blacksquare.R
import com.example.blacksquare.Utils.ProjectJsonObjects
import com.example.blacksquare.Utils.ProjectJsonUtils

class LoadProjectsFragment : Fragment() {
    private lateinit var projectWriteJson: ProjectJsonUtils
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpData()
        viewAdapter = LoadProjectRVAdapter(myListArray, activity!!.applicationContext)
        recyclerView = view.findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        val divider =
            DividerItemDecoration(activity!!.applicationContext, DividerItemDecoration.HORIZONTAL)
        recyclerView.addItemDecoration(divider)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = viewAdapter
    }

    private fun setUpData() {
//        //write some project details to file
        projectWriteJson = ProjectJsonUtils(activity!!.applicationContext)
//        val projectTree = projectWriteJson.getObject()
//        //Create project details
//        val projectDetails = ProjectJsonObjects.ProjectDetails()
//        projectDetails.name = "Project 2"
//        projectTree.project.projectList.add(projectDetails)
//        projectWriteJson.writeToFile(projectTree)

        //Retrieve some project info from file
        val getProjectJsonFile = projectWriteJson.getObject()
        val projectList = getProjectJsonFile.project.projectList
        //add to the list
        for (key in projectList.indices) {
            myListArray.add(projectList[key].name)
        }


    }

    private var myListArray: ArrayList<String> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<LoadProjectRVAdapter.ViewHolder>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.drum_screen_load_kit_layout, container, false)

        return view
    }


}