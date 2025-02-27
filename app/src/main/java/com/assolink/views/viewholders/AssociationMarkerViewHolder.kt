package com.assolink.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.assolink.R
import com.bumptech.glide.Glide

class AssociationMarkerViewHolder(private val view: View) {
    private val nameTextView: TextView = view.findViewById(R.id.markerNameTextView)
    private val logoImageView: ImageView = view.findViewById(R.id.markerLogoImageView)

    fun bind(name: String, logoUrl: String) {
        nameTextView.text = name

        Glide.with(view.context)
            .load(logoUrl)
            .placeholder(R.drawable.placeholder_logo)
            .error(R.drawable.error_logo)
            .circleCrop()
            .into(logoImageView)
    }
}