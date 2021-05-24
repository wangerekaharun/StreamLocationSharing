/*
 * Copyright 2021 GradleBuildPlugins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.stream.locationsharing

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.client.api.models.QuerySort
import io.getstream.chat.android.client.channel.ChannelClient
import io.getstream.chat.android.client.models.*
import io.getstream.chat.android.livedata.ChatDomain
import io.stream.locationsharing.databinding.ActivityLocationSharingBinding
import io.stream.locationsharing.utils.locationFlow
import io.stream.locationsharing.utils.toast
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LocationSharingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationSharingBinding
    private var sentMessage = Message()
    private lateinit var channelClient: ChannelClient
    private var channelId = ""
    private val mFusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }


    @OptIn(InternalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_sharing)

        binding = ActivityLocationSharingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mFusedLocationClient.locationFlow().collect {
                    Log.d("Location", it.longitude.toString())

                }
            }
        }

        val client = ChatClient.Builder("b67pax5b2wdq", applicationContext).build()
        ChatDomain.Builder(client, applicationContext).build()

        val user = User(
            id = "tutorial-droid",
            extraData = mutableMapOf(
                "name" to "Stream Reactions",
                "image" to "https://bit.ly/2TIt8NR",
            ),
        )
        client.connectUser(
            user = user,
            token = BuildConfig.TOKEN
        ).enqueue()

        channelClient = client.channel(channelType = "messaging", channelId = "general")

        channelClient.create().enqueue { result ->
            if (result.isSuccess) {
                val newChannel: Channel = result.data()
                channelId = newChannel.cid
                Log.d("Channel", newChannel.name)
            } else {
                showSnackBar("Adding channels Failed")
            }
        }

        val request = QueryChannelsRequest(
            filter = Filters.and(
                Filters.eq("members", listOf("tutorial-droid")),
            ),
            offset = 0,
            limit = 10,
            querySort = QuerySort.desc("last_message_at")
        ).apply {
            watch = true
            state = true
        }

        client.queryChannels(request).enqueue { result ->
            if (result.isSuccess) {
                val channels: List<Channel> = result.data()
                Log.d("Channel", channels.toString())
            } else {
                showSnackBar("Querying channel Failed")
            }
        }

        val message = Message(text = "Sample message text")

        channelClient.sendMessage(message).enqueue { result ->
            if (result.isSuccess) {
                sentMessage = result.data()
            } else {
                showSnackBar("Adding message Failed")
            }
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