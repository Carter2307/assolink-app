package com.assolink.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.assolink.R
import com.assolink.data.model.Association
import com.assolink.databinding.ItemAssociationCardBinding
import com.bumptech.glide.Glide

class AssociationAdapter(
    private var associations: List<Association> = listOf(),
    private val onAssociationClick: (Association) -> Unit
) : RecyclerView.Adapter<AssociationAdapter.AssociationViewHolder>() {

    class AssociationViewHolder(val binding: ItemAssociationCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssociationViewHolder {
        val binding = ItemAssociationCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AssociationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssociationViewHolder, position: Int) {
        val association = associations[position]

        with(holder.binding) {
            associationNameTextView.text = association.name
            categoryTextView.text = association.category

            // Charger l'image avec Glide
            Glide.with(root.context)
                .load(association.imageUrl)
                .placeholder(R.drawable.placeholder_logo)
                .error(R.drawable.error_logo)
                .into(associationImageView)

            // Configuration du clic
            root.setOnClickListener { onAssociationClick(association) }
        }
    }

    override fun getItemCount(): Int = associations.size

    fun updateAssociations(newAssociations: List<Association>) {
        this.associations = newAssociations
        notifyDataSetChanged()
    }
}