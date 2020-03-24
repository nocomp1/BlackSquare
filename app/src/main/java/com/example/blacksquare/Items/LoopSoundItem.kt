package com.example.blacksquare.Items

import android.view.View
import com.example.blacksquare.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.load_sound_list_item.view.*

class LoopSoundItem(
    private val clickListener: ItemClickListenerInterface,
    private val title: String,
    private val fileLocation: String
) : Item() {

    interface ItemClickListenerInterface {
        fun onPreviewLoopItemClicked(
            fileLocation: String,
            title: String,view: View
        )

        fun onLoadSoundItemClicked(fileLocation: String, title: String,view: View)

    }

    override fun getLayout(): Int = R.layout.load_sound_list_item

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {

            viewHolder.itemView.sound_title.text = title

            viewHolder.itemView.sound_preview.setOnClickListener {
                clickListener.onPreviewLoopItemClicked(fileLocation, title,it)
            }

            viewHolder.itemView.load_sound.setOnClickListener {
                clickListener.onLoadSoundItemClicked(fileLocation, title,it)
            }


        }
    }


}