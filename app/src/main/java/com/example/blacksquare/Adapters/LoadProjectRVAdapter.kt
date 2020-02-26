package com.example.blacksquare.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blacksquare.R
import kotlinx.android.synthetic.main.load_sound_list_item.view.*

class LoadProjectRVAdapter (val items: ArrayList<String>, val context: Context) :
    RecyclerView.Adapter<LoadProjectRVAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: LoadProjectRVAdapter.ViewHolder, position: Int) {
        holder.soundTitle.text = items[position]
    }


    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {


        val soundTitle = view.sound_title
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.load_sound_list_item, parent, false)


        return ViewHolder(view)

    }

    override fun getItemCount(): Int {

        return items.size
    }


}