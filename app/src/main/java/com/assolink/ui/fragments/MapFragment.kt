package com.assolink.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import com.assolink.R
import com.assolink.data.model.Association
import com.assolink.databinding.FragmentMapBinding
import com.assolink.ui.activities.AssociationDetailsActivity
import com.assolink.ui.viewmodels.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleMap: GoogleMap
    private val viewModel: MapViewModel by viewModel()

    private val categories = listOf("Sport", "Culture", "Environnement", "Éducation", "Solidarité")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryChips()
        setupSearchView()
        setupObservers()
        setupRefreshButton()
    }

    private fun setupCategoryChips() {
        // Créer les chips pour les catégories
        categories.forEach { category ->
            val chip = layoutInflater.inflate(
                R.layout.item_category_chip,
                binding.categoryChipGroup,
                false
            ) as Chip

            chip.text = category
            chip.setOnClickListener {
                if (chip.isChecked) {
                    viewModel.setCategory(category)
                } else {
                    viewModel.setCategory(null)
                }
            }

            binding.categoryChipGroup.addView(chip)
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.setSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    viewModel.clearSearch()
                }
                return true
            }
        })

        binding.searchView.setOnCloseListener {
            viewModel.clearSearch()
            false
        }
    }

    private fun setupObservers() {
        viewModel.associations.observe(viewLifecycleOwner) { associations ->
            associations?.let {
                displayMarkers(it)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setAction("Réessayer") { viewModel.loadAssociations(true) }
                    .show()
                viewModel.clearError()
            }
        }
    }

    private fun setupRefreshButton() {
        binding.fabRefresh.setOnClickListener {
            viewModel.loadAssociations(true)
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

        // Position initiale sur la France
        val france = LatLng(46.227638, 2.213749)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(france, 5f))

        viewModel.associations.value?.let { displayMarkers(it) }

        googleMap.setOnMarkerClickListener { marker ->
            val associationId = marker.tag as String
            navigateToDetails(associationId)
            true
        }
    }

    private fun displayMarkers(associations: List<Association>) {
        googleMap.clear()

        associations.forEach { association ->
            association.location?.let { geoPoint ->
                val position = LatLng(geoPoint.latitude, geoPoint.longitude)
                val markerOptions = MarkerOptions()
                    .position(position)
                    .title(association.name)

                val marker = googleMap.addMarker(markerOptions)
                marker?.tag = association.id
            }
        }

        // Si nous avons des résultats, zoomer sur le premier
        if (associations.isNotEmpty() && associations[0].location != null) {
            val firstAssociation = associations[0]
            val position = LatLng(
                firstAssociation.location!!.latitude,
                firstAssociation.location!!.longitude
            )
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12f))
        }
    }

    private fun navigateToDetails(associationId: String) {
        val intent = Intent(requireContext(), AssociationDetailsActivity::class.java).apply {
            putExtra("ASSOCIATION_ID", associationId)
        }
        startActivity(intent)
    }

    // Gestion du cycle de vie de MapView
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
}