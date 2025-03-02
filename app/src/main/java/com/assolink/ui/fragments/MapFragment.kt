package com.assolink.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.assolink.R
import com.assolink.data.model.Association
import com.assolink.ui.activities.AssociationDetailsActivity
import com.assolink.ui.viewmodels.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var viewModel: MapViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        viewModel = ViewModelProvider(requireActivity())[MapViewModel::class.java]
        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true

        viewModel.associations.observe(viewLifecycleOwner) { associations ->
            associations?.let {
                displayMarkers(it)
            }
        }

        // Position initiale sur la France
        val france = LatLng(46.227638, 2.213749)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(france, 5f))
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun displayMarkers(associations: List<Association>) {
        associations.forEach { association ->
            val position = LatLng(association.latitude, association.longitude)
            val markerOptions = MarkerOptions()
                .position(position)
                .title(association.name)

            val marker = googleMap.addMarker(markerOptions)
            marker?.tag = association.id
        }

        googleMap.setOnMarkerClickListener { marker ->
            val associationId = marker.tag as String
            navigateToDetails(associationId)
            true
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
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}