package com.example.nextsoundz.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nextsoundz.R
import kotlinx.android.synthetic.main.load_sound_list_item.view.*

class LoadKitRvAdapter(val items: Array<String>, val context: Context) :
    RecyclerView.Adapter<LoadKitRvAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: LoadKitRvAdapter.ViewHolder, position: Int) {
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