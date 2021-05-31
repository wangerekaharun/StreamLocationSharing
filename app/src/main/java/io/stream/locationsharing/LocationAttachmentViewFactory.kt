package io.stream.locationsharing

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
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

class LocationAttachmentViewFactory(lifecycleOwner: LifecycleOwner): AttachmentViewFactory(), OnMapReadyCallback, LifecycleObserver {
    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap
    private var currentLocation = LatLng(0.0,0.0)

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

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
        currentLocation = location
        val binding = LocationAttachementViewBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        mapView = binding.mapView
        mapView.onCreate(Bundle())
        mapView.getMapAsync(this)

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setMinZoomPreference(18f)
        map.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroyMapView(){
        Log.d("LifecycleState","Yes we are detached")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun startMapView(){
        Log.d("LifecycleState","Yes we are started")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resumeMapView(){
        Log.d("LifecycleState","Yes we are resumed")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopMapView(){
        Log.d("LifecycleState","Yes we are stopped")
    }

}