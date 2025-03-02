package com.assolink.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.assolink.R
import com.assolink.data.model.Association
import com.assolink.data.model.Event
import com.assolink.databinding.FragmentAssociationDetailsBinding
import com.assolink.ui.adapters.EventAdapter
import com.assolink.ui.viewmodels.AssociationDetailsViewModel
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class AssociationDetailsFragment : Fragment() {

    private var _binding: FragmentAssociationDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AssociationDetailsViewModel by viewModel()
    private lateinit var eventAdapter: EventAdapter
    private lateinit var associationId: String

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
    ): View {
        _binding = FragmentAssociationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()

        // Charger les détails de l'association
        viewModel.loadAssociationDetails(associationId)
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter(
            onEventClick = { /* Navigation vers les détails de l'événement */ },
            onRegisterClick = { /* Inscription à l'événement */ }
        )

        binding.eventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventAdapter
        }
    }

    private fun setupObservers() {
        viewModel.association.observe(viewLifecycleOwner) { association ->
            association?.let { updateUI(it) }
        }

        viewModel.events.observe(viewLifecycleOwner) { events ->
            updateEvents(events)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setAction("Réessayer") { viewModel.loadAssociationDetails(associationId, true) }
                    .show()
                viewModel.clearError()
            }
        }
    }

    private fun updateUI(association: Association) {
        with(binding) {
            nameTextView.text = association.name
            descriptionTextView.text = association.description
            addressTextView.text = association.address

            Glide.with(requireContext())
                .load(association.imageUrl)
                .placeholder(R.drawable.placeholder_logo)
                .error(R.drawable.error_logo)
                .into(logoImageView)

            // Configuration des boutons d'action
            emailButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:${association.contactEmail}")
                    putExtra(Intent.EXTRA_SUBJECT, "Contact via AssoLink")
                }
                startActivity(intent)
            }

            phoneButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${association.contactPhone}")
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
    }

    private fun updateEvents(events: List<Event>) {
        if (events.isEmpty()) {
            binding.eventsRecyclerView.visibility = View.GONE
            binding.noEventsTextView.visibility = View.VISIBLE
        } else {
            binding.eventsRecyclerView.visibility = View.VISIBLE
            binding.noEventsTextView.visibility = View.GONE
            eventAdapter.updateEvents(events)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}