# Location Sharing With Custom Attachments 



## Introduction

Stream's Android SDK supports sending custom attachements with messages. In this tutorial you'll learn how to send location as a custom attachment.

**Note:** This tutorial assumes you already know the basic knowledge on the Stream API's. To get started checkout the [Android Chat Messaging Tutorial](https://getstream.io/tutorials/android-chat/#kotlin). To get started with sending custom attachements with the Stream Chat SDK checkout the [Creating Custom Attachments on Android](https://getstream.io/blog/android-chat-custom-attachments/) tutorial.

## Adding Location as a Custom Attachement

To send add location to your custom attachment, you need to create an <code>Attachment</code> object as shown below.

```Kotlin
// 1
val attachment = Attachment(
    type = "location",
    extraData = mutableMapOf("latitude" to "-1.3754604377993476", "longitude" to "36.71737641378712"),
)

// 2
val message = Message(
    cid = channelId,
    text = "My current location",
    attachments = mutableListOf(attachment),
)
```

To explan what the code above does:

1. Here, you're creating an attachement with the <code>location</code> key which you'll use later to retrieve this data. You're also passing the latitude and longitude for your location as extra data which you'll be retreiving later on.
2. You add your location attachment to your message on the <code>attachments</code> property.



With that you can send your message with this custom attachement.

Streams Android SDK renders preview for attachements like images and files. For custom attachements, you''ll override the <code>AttachmentViewFactory</code> class which has a method to create your custom view. You'll be seeing how to do that in the next section.

## Adding A Map Preview

This is the layout for your custom attachement:

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="io.stream.locationsharing.LocationSharingActivity">

    <com.google.android.gms.maps.MapView
        android:id="@+id/mapView"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

The layour has a <code>MapView</code> for displaying the location on the map.

Next you'll create the <code>LocationAttachmentViewFactory</code> which overrides <code>AttachmentViewFactory</code> . This is how the class looks like:

```kotlin
class LocationAttachmentViewFactory: AttachmentViewFactory(), OnMapReadyCallback {
    // 1
    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap
    private var currentLocation = LatLng(0.0,0.0)

    // 2 
  	override fun createAttachmentView(
        data: MessageListItem.MessageItem,
        listeners: MessageListListenerContainer,
        style: MessageListItemStyle,
        parent: ViewGroup
    ): View {
      // 3
        val location = data.message.attachments.find { it.type == "location" }
        return if (location != null) {
            val lat = location.extraData["latitude"] as String
            val long = location.extraData["longitude"] as String
            val latLng = LatLng(lat.toDouble(), long.toDouble())
            // 4
            createLocationView(parent, latLng)
        } else {
            super.createAttachmentView(data, listeners, style, parent)
        }

    }

    private fun createLocationView(parent: ViewGroup, location: LatLng): View {
        currentLocation = location
        val binding = LocationAttachementViewBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        // 5
        mapView = binding.mapView
        mapView.onCreate(Bundle())
        mapView.getMapAsync(this)

        return binding.root
    }

    // 6
  	override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setMinZoomPreference(12f)
        map.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
    }
}
```

Here's a breakdown of the code above:

1. This are top level variables for your map view, google map and location that you get from the attachement.
2. This the method responsible for rendering your custom attachement UI
3. Here, you're getting the location data that you passed on your message using the <code>location</code>key that you defined earlier.
4. You're calling the <code>createLocationView</code> which is responsible for inflating the view.
5.  Here you're initializing the <code>mapView</code> and also calling the <code>getMapAsync()</code>
6. Youre overriding the <code>oNMapReady</code> method which is called when the map is loaded. In this method you update the map to show the location you added on your attachment.

With the custom factory set, you now need to notify the <code>MessageListView</code> of the <code>LocationAttachmentViewFactory</code>. You do this as shown in the code below:

```kotlin
binding.messageListView.setAttachmentViewFactory(LocationAttachmentViewFactory())
```

Here's you're adding the attachment view factory to the MessageListView.

With this, your app is ready to send and also preview custom location attachement.



<img src="/Users/harun/AndroidStudioProjects/StreamLocationSharing/images/location_attachment.png" alt="location_attachment" style="zoom:50%;" />



As seen from the image above, the attachmennt shows a map and a text. The map shows the location of the coordinates sent as custom attachments.

## Conclusion

You've seen how easy it is to add location as custom attachment. You can now enrich your chat with location sharing. You can even go a step further to add features such as live location sharing or current location sharing as the Stream Android SDK supports custom attachments.

You can get the full sample project with examples in this tutorial [here](https://github.com/wangerekaharun/StreamLocationSharing).

