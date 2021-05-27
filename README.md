# Location Sharing With Custom Attachments 


Stream's Android SDK supports sending custom attachments with messages. In this tutorial, you'll learn how to send location as a custom attachment. You'll be learning this by building an app that shares your current location as an attachment to a message.

**Note:** This tutorial assumes you already know the basic knowledge of the Stream API. To get started, check out the [Android In-App Messaging Tutorial](https://getstream.io/tutorials/android-chat/), and take a look at the [Android SDK on GitHub](https://github.com/GetStream/stream-chat-android). To get started with sending custom attachments with the Stream Chat SDK check out the [Creating Custom Attachments on Android](https://getstream.io/blog/android-chat-custom-attachments/) tutorial.

## Getting Current Location

Before you send your attachment, first you'll need to get the current location of the user. This logic for getting the current location has already been set up for you in the [sample project]((https://github.com/wangerekaharun/StreamLocationSharing)). 

There's the <code>LocationUtils</code> file which has a method with extends the <code>FusedLocationProviderClient</code> class. The extension method returns a <code>callbackFlow</code> with the location data.

To get the location you collect the results on your activity as shown below:

```kotlin
lifecycleScope.launch {
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        fusedLocationClient.locationFlow().collect {
            currentLocation = LatLng(it.latitude, it.longitude)
        }
    }
}
```

Here you getting location from <code>FusedLocationProviderClient</code> as a <code>Flow</code> using  the <code>locationFlow()</code> extension method.  You're collecting the results in a safe way using the <code>Lifecycle></code> methods.

Now you have the current user location, in the next section, you'll see how to add location coordinates as custom attachments.

## Adding Location as a Custom Attachments

To add the location to your custom attachment, you need to create an <code>Attachment</code> object as shown below.

```Kotlin
// 1
val attachment = Attachment(
    type = "location",
    extraData = mutableMapOf("latitude" to currentLocation.latitude, "longitude" to currentLocation.longitude),
)

// 2
val message = Message(
    cid = channelId,
    text = "My current location",
    attachments = mutableListOf(attachment),
)
```

To explain what the code above does:

1. Here, you're creating an attachment with the <code>location</code> key which you'll use later to retrieve this data. You're also passing the latitude and longitude from your location coordinates as extra data which you'll be retrieving later on.
2. You add your location attachment to your message on the <code>attachments</code> property.



With this, you can send your message with this custom attachment.

Streams Android SDK renders preview for attachments like images and files. For custom attachments, you'll override the <code>AttachmentViewFactory</code> class which has a method to create your custom view. You'll be seeing how to do that in the next section.

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

The layout has a <code>MapView</code> for displaying the location on the map.

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

1. This are top level variables for your <code>MapView</code>, <code>GoogleMap</code> and a <code>LatLng</code> object for location that you get from the attachement.
2. This the method responsible for rendering your custom attachment UI. In this method, you only need to change the UI for attachments that have a location. The other attachments remain unchanged.
3. Here, you're getting the location data that you passed on your message using the <code>location</code>key that you defined earlier. There'
4. You're calling the <code>createLocationView</code> which is responsible for inflating the view.
5.  Here you're initializing the <code>mapView</code> and also calling the <code>getMapAsync()</code> which sets a callback object which will be triggered when the GoogleMap instance is ready to be used by our class.
6. You're overriding the <code>onMapReady</code> method which is called when the map is ready to be used. In this method you update the map to show the location you added to your attachment.

With the custom factory set, you now need to notify the <code>MessageListView</code> of the <code>LocationAttachmentViewFactory</code>. You do this as shown in the code below:

```kotlin
binding.messageListView.setAttachmentViewFactory(LocationAttachmentViewFactory())
```

Here's you're adding the attachment view factory to the MessageListView.

With this, your app is ready to send and also preview custom location attachments. For the project, the action button for sending the location is on the options menu as shown in the image below.



<img src="/Users/harun/AndroidStudioProjects/StreamLocationSharing/images/app_ui.png" alt="app_ui" style="zoom:50%;" />

You'll use the menu options to send the user's current location from the app to Stream Android Chat SDK. Once you tap on the location icon at the top right, it sends a message with the text: "My Current location". 

<img src="/Users/harun/AndroidStudioProjects/StreamLocationSharing/images/location_attachment.png" alt="location_attachment" style="zoom:50%;" />



As seen from the image above, the attachment shows a map and <code>TextView</code>. The map shows the location of the coordinates sent as custom attachments.

## Conclusion

You've seen how easy it is to add a location as a custom attachment. You can now enrich your chat with location sharing. You can even go a step further to add features such as live location sharing or current location sharing as the Stream Android SDK supports custom attachments.

You can learn more about the Android SDK by checking out its [GitHub repository](https://github.com/GetStream/stream-chat-android), and by taking a look at [the documentation](https://getstream.io/chat/docs/android/?language=kotlin). You can also go through the [Message List View Custom Attachments](https://getstream.io/chat/docs/android/message_list_view/?language=kotlin&q=AttachmentViewFactory#customizations) sections that explain more about custom attachments.

You can get the full sample project with examples in this tutorial [here](https://github.com/wangerekaharun/StreamLocationSharing).

