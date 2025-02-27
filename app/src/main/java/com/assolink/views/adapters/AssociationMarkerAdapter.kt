package com.assolink.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.assolink.R
import com.assolink.data.model.association.Association
import com.assolink.viewholders.AssociationMarkerViewHolder
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator

class AssociationMarkerAdapter(
    private val context: Context,
    private val map: GoogleMap
) {
    private val clusterManager: ClusterManager<AssociationClusterItem>

    init {
        clusterManager = ClusterManager(context, map)
        map.setOnCameraIdleListener(clusterManager)

        val renderer = AssociationMarkerRenderer(context, map, clusterManager)
        clusterManager.renderer = renderer
    }

    fun setItems(associations: List<Association>) {
        clusterManager.clearItems()
        associations.forEach { association ->
            val item = AssociationClusterItem(
                association.id,
                association.name,
                association.latitude,
                association.longitude,
                association.logoUrl
            )
            clusterManager.addItem(item)
        }
        clusterManager.cluster()
    }

    fun setOnMarkerClickListener(listener: (String) -> Unit) {
        clusterManager.setOnClusterItemClickListener { item ->
            listener(item.id)
            true
        }
    }

    // Classe interne pour les éléments de cluster
    class AssociationClusterItem(
        val id: String,
        private val name: String, // Changé de title à name
        val lat: Double,
        val lng: Double,
        val logoUrl: String
    ) : ClusterItem {
        override fun getPosition() = com.google.android.gms.maps.model.LatLng(lat, lng)
        override fun getTitle(): String = name // Utilise name au lieu de title
        override fun getSnippet(): String = ""
        override fun getZIndex(): Float? = null
    }

    private inner class AssociationMarkerRenderer(
        context: Context,
        map: GoogleMap,
        clusterManager: ClusterManager<AssociationClusterItem>
    ) : DefaultClusterRenderer<AssociationClusterItem>(context, map, clusterManager) {

        private val iconGenerator = IconGenerator(context)
        private val markerView: View = LayoutInflater.from(context).inflate(R.layout.item_map_marker, null)
        private val viewHolder = AssociationMarkerViewHolder(markerView)

        init {
            iconGenerator.setContentView(markerView)
        }

        override fun onBeforeClusterItemRendered(
            item: AssociationClusterItem,
            markerOptions: MarkerOptions
        ) {
            viewHolder.bind(item.getTitle(), item.logoUrl)

            val icon = iconGenerator.makeIcon()
            markerOptions
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                .title(item.getTitle())
        }

        override fun onClusterItemRendered(clusterItem: AssociationClusterItem, marker: Marker) {
            marker.tag = clusterItem.id
        }
    }
}