package com.example.blacksquare.Items

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.example.blacksquare.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_kit.view.*

class KitItem(
    private val title: String,
    private val imageUrl: String,
    private val kitDescription: String,
    private val kitPrice: String,
    private val sale:Boolean,
    private val previewUrl: String,
    private val context: Context,
    private val clickListener: ItemClickListenerInterface

) : Item() {

    interface ItemClickListenerInterface{
        fun onPreviewKitItemClicked(
            view: View,
            previewUrl: String,
            previewProgress: ProgressBar
        )

    }
    override fun getLayout(): Int = R.layout.item_kit

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {

            viewHolder.itemView.kit_title.text = title
            viewHolder.itemView.kit_description.text = kitDescription
            viewHolder.itemView.kit_price.text = kitPrice
            val previewProgress = viewHolder.itemView.preview_progress
            val kitImageView = viewHolder.itemView.kit_image
            Glide.with(context).load(imageUrl).into(kitImageView)

            viewHolder.itemView.sale_badge.visibility = when(sale){true -> View.VISIBLE
                false -> View.INVISIBLE
            }


            viewHolder.itemView.preview_kit.setOnClickListener {
                clickListener.onPreviewKitItemClicked(it,previewUrl,previewProgress)
            }




        }
    }

    override fun getSpanSize(spanCount: Int, position: Int) = spanCount / 2
}