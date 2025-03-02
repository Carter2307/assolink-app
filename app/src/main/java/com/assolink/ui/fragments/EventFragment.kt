package com.assolink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.assolink.databinding.FragmentEventBinding
import com.assolink.ui.adapters.EventAdapter
import com.assolink.ui.viewmodels.EventsViewModel
import com.assolink.ui.viewmodels.RegistrationStatus
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class EventFragment : Fragment() {

    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EventsViewModel by viewModel()
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupListeners()

        // Charger les événements
        viewModel.loadUpcomingEvents()
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter(
            onEventClick = { event ->
                // Naviguer vers les détails de l'événement
                // Par exemple: findNavController().navigate(...)

            },
            onRegisterClick = { event ->
                viewModel.registerForEvent(event.id, event.associationId)
            }
        )

        binding.eventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventAdapter
        }
    }

    private fun setupObservers() {
        viewModel.events.observe(viewLifecycleOwner) { events ->
            if (events.isEmpty()) {
                binding.emptyStateLayout.visibility = View.VISIBLE
                binding.eventsRecyclerView.visibility = View.GONE
            } else {
                binding.emptyStateLayout.visibility = View.GONE
                binding.eventsRecyclerView.visibility = View.VISIBLE
                eventAdapter.updateEvents(events)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.registrationStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is RegistrationStatus.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is RegistrationStatus.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, "Opération réussie", Snackbar.LENGTH_SHORT).show()
                }
                is RegistrationStatus.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, status.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadUpcomingEvents(forceRefresh = true)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}