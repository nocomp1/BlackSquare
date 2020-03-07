package com.example.blacksquare.Items

import com.example.blacksquare.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

import kotlinx.android.synthetic.main.item_kit.view.*

class KitItem(
    private val title: String,
    private val imageUrl: String,
    private val kitDescription: String,
    private val kitPrice: String
) : Item() {

    override fun getLayout(): Int = R.layout.item_kit

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            viewHolder.itemView.kit_title.text = title
            viewHolder.itemView.kit_description.text = kitDescription
            viewHolder.itemView.kit_price.text = kitPrice
            val kitImageView = viewHolder.itemView.kit_image
            Picasso.get().load(imageUrl).into(kitImageView);
        }
    }

    override fun getSpanSize(spanCount: Int, position: Int) = spanCount / 2
}