package com.assolink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.assolink.databinding.FragmentHomeBinding
import com.assolink.ui.adapters.AssociationAdapter
import com.assolink.ui.adapters.EventAdapter
import com.assolink.ui.viewmodels.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var favoriteAssociationsAdapter: AssociationAdapter
    private lateinit var upcomingEventsAdapter: EventAdapter
    private lateinit var recommendedAssociationsAdapter: AssociationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupObservers()
        setupSearchBar()

        // Charger les données
        viewModel.loadHomeData()
    }

    private fun setupRecyclerViews() {
        // Adapter pour les associations favorites
        favoriteAssociationsAdapter = AssociationAdapter(
            onAssociationClick = { association ->
                navigateToAssociationDetails(association.id)
            }
        )

        binding.favoriteAssociationsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = favoriteAssociationsAdapter
        }

        // Adapter pour les événements à venir
        upcomingEventsAdapter = EventAdapter(
            onEventClick = { event ->
                // Navigation vers les détails de l'événement
                Snackbar.make(binding.root, "Événement: ${event.title}", Snackbar.LENGTH_SHORT).show()
            },
            onRegisterClick = { event ->
                viewModel.registerForEvent(event.id, event.associationId)
            }
        )

        binding.upcomingEventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = upcomingEventsAdapter
        }

        // Adapter pour les associations recommandées
        recommendedAssociationsAdapter = AssociationAdapter(
            onAssociationClick = { association ->
                navigateToAssociationDetails(association.id)
            }
        )

        binding.recommendedAssociationsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recommendedAssociationsAdapter
        }
    }

    private fun setupObservers() {
        // Observer pour les associations favorites
        viewModel.favoriteAssociations.observe(viewLifecycleOwner) { associations ->
            if (associations.isEmpty()) {
                binding.favoriteAssociationsSection.visibility = View.GONE
            } else {
                binding.favoriteAssociationsSection.visibility = View.VISIBLE
                favoriteAssociationsAdapter.updateAssociations(associations)
            }
        }

        // Observer pour les événements à venir
        viewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            if (events.isEmpty()) {
                binding.upcomingEventsSection.visibility = View.GONE
            } else {
                binding.upcomingEventsSection.visibility = View.VISIBLE
                upcomingEventsAdapter.updateEvents(events)
            }
        }

        // Observer pour les associations recommandées
        viewModel.recommendedAssociations.observe(viewLifecycleOwner) { associations ->
            if (associations.isEmpty()) {
                binding.recommendedAssociationsSection.visibility = View.GONE
            } else {
                binding.recommendedAssociationsSection.visibility = View.VISIBLE
                recommendedAssociationsAdapter.updateAssociations(associations)
            }
        }

        // Observer pour le chargement
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observer pour les erreurs
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setAction("Réessayer") { viewModel.loadHomeData(true) }
                    .show()
                viewModel.clearError()
            }
        }
    }

    private fun setupSearchBar() {
        binding.searchBar.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Naviguer vers la recherche ou filtrer les résultats
                Snackbar.make(binding.root, "Recherche: $query", Snackbar.LENGTH_SHORT).show()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Optionnel: filtrer en temps réel
                return false
            }
        })
    }

    private fun navigateToAssociationDetails(associationId: String) {
        // Naviguer vers les détails de l'association
        val intent = android.content.Intent(requireContext(), com.assolink.ui.activities.AssociationDetailsActivity::class.java)
        intent.putExtra("ASSOCIATION_ID", associationId)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}