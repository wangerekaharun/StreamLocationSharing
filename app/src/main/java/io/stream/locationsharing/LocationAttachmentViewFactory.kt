package io.stream.locationsharing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.getstream.sdk.chat.adapter.MessageListItem
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import io.getstream.chat.android.ui.message.list.MessageListItemStyle
import io.getstream.chat.android.ui.message.list.adapter.MessageListListenerContainer
import io.getstream.chat.android.ui.message.list.adapter.viewholder.attachment.AttachmentViewFactory
import io.stream.locationsharing.databinding.LocationAttachementViewBinding

class LocationAttachmentViewFactory: AttachmentViewFactory(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap
    private var currentLocation = LatLng(0.0,0.0)

    override fun createAttachmentView(
        data: MessageListItem.MessageItem,
        listeners: MessageListListenerContainer,
        style: MessageListItemStyle,
        parent: ViewGroup
    ): View {
        val location = data.message.attachments.find { it.type == "location" }
        return if (location != null) {
            createLocationView(parent, location.extraData["location"] as LatLng)
        } else {
            super.createAttachmentView(data, listeners, style, parent)
        }

    }

    private fun createLocationView(parent: ViewGroup, location: LatLng): View {
        currentLocation = location
        val binding = LocationAttachementViewBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        mapView = binding.mapView
        mapView.getMapAsync(this)

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setMinZoomPreference(12f)
        map.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
    }
}