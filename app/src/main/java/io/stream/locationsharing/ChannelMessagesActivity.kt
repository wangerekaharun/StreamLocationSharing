package io.stream.locationsharing

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.Attachment
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.ui.message.input.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModel
import io.getstream.chat.android.ui.message.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.viewmodel.factory.MessageListViewModelFactory
import io.stream.locationsharing.databinding.ActivityChannelMessagesBinding
import io.stream.locationsharing.utils.locationFlow
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChannelMessagesActivity: AppCompatActivity() {
    private lateinit var binding: ActivityChannelMessagesBinding
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private var currentLocation = LatLng(0.0,0.0)
    private var message = Message()
    private var channelId: String = ""

    @OptIn(InternalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChannelMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                fusedLocationClient.locationFlow().collect {
                    currentLocation = LatLng(it.latitude, it.longitude)
                }
            }
        }


        channelId = checkNotNull(intent.getStringExtra("channelId")) {
            "Specifying a channel id is required when starting ChannelActivity"
        }

        val factory = MessageListViewModelFactory(channelId)
        val messageListHeaderViewModel: MessageListHeaderViewModel by viewModels { factory }
        val messageListViewModel: MessageListViewModel by viewModels { factory }
        val messageInputViewModel: MessageInputViewModel by viewModels { factory }

        binding.messageListView.setAttachmentViewFactory(LocationAttachmentViewFactory())


        // Step 2 - Bind the view and ViewModels, they are loosely coupled so it's easy to customize
        messageListHeaderViewModel.bindView(binding.messageListHeaderView, this)
        messageListViewModel.bindView(binding.messageListView, this)
        messageInputViewModel.bindView(binding.messageInputView, this)

        // Step 3 - Let both MessageListHeaderView and MessageInputView know when we open a thread
        // Note: the observe syntax used here requires Kotlin 1.4
        messageListViewModel.mode.observe(this) { mode ->
            when (mode) {
                is MessageListViewModel.Mode.Thread -> {
                    messageListHeaderViewModel.setActiveThread(mode.parentMessage)
                    messageInputViewModel.setActiveThread(mode.parentMessage)
                }
                is MessageListViewModel.Mode.Normal -> {
                    messageListHeaderViewModel.resetThread()
                    messageInputViewModel.resetThread()
                }
            }
        }

        // Step 4 - Let the message input know when we are editing a message
        binding.messageListView.setMessageEditHandler { message ->
            messageInputViewModel.editMessage.postValue(message)
        }

        // Step 5 - Handle navigate up state
        messageListViewModel.state.observe(this) { state ->
            if (state is MessageListViewModel.State.NavigateUp) {
                finish()
            }
        }

        // Step 6 - Handle back button behaviour correctly when you're in a thread
        val backHandler = {
            messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed)
        }
        binding.messageListHeaderView.setBackButtonClickListener(backHandler)
        onBackPressedDispatcher.addCallback(this) {
            backHandler()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_channel_messages_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionLocation -> {
                val attachment = Attachment(
                    type = "location",
                    extraData = mutableMapOf("latitude" to currentLocation.latitude, "longitude" to currentLocation.longitude),
                )

                message = Message(
                    cid = channelId,
                    text = "My current location",
                    attachments = mutableListOf(attachment),
                )

                ChatClient
                    .instance()
                    .channel(channelId)
                    .sendMessage(message).enqueue { result ->
                        if (result.isSuccess) {
                            showSnackBar("Your location has been sent")
                        } else {
                            showSnackBar("Sending location failed")
                        }

                    }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }
}