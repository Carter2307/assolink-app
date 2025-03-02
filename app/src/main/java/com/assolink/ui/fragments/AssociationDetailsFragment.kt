package com.assolink.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assolink.R
import com.assolink.data.model.Association
import com.assolink.data.model.Event
import com.assolink.ui.viewmodels.AssociationDetailsViewModel
import com.assolink.ui.adapters.EventAdapter
import com.bumptech.glide.Glide

class AssociationDetailsFragment : Fragment() {

    private lateinit var viewModel: AssociationDetailsViewModel
    private lateinit var associationId: String

    private lateinit var nameTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var logoImageView: ImageView
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var emailButton: Button
    private lateinit var phoneButton: Button
    private lateinit var shareButton: Button

    private val eventAdapter = EventAdapter()

    companion object {
        fun newInstance(associationId: String): AssociationDetailsFragment {
            val fragment = AssociationDetailsFragment()
            val args = Bundle()
            args.putString("ASSOCIATION_ID", associationId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        associationId = arguments?.getString("ASSOCIATION_ID")
            ?: throw IllegalArgumentException("Association ID est requis")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_association_details, container, false)

        // Initialisation des vues
        nameTextView = view.findViewById(R.id.nameTextView)
        descriptionTextView = view.findViewById(R.id.descriptionTextView)
        addressTextView = view.findViewById(R.id.addressTextView)
        logoImageView = view.findViewById(R.id.logoImageView)
        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView)
        emailButton = view.findViewById(R.id.emailButton)
        phoneButton = view.findViewById(R.id.phoneButton)
        shareButton = view.findViewById(R.id.shareButton)

        // Configuration du RecyclerView
        eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventsRecyclerView.adapter = eventAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[AssociationDetailsViewModel::class.java]

        // Observer pour les détails de l'association
        viewModel.association.observe(viewLifecycleOwner) { association ->
            if (association != null) {
                updateUI(association)
            }
        }

        // Observer pour les événements
        viewModel.events.observe(viewLifecycleOwner) { events ->
            updateEvents(events)
        }
    }

    private fun updateUI(association: Association) {
        nameTextView.text = association.name
        descriptionTextView.text = association.description
        addressTextView.text = association.address

        Glide.with(this)
            .load(association.logoUrl)
            .placeholder(R.drawable.placeholder_logo)
            .error(R.drawable.error_logo)
            .into(logoImageView)

        // Configuration des boutons d'action
        emailButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${association.email}")
                putExtra(Intent.EXTRA_SUBJECT, "Contact via AssoLink")
            }
            startActivity(intent)
        }

        phoneButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${association.phone}")
            }
            startActivity(intent)
        }

        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Découvrez ${association.name}")
                putExtra(Intent.EXTRA_TEXT, "Je vous recommande l'association ${association.name}. " +
                        "Plus d'informations sur : https://assolink.fr/association/${association.id}")
            }
            startActivity(Intent.createChooser(shareIntent, "Partager via"))
        }
    }

    private fun updateEvents(events: List<Event>) {
        if (events.isEmpty()) {
            eventsRecyclerView.visibility = View.GONE
            view?.findViewById<TextView>(R.id.noEventsTextView)?.visibility = View.VISIBLE
        } else {
            eventsRecyclerView.visibility = View.VISIBLE
            view?.findViewById<TextView>(R.id.noEventsTextView)?.visibility = View.GONE
            eventAdapter.submitList(events)
        }
    }
}