// ui/adapters/EventAdapter.kt
package com.assolink.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.assolink.data.model.Event
import com.assolink.databinding.ItemEventBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventAdapter(
    private var events: List<Event> = listOf(),
    private val onEventClick: (Event) -> Unit,
    private val onRegisterClick: ((Event) -> Unit)? = null
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]

        with(holder.binding) {
            eventTitleTextView.text = event.title

            // Formater la date
            val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
            eventDateTextView.text = dateFormat.format(Date(event.startDate))

            eventLocationTextView.text = event.address

            // Configuration des clics
            root.setOnClickListener { onEventClick(event) }

            // Si le bouton d'inscription est présent, configurer son action
            registerButton?.setOnClickListener {
                onRegisterClick?.invoke(event)
            }
        }
    }

    override fun getItemCount(): Int = events.size

    fun updateEvents(newEvents: List<Event>) {
        this.events = newEvents
        notifyDataSetChanged()
    }
}