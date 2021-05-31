# Location Sharing With Custom Attachments 


Stream's Android SDK supports sending custom attachments with messages. In this tutorial, you'll learn how to send location as a custom attachment. You'll be learning this by building an app that shares your current location as an attachment to a message.

**Note:** This tutorial assumes you already know the basic knowledge of the Stream API. To get started, check out the [Android In-App Messaging Tutorial](https://getstream.io/tutorials/android-chat/), and take a look at the [Android SDK on GitHub](https://github.com/GetStream/stream-chat-android). To get started with sending custom attachments with the Stream Chat SDK check out the [Creating Custom Attachments on Android](https://getstream.io/blog/android-chat-custom-attachments/) tutorial.

## Getting Current Location

Before you send your attachment, first you'll need to get the current location of the user. The implementation for getting the current location is already set up.  You can see the implementation in the [sample project]((https://github.com/wangerekaharun/StreamLocationSharing)). 

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

Here, you're getting location from <code>FusedLocationProviderClient</code> as a <code>Flow</code>. You're using  the <code>locationFlow()</code> extension method.  You're also collecting the results i safe way using the <code>Lifecycle></code> methods.

You now have the user's current location. In the next section, you'll see how to add location coordinates as custom attachments.

**Note:** To be able to access location or load a map, you'll need to have your project on the Google Maps Platform console. From the console you can get an API Key for your app which enable your app to access all location and map functionalities. Read more about this on the [official documentation](https://developers.google.com/maps/documentation/android-sdk/start#create-project).

Once you have the API Key, you can add it on the <code>local.properties</code> file as:

```groovy
googleMapsKey="YOUR_API_KEY"
```

With this the project will be able to load the map and use all the map based functionalities.

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

1. Here, you're creating an attachment with the <code>location</code> key. You'll use this key later to retrieve this data. You're also passing the latitude and longitude from your location coordinates as extra data.
2. You're adding your location attachment to the message using the <code>attachments</code> property.


With this, you can send your message with this custom attachment.

Stream Android SDK renders preview for attachments like images and files. For custom attachments, you'll override the <code>AttachmentViewFactory</code> class. It has a method to create and render your custom view. You'll be seeing how to do that in the next section.

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
class LocationAttachmentViewFactory(
    private val lifecycleOwner: LifecycleOwner
): AttachmentViewFactory() {
    // 1
    override fun createAttachmentView(
        data: MessageListItem.MessageItem,
        listeners: MessageListListenerContainer,
        style: MessageListItemStyle,
        parent: ViewGroup
    ): View {
        // 2
        val location = data.message.attachments.find { it.type == "location" }
        return if (location != null) {
            val lat = location.extraData["latitude"] as Double
            val long = location.extraData["longitude"] as Double
            val latLng = LatLng(lat, long)
            // 3
          	createLocationView(parent, latLng)
        } else {
            super.createAttachmentView(data, listeners, style, parent)
        }
    }

    private fun createLocationView(parent: ViewGroup, location: LatLng): View {
        val binding = LocationAttachementViewBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        // 4
      	val mapView = binding.mapView
        mapView.onCreate(Bundle())
      	// 5
        mapView.getMapAsync { googleMap ->
            googleMap.setMinZoomPreference(18f)
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        }

        // 6
      	lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun destroyMapView(){
                mapView.onDestroy()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun startMapView(){
               mapView.onStart()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun resumeMapView(){
                mapView.onResume()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun stopMapView(){
                mapView.onStop()
            }
        })

        return binding.root
    }

}
```

Here's a breakdown of the code above:

1. This the method responsible for rendering your custom attachment UI. In this method, you only need to change the UI for attachments that have a location. The other attachments remain unchanged.
2. Here, you're getting the location data that you passed on your message using the <code>location</code>key that you defined earlier. 
3. You're calling the <code>createLocationView</code> which is responsible for inflating the view.
4.  Here you're initializing the <code>mapView</code>. 
5. You're calling the <code>getMapAsync()</code> . This method sets a callback object which is triggered when the GoogleMap instance is ready for use.  You're also updating the map with the zoom and updating the map to show the location you added to your attachment.
6. You're adding a <code>LifecycleObserver</code>. This is for calling the different <code>MapView</code> life cycle methods depending on the lifecycle state of <code>LocationAttachmentViewFactory</code> . For example you're supposed to destoy the <code>MapView</code> when the view has been destoyed.  You achieve this by calling <code>mapView.onDestroy()</code> when you receive the <code>ON_DESTROY</code> lifecycle event.

With the custom factory set, you now need to notify the <code>MessageListView</code> of the <code>LocationAttachmentViewFactory</code>. You do this as shown in the code below:

```kotlin
binding.messageListView.setAttachmentViewFactory(LocationAttachmentViewFactory(lifecycleOwner = this))
```

Here's you're adding the attachment view factory to the MessageListView. You also pass the activity context as the <code>lifecycleOwner</code> for the <code>LocationAttachmentViewFactory</code> . This hooks it with the life cycle of the activity.

With this, your app is ready to send and also preview custom location attachments. For the project, the action button for sending the location is on the options menu as shown in the image below.



![App Ui](https://github.com/wangerekaharun/StreamLocationSharing/blob/master/images/app_ui.png)

You'll use the menu options to send the user's current location from the app to Stream Android Chat SDK. Once you tap on the location icon at the top right, it sends a message with the text: "My Current location". 

![Location Attachment Custom View](https://github.com/wangerekaharun/StreamLocationSharing/blob/master/images/location_attachment.png)

As seen from the image above, the attachment shows a map and <code>TextView</code>. The map shows the location of the coordinates sent as custom attachments.

## Conclusion

You've seen how easy it is to add a location as a custom attachment. You can now enrich your chat with location sharing. You can add features such as live location sharing or current location sharing as the Stream Android SDK supports custom attachments.

You can learn more about the Android SDK by checking out its [GitHub repository](https://github.com/GetStream/stream-chat-android), and by taking a look at [the documentation](https://getstream.io/chat/docs/android/?language=kotlin). You can also go through the [Message List View Custom Attachments](https://getstream.io/chat/docs/android/message_list_view/?language=kotlin&q=AttachmentViewFactory#customizations) sections that explain more about custom attachments.

You can get the full sample project with examples in this tutorial [here](https://github.com/wangerekaharun/StreamLocationSharing).

