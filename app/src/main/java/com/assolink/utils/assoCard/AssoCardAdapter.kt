package com.assolink.utils.assoCard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.assolink.R
import com.bumptech.glide.Glide

class AssoCardAdapter(val assoList: List<Asso>): RecyclerView.Adapter<AssoCardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssoCardViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val assoCarView = inflater.inflate(R.layout.asso_card, parent, false)
        return AssoCardViewHolder(assoCarView)
    }

    override fun getItemCount(): Int {
        return assoList.size
    }

    override fun onBindViewHolder(holder: AssoCardViewHolder, position: Int) {
        val card: Asso = assoList[position];
        val assoName = holder.name
        assoName.text = card.name


        Glide.with(holder.itemView.context)
            .load(card.cover)
            .into(holder.cover)

    }

}