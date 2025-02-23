package com.assolink.views.assoCard

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.assolink.R

class AssoCardViewHolder(itemView: View
): RecyclerView.ViewHolder(itemView) {
    val cover: ImageView = itemView.findViewById(R.id.assoc_card_image)
    val name: TextView = itemView.findViewById(R.id.assoc_card_title)
}