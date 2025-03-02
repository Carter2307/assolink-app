package com.assolink.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.assolink.R
import com.assolink.data.model.Event
import java.text.SimpleDateFormat
import java.util.Locale

class EventAdapter : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    private var events: List<Event> = emptyList()

    fun submitList(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount() = events.size

    class EventViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: android.widget.TextView = view.findViewById(R.id.eventTitleTextView)
        private val dateTextView: android.widget.TextView = view.findViewById(R.id.eventDateTextView)
        private val locationTextView: android.widget.TextView = view.findViewById(R.id.eventLocationTextView)
        private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        fun bind(event: Event) {
            titleTextView.text = event.title
            dateTextView.text = dateFormat.format(event.date)
            locationTextView.text = event.location
        }
    }
}