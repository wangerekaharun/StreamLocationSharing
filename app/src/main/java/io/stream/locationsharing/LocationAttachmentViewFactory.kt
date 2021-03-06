package io.stream.locationsharing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.getstream.sdk.chat.adapter.MessageListItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import io.getstream.chat.android.ui.message.list.MessageListItemStyle
import io.getstream.chat.android.ui.message.list.adapter.MessageListListenerContainer
import io.getstream.chat.android.ui.message.list.adapter.viewholder.attachment.AttachmentViewFactory
import io.stream.locationsharing.databinding.LocationAttachementViewBinding

class LocationAttachmentViewFactory(
    private val lifecycleOwner: LifecycleOwner
) : AttachmentViewFactory() {
    override fun createAttachmentView(
        data: MessageListItem.MessageItem,
        listeners: MessageListListenerContainer,
        style: MessageListItemStyle,
        parent: ViewGroup
    ): View {
        val location = data.message.attachments.find { it.type == "location" }
        return if (location != null) {
            val lat = location.extraData["latitude"] as Double
            val long = location.extraData["longitude"] as Double
            val latLng = LatLng(lat, long)
            createLocationView(parent, latLng)
        } else {
            super.createAttachmentView(data, listeners, style, parent)
        }
    }

    private fun createLocationView(parent: ViewGroup, location: LatLng): View {
        val binding = LocationAttachementViewBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        val mapView = binding.mapView
        mapView.onCreate(Bundle())
        mapView.getMapAsync { googleMap ->
            googleMap.setMinZoomPreference(18f)
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        }

        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun destroyMapView() {
                mapView.onDestroy()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun startMapView() {
                mapView.onStart()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun resumeMapView() {
                mapView.onResume()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun stopMapView() {
                mapView.onStop()
            }
        })

        return binding.root
    }
}